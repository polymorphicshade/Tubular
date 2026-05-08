package org.schabi.newpipe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.schabi.newpipe.BaseFragment;
import org.schabi.newpipe.R;
import org.schabi.newpipe.util.NavigationHelper;

/**
 * The "You" tab — a YouTube-style library hub with shortcuts to History,
 * Playlists, Subscriptions, Downloads, Settings and About.
 */
public class LibraryFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.library_item_history).setOnClickListener(v ->
                NavigationHelper.openStatisticFragment(getParentFragmentManager()));
        view.findViewById(R.id.library_item_playlists).setOnClickListener(v ->
                NavigationHelper.openBookmarksFragment(getParentFragmentManager()));
        view.findViewById(R.id.library_item_subscriptions).setOnClickListener(v ->
                NavigationHelper.openSubscriptionFragment(getParentFragmentManager()));
        view.findViewById(R.id.library_item_downloads).setOnClickListener(v ->
                NavigationHelper.openDownloads(requireActivity()));
        view.findViewById(R.id.library_item_settings).setOnClickListener(v ->
                NavigationHelper.openSettings(requireContext()));
        view.findViewById(R.id.library_item_about).setOnClickListener(v ->
                NavigationHelper.openAbout(requireContext()));
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}
