package org.schabi.newpipe.fragments.list.shorts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;

import org.schabi.newpipe.BaseFragment;
import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.kiosk.KioskInfo;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.VideoStream;
import org.schabi.newpipe.player.helper.PlayerHolder;
import org.schabi.newpipe.player.notification.NotificationConstants;
import org.schabi.newpipe.util.ExtractorHelper;
import org.schabi.newpipe.util.ServiceHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * YouTube-style Shorts: a vertical, full-screen swipeable feed with inline
 * autoplay. Pulls the current service's default kiosk and filters to videos
 * ≤60s.
 */
public class ShortsFragment extends BaseFragment {

    private static final long SHORTS_MAX_DURATION_SEC = 60;

    private ViewPager2 pager;
    private ProgressBar loading;
    private TextView empty;

    private ShortsAdapter adapter;
    private ExoPlayer exoPlayer;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Disposable currentLoad;
    private int currentPosition = -1;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shorts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = view.findViewById(R.id.shorts_pager);
        loading = view.findViewById(R.id.shorts_loading);
        empty = view.findViewById(R.id.shorts_empty);

        // If the main bottom-sheet player is playing, pause it so audio
        // focus doesn't fight between two players.
        if (PlayerHolder.getInstance().isPlaying()) {
            requireContext().sendBroadcast(
                    new Intent(NotificationConstants.ACTION_PLAY_PAUSE)
                            .setPackage(requireContext().getPackageName()));
        }

        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        exoPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .build(), /* handleAudioFocus= */ true);
        exoPlayer.setPlayWhenReady(true);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(final int position) {
                playPosition(position);
            }
        });

        loadShorts();
    }

    private void loadShorts() {
        loading.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);

        final int serviceId = ServiceHelper.getSelectedServiceId(requireContext());
        final StreamingService service;
        try {
            service = NewPipe.getService(serviceId);
        } catch (final Exception e) {
            showEmpty(getString(R.string.general_error));
            return;
        }

        final String kioskId;
        try {
            kioskId = service.getKioskList().getDefaultKioskId();
        } catch (final Exception e) {
            showEmpty(getString(R.string.general_error));
            return;
        }

        final String kioskUrl = getKioskUrl(service, kioskId);
        disposables.add(ExtractorHelper.getKioskInfo(serviceId, kioskUrl, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onKioskLoaded, throwable ->
                        showEmpty(getString(R.string.general_error))));
    }

    private String getKioskUrl(final StreamingService service, final String kioskId) {
        try {
            return service.getKioskList().getListLinkHandlerFactoryByType(kioskId)
                    .fromId(kioskId).getUrl();
        } catch (final Exception e) {
            return "";
        }
    }

    private void onKioskLoaded(final KioskInfo info) {
        final List<StreamInfoItem> shorts = new ArrayList<>();
        for (final StreamInfoItem item : info.getRelatedItems()) {
            if (item.getDuration() > 0 && item.getDuration() <= SHORTS_MAX_DURATION_SEC) {
                shorts.add(item);
            }
        }

        loading.setVisibility(View.GONE);

        if (shorts.isEmpty()) {
            showEmpty(getString(R.string.no_videos));
            return;
        }

        adapter = new ShortsAdapter(this, shorts, exoPlayer);
        pager.setAdapter(adapter);
        // play the first one automatically
        pager.post(() -> playPosition(0));
    }

    private void playPosition(final int position) {
        if (adapter == null || position < 0 || position >= adapter.getItemCount()) {
            return;
        }
        currentPosition = position;
        if (currentLoad != null) {
            currentLoad.dispose();
        }

        adapter.bindPlayerToHolder(position);

        final StreamInfoItem item = adapter.getItem(position);
        currentLoad = ExtractorHelper.getStreamInfo(requireContext(),
                item.getServiceId(), item.getUrl(), false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(streamInfo -> {
                    if (currentPosition != position) {
                        return; // user moved on
                    }
                    final MediaItem mediaItem = pickMediaItem(streamInfo);
                    if (mediaItem == null) {
                        return;
                    }
                    exoPlayer.setMediaItem(mediaItem);
                    exoPlayer.prepare();
                    exoPlayer.setPlayWhenReady(true);
                }, throwable -> {
                    // Fail silently — user can swipe to next short.
                });
        disposables.add(currentLoad);
    }

    @Nullable
    private MediaItem pickMediaItem(final StreamInfo info) {
        // Prefer a HLS/DASH manifest when available — most YouTube videos only
        // expose adaptive streams now and progressive fallbacks are rare.
        if (info.getHlsUrl() != null && !info.getHlsUrl().isEmpty()) {
            return new MediaItem.Builder()
                    .setUri(Uri.parse(info.getHlsUrl()))
                    .setMimeType(com.google.android.exoplayer2.util.MimeTypes
                            .APPLICATION_M3U8)
                    .build();
        }
        if (info.getDashMpdUrl() != null && !info.getDashMpdUrl().isEmpty()) {
            return new MediaItem.Builder()
                    .setUri(Uri.parse(info.getDashMpdUrl()))
                    .setMimeType(com.google.android.exoplayer2.util.MimeTypes
                            .APPLICATION_MPD)
                    .build();
        }

        final List<VideoStream> streams = info.getVideoStreams();
        if (streams == null || streams.isEmpty()) {
            return null;
        }
        // Pick a progressive stream — preferring ~480p for fast load on mobile.
        VideoStream chosen = null;
        for (final VideoStream s : streams) {
            if (s.getContent() == null || s.getContent().isEmpty()) {
                continue;
            }
            if (chosen == null) {
                chosen = s;
            }
            if (s.getResolution() != null && s.getResolution().contains("480")) {
                chosen = s;
                break;
            }
        }
        return chosen == null ? null : MediaItem.fromUri(Uri.parse(chosen.getContent()));
    }

    private void showEmpty(final String message) {
        loading.setVisibility(View.GONE);
        empty.setText(message);
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer != null && currentPosition >= 0) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
