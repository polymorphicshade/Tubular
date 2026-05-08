package org.schabi.newpipe.fragments.list.shorts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.util.NavigationHelper;
import org.schabi.newpipe.util.image.CoilHelper;

import java.util.List;

/**
 * Adapter for the vertical Shorts pager. Each page shows a thumbnail plus a
 * PlayerView that the host fragment binds the shared ExoPlayer to whenever
 * that page becomes active.
 */
public class ShortsAdapter extends RecyclerView.Adapter<ShortsAdapter.ShortsViewHolder> {

    private final Fragment hostFragment;
    private final List<StreamInfoItem> items;
    private final ExoPlayer player;

    private RecyclerView attachedRecycler;
    private ShortsViewHolder activeHolder;

    public ShortsAdapter(final Fragment hostFragment,
                         final List<StreamInfoItem> items,
                         final ExoPlayer player) {
        this.hostFragment = hostFragment;
        this.items = items;
        this.player = player;
    }

    public StreamInfoItem getItem(final int position) {
        return items.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        attachedRecycler = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        attachedRecycler = null;
        activeHolder = null;
    }

    /**
     * Move the shared ExoPlayer's surface to the holder for {@code position}.
     * Called by the fragment on every page change.
     *
     * @param position adapter position whose PlayerView should host the player
     */
    public void bindPlayerToHolder(final int position) {
        if (attachedRecycler == null) {
            return;
        }
        final RecyclerView.ViewHolder vh =
                attachedRecycler.findViewHolderForAdapterPosition(position);
        if (!(vh instanceof ShortsViewHolder)) {
            return;
        }
        final ShortsViewHolder target = (ShortsViewHolder) vh;
        if (activeHolder != null && activeHolder != target) {
            activeHolder.playerView.setPlayer(null);
            activeHolder.playerView.setVisibility(View.GONE);
            activeHolder.thumbnail.setVisibility(View.VISIBLE);
        }
        target.playerView.setPlayer(player);
        target.playerView.setVisibility(View.VISIBLE);
        // keep thumbnail underneath as a placeholder while the stream loads
        activeHolder = target;
    }

    @NonNull
    @Override
    public ShortsViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                               final int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_shorts_item, parent, false);
        return new ShortsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShortsViewHolder holder, final int position) {
        final StreamInfoItem item = items.get(position);

        holder.title.setText(item.getName());
        holder.uploader.setText(item.getUploaderName());
        CoilHelper.INSTANCE.loadThumbnail(holder.thumbnail, item.getThumbnails());

        holder.playerView.setPlayer(null);
        holder.playerView.setVisibility(View.GONE);
        holder.thumbnail.setVisibility(View.VISIBLE);

        // Tap-to-pause on the player surface area, falling through to opening
        // the regular detail when the user long-presses.
        holder.playerView.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.play();
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            NavigationHelper.openVideoDetailFragment(
                    v.getContext(),
                    hostFragment.getParentFragmentManager(),
                    item.getServiceId(),
                    item.getUrl(),
                    item.getName(),
                    null,
                    false);
            return true;
        });
    }

    @Override
    public void onViewRecycled(@NonNull final ShortsViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder == activeHolder) {
            activeHolder = null;
        }
        holder.playerView.setPlayer(null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ShortsViewHolder extends RecyclerView.ViewHolder {
        final ImageView thumbnail;
        final PlayerView playerView;
        final TextView title;
        final TextView uploader;

        ShortsViewHolder(@NonNull final View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.shorts_thumbnail);
            playerView = itemView.findViewById(R.id.shorts_player_view);
            title = itemView.findViewById(R.id.shorts_title);
            uploader = itemView.findViewById(R.id.shorts_uploader);
        }
    }
}
