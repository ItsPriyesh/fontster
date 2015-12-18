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
import android.widget.Button;
import android.widget.ProgressBar;

import com.chromium.fontinstaller.Injector;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontDownloader;
import com.chromium.fontinstaller.core.FontsterPreferences;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.main.MainActivity;
import com.chromium.fontinstaller.util.ViewUtils;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.chromium.fontinstaller.core.FontsterPreferences.Key;

public class FontListFragment extends Fragment {

  @Bind(R.id.font_list_view)
  RecyclerView mRecyclerView;

  @Bind(R.id.download_progress)
  ProgressBar mDownloadProgress;

  @Bind(R.id.error_container)
  ViewGroup mErrorContainer;

  @Bind(R.id.retry)
  Button mRetryButton;

  @Inject
  FontsterPreferences mPreferences;

  private Activity mActivity;
  private List<String> mFontList;
  private FontListAdapter mListAdapter;
  private ProgressDialog mProgressDialog;

  public FontListFragment() { }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Injector.get().inject(this);

    final View view = inflater.inflate(R.layout.fragment_font_list, container, false);
    ButterKnife.bind(this, view);

    mActivity = getActivity();
    ((MainActivity) mActivity).setToolbarTitle(getString(R.string.app_name));

    mFontList = Arrays.asList(getResources().getStringArray(R.array.font_list));

    RecyclerView.LayoutManager listManager = new LinearLayoutManager(mActivity);
    mRecyclerView.setLayoutManager(listManager);

    mRetryButton.setOnClickListener(v -> downloadFontList());

    if (mPreferences.getBoolean(Key.ENABLE_TRUEFONT)) downloadFontList();
    else setupRecyclerViewAdapter(false);

    return view;
  }

  private void setupRecyclerViewAdapter(boolean enableTrueFont) {
    mRecyclerView.setVisibility(View.VISIBLE);

    mListAdapter = new FontListAdapter(mActivity, new ArrayList<>(mFontList), enableTrueFont);

    mRecyclerView.setAdapter(mListAdapter);
    mRecyclerView.addItemDecoration(buildHeaderDecor());
  }

  private void dismissProgressDialog() {
    if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
  }

  private void downloadFontList() {
    final boolean previewsCached = mPreferences.getBoolean(Key.TRUEFONTS_CACHED);

    if (!previewsCached) {
      mProgressDialog = new ProgressDialog(mActivity);
      mProgressDialog.setMessage(mActivity.getString(R.string.font_list_download_progress));
      mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      mProgressDialog.setMax(mFontList.size());
      mProgressDialog.show();
    }

    if (mErrorContainer.getVisibility() == View.VISIBLE) mErrorContainer.setVisibility(View.GONE);
    mDownloadProgress.setVisibility(View.VISIBLE);

    final List<FontPackage> fontPackages = new ArrayList<>(mFontList.size());
    for (String fontName : mFontList) fontPackages.add(new FontPackage(fontName));

    FontDownloader.downloadStyleFromPackages(fontPackages, Style.REGULAR)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            font -> {
              if (mProgressDialog != null) mProgressDialog.incrementProgressBy(1);
            },
            this::handleDownloadFailure,
            this::handleDownloadSuccess);
  }

  private void handleDownloadSuccess() {
    mPreferences.setBoolean(Key.TRUEFONTS_CACHED, true);

    dismissProgressDialog();
    ViewUtils.animSlideUp(mDownloadProgress, getActivity());
    new Handler().postDelayed(() -> {
      mDownloadProgress.setVisibility(View.INVISIBLE);
      setupRecyclerViewAdapter(true);
    }, 400);
  }

  private void handleDownloadFailure(Throwable error) {
    dismissProgressDialog();
    error.printStackTrace();
    Timber.e("Download failed: " + error.getMessage());
    ViewUtils.animSlideUp(mDownloadProgress, getActivity());
    new Handler().postDelayed(() -> {
      mDownloadProgress.setVisibility(View.INVISIBLE);
      ViewUtils.animSlideInBottom(mErrorContainer, getActivity());
      mErrorContainer.setVisibility(View.VISIBLE);
    }, 400);
  }

  private StickyHeadersItemDecoration buildHeaderDecor() {
    return new StickyHeadersBuilder()
        .setAdapter(mListAdapter)
        .setRecyclerView(mRecyclerView)
        .setStickyHeadersAdapter(new FontListHeaderAdapter(mFontList), true)
        .build();
  }
}
