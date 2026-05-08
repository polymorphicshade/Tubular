package org.schabi.newpipe.fragments.list.shorts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.util.NavigationHelper;
import org.schabi.newpipe.util.image.CoilHelper;

import java.util.List;

/**
 * Adapter for the vertical Shorts pager. Each page shows a thumbnail with the
 * action rail and bottom info overlay; tapping the page opens the regular
 * video detail.
 */
public class ShortsAdapter extends RecyclerView.Adapter<ShortsAdapter.ShortsViewHolder> {

    private final Fragment hostFragment;
    private final List<StreamInfoItem> items;

    public ShortsAdapter(final Fragment hostFragment, final List<StreamInfoItem> items) {
        this.hostFragment = hostFragment;
        this.items = items;
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

        holder.itemView.setOnClickListener(v ->
                NavigationHelper.openVideoDetailFragment(
                        v.getContext(),
                        hostFragment.getParentFragmentManager(),
                        item.getServiceId(),
                        item.getUrl(),
                        item.getName(),
                        null,
                        false));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ShortsViewHolder extends RecyclerView.ViewHolder {
        final ImageView thumbnail;
        final TextView title;
        final TextView uploader;

        ShortsViewHolder(@NonNull final View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.shorts_thumbnail);
            title = itemView.findViewById(R.id.shorts_title);
            uploader = itemView.findViewById(R.id.shorts_uploader);
        }
    }
}
