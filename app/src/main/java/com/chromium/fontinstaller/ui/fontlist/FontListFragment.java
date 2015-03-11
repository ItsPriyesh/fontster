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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontDownloader;
import com.chromium.fontinstaller.events.DownloadCompleteEvent;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.util.PreferencesManager;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class FontListFragment extends Fragment {

    @InjectView(R.id.font_list_view)
    RecyclerView recyclerView;

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

        prefs = PreferencesManager.getInstance(activity);

        populateFontList();

        listManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(listManager);

        if (prefs.getBoolean(PreferencesManager.KEY_ENABLE_TRUEFONT)) downloadFontList();
        else setupRecyclerViewAdapter(false);

        return view;
    }

    private void setupRecyclerViewAdapter(boolean enableTrueFont) {
        listAdapter = new FontListAdapter(activity, fontList, enableTrueFont);

        recyclerView.setAdapter(listAdapter);
        recyclerView.addItemDecoration(buildHeaderDecor());
    }

    private void downloadFontList() {
        List<FontPackage> fontPackages = new ArrayList<>(fontList.size());
        for (String fontName : fontList) fontPackages.add(new FontPackage(fontName));

        new FontDownloader(activity).downloadFromList(fontPackages, Style.REGULAR);
    }

    @Subscribe
    public void onDownloadFontListComplete(DownloadCompleteEvent event) {
        if (event.wasSuccessful()) setupRecyclerViewAdapter(true);
        else Timber.i("FAILED TO GET ALL FONTS");
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
