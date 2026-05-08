package org.schabi.newpipe.fragments.list.shorts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import org.schabi.newpipe.BaseFragment;
import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.kiosk.KioskInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.util.ExtractorHelper;
import org.schabi.newpipe.util.ServiceHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * YouTube-style Shorts: a vertical, full-screen swipeable feed.
 *
 * MVP implementation: pulls the current service's Trending kiosk and filters
 * to videos ≤60s (the YouTube Shorts duration cap). Tapping a short opens
 * the standard video detail / player rather than playing inline — inline
 * playback requires deeper player integration than this pass covers.
 */
public class ShortsFragment extends BaseFragment {

    private static final long SHORTS_MAX_DURATION_SEC = 60;

    private ViewPager2 pager;
    private ProgressBar loading;
    private TextView empty;
    private final CompositeDisposable disposables = new CompositeDisposable();

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

        disposables.add(ExtractorHelper.getKioskInfo(serviceId, getKioskUrl(service, kioskId), false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onKioskLoaded, throwable -> {
                    showEmpty(getString(R.string.general_error));
                }));
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

        pager.setAdapter(new ShortsAdapter(this, shorts));
    }

    private void showEmpty(final String message) {
        loading.setVisibility(View.GONE);
        empty.setText(message);
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}
