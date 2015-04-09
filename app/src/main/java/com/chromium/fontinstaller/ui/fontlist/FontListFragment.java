/*
 * Copyright 2015 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chromium.fontinstaller.ui.fontlist;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontDownloader;
import com.chromium.fontinstaller.events.DownloadCompleteEvent;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.main.MainActivity;
import com.chromium.fontinstaller.util.PreferencesManager;
import com.chromium.fontinstaller.util.ViewUtils;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FontListFragment extends Fragment {

    @InjectView(R.id.font_list_view)
    RecyclerView recyclerView;

    @InjectView(R.id.download_progress)
    ProgressBar downloadProgress;

    @InjectView(R.id.error_container)
    ViewGroup errorContainer;

    private FontListAdapter listAdapter;
    private RecyclerView.LayoutManager listManager;
    private ArrayList<String> fontList = new ArrayList<>();
    private Activity activity;
    private PreferencesManager prefs;

    public FontListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_font_list, container, false);
        ButterKnife.inject(this, view);

        activity = getActivity();
        ((MainActivity) activity).setToolbarTitle("Fontster");

        prefs = PreferencesManager.getInstance(activity);

        populateFontList();

        listManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(listManager);

        if (prefs.getBoolean(PreferencesManager.KEY_ENABLE_TRUEFONT)) downloadFontList();
        else setupRecyclerViewAdapter(false);

        return view;
    }

    private void setupRecyclerViewAdapter(boolean enableTrueFont) {
        recyclerView.setVisibility(View.VISIBLE);

        listAdapter = new FontListAdapter(activity, fontList, enableTrueFont);

        recyclerView.setAdapter(listAdapter);
        recyclerView.addItemDecoration(buildHeaderDecor());
    }

    private void downloadFontList() {
        if (errorContainer.getVisibility() == View.VISIBLE) {
            errorContainer.setVisibility(View.GONE);
        }
        downloadProgress.setVisibility(View.VISIBLE);

        List<FontPackage> fontPackages = new ArrayList<>(fontList.size());
        for (String fontName : fontList) fontPackages.add(new FontPackage(fontName));

        new FontDownloader(activity).downloadFromList(fontPackages, Style.REGULAR);
    }

    private void handleDownloadSuccess() {
        ViewUtils.animSlideUp(downloadProgress, getActivity());
        new Handler().postDelayed(() -> {
            downloadProgress.setVisibility(View.INVISIBLE);

            ViewUtils.animFadeIn(recyclerView, getActivity());
            setupRecyclerViewAdapter(true);
        }, 400);
    }

    private void handleDownloadFailure() {
        ViewUtils.animSlideUp(downloadProgress, getActivity());
        new Handler().postDelayed(() -> {
            downloadProgress.setVisibility(View.INVISIBLE);

            ViewUtils.animSlideInBottom(errorContainer, getActivity());
            errorContainer.setVisibility(View.VISIBLE);
        }, 400);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.retry)
    public void retryButtonClicked() {
        downloadFontList();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDownloadFontListComplete(DownloadCompleteEvent event) {
        if (event.wasSuccessful()) handleDownloadSuccess();
        else handleDownloadFailure();
    }

    private StickyHeadersItemDecoration buildHeaderDecor() {
        return new StickyHeadersBuilder()
                .setAdapter(listAdapter)
                .setRecyclerView(recyclerView)
                .setStickyHeadersAdapter(new FontListHeaderAdapter(fontList), true)
                .build();
    }

    private void populateFontList() {
        try {
            Scanner scanner = new Scanner(activity.getAssets().open("fonts"));
            while (scanner.hasNextLine()) {
                fontList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

}
