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
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontDownloader;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.main.MainActivity;
import com.chromium.fontinstaller.util.PreferencesManager;
import com.chromium.fontinstaller.util.ViewUtils;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.chromium.fontinstaller.util.PreferencesManager.Keys;

public class FontListFragment extends Fragment {

    @Bind(R.id.font_list_view)
    RecyclerView recyclerView;

    @Bind(R.id.download_progress)
    ProgressBar downloadProgress;

    @Bind(R.id.error_container)
    ViewGroup errorContainer;

    private FontListAdapter listAdapter;
    private List<String> fontList;
    private Activity activity;
    private ProgressDialog progressDialog;
    private PreferencesManager preferences;

    public FontListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_font_list, container, false);
        ButterKnife.bind(this, view);

        activity = getActivity();
        ((MainActivity) activity).setToolbarTitle("Fontster");

        preferences = PreferencesManager.getInstance(activity);

        fontList = Arrays.asList(getResources().getStringArray(R.array.font_list));

        RecyclerView.LayoutManager listManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(listManager);

        if (preferences.getBoolean(Keys.KEY_ENABLE_TRUEFONT)) downloadFontList();
        else setupRecyclerViewAdapter(false);

        return view;
    }

    private void setupRecyclerViewAdapter(boolean enableTrueFont) {
        recyclerView.setVisibility(View.VISIBLE);

        listAdapter = new FontListAdapter(activity, new ArrayList<>(fontList), enableTrueFont);

        recyclerView.setAdapter(listAdapter);
        recyclerView.addItemDecoration(buildHeaderDecor());
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void downloadFontList() {
        final boolean previewsCached = preferences.getBoolean(Keys.KEY_TRUEFONTS_CACHED);

        if (!previewsCached) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Downloading previews");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(fontList.size());
            progressDialog.show();
        }

        if (errorContainer.getVisibility() == View.VISIBLE) errorContainer.setVisibility(View.GONE);
        downloadProgress.setVisibility(View.VISIBLE);

        final List<FontPackage> fontPackages = new ArrayList<>(fontList.size());
        for (String fontName : fontList) fontPackages.add(new FontPackage(fontName));

        Observable.from(fontPackages)
                .flatMap(fp -> FontDownloader.downloadStyledFonts(fp, activity, Style.REGULAR))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        font -> progressDialog.incrementProgressBy(1),
                        this::handleDownloadFailure,
                        this::handleDownloadSuccess);
    }

    private void handleDownloadSuccess() {
        preferences.setBoolean(Keys.KEY_TRUEFONTS_CACHED, true);

        dismissProgressDialog();
        ViewUtils.animSlideUp(downloadProgress, getActivity());
        new Handler().postDelayed(() -> {
            downloadProgress.setVisibility(View.INVISIBLE);
            setupRecyclerViewAdapter(true);
        }, 400);
    }

    private void handleDownloadFailure(Throwable error) {
        dismissProgressDialog();
        Timber.e("Download failed: " + error.getMessage());
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

    private StickyHeadersItemDecoration buildHeaderDecor() {
        return new StickyHeadersBuilder()
                .setAdapter(listAdapter)
                .setRecyclerView(recyclerView)
                .setStickyHeadersAdapter(new FontListHeaderAdapter(fontList), true)
                .build();
    }
}
