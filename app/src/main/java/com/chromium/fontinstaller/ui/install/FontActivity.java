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

package com.chromium.fontinstaller.ui.install;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontDownloader;
import com.chromium.fontinstaller.core.FontInstaller;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.util.RebootDialog;
import com.crashlytics.android.Crashlytics;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.chromium.fontinstaller.util.ViewUtils.animGrowFromCenter;
import static com.chromium.fontinstaller.util.ViewUtils.animShrinkToCenter;
import static com.chromium.fontinstaller.util.ViewUtils.animSlideInBottom;
import static com.chromium.fontinstaller.util.ViewUtils.animSlideUp;
import static com.chromium.fontinstaller.util.ViewUtils.reveal;
import static com.chromium.fontinstaller.util.ViewUtils.snackbar;

public final class FontActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    @Bind(R.id.font_name)
    TextView mFontTitle;

    @Bind(R.id.install_fab)
    FloatingActionButton mInstallButton;

    @Bind(R.id.download_progress)
    ProgressBar mDownloadProgress;

    @Bind(R.id.preview_pager)
    ViewPager mPreviewPager;

    @Bind(R.id.sliding_tabs)
    TabLayout mTabLayout;

    @Bind(R.id.error_container)
    ViewGroup mErrorContainer;

    private int mCurrentPage = 0;
    private FontPackage mFontPackage;
    private PreviewFragment[] mPreviewPages = new PreviewFragment[3];
    private boolean mFragmentsInitialized = false;
    private ProgressDialog mProgressDialog;

    static final SparseArray<Style> PREVIEW_STYLES = new SparseArray<Style>() {{
        put(0, Style.REGULAR);
        put(1, Style.BOLD);
        put(2, Style.ITALIC);
    }};

    public static final String FONT_NAME_KEY = "font_name";

    public static Intent getLaunchIntent(final Context context, final String fontName) {
        final Intent intent = new Intent(context, FontActivity.class);
        intent.putExtra(FontActivity.FONT_NAME_KEY, fontName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);
        disableToolbarElevation();
        showToolbarBackButton();
        setToolbarTitle("");

        final String fontName = getIntent().getStringExtra(FONT_NAME_KEY);
        logEvent("Viewing " + fontName);

        mFontPackage = new FontPackage(fontName);
        startDownload();

        mFontTitle.setText(fontName);
    }

    private void initializeFragments() {
        for (int i = 0; i < mPreviewPages.length; i++) {
            mPreviewPages[i] = PreviewFragment.newInstance(mFontPackage, PREVIEW_STYLES.get(i));
        }

        mFragmentsInitialized = true;
    }

    private void startDownload() {
        if (isVisible(mErrorContainer)) hide(mErrorContainer);
        show(mDownloadProgress);

        FontDownloader.downloadStyledFonts(mFontPackage, Style.REGULAR, Style.BOLD, Style.ITALIC)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        font -> Timber.i("Got font: " + font.getName()),
                        this::handleFailedDownload,
                        this::setupPager);
    }

    private void handleFailedDownload(Throwable error) {
        Crashlytics.logException(error);
        Timber.e("Download failed: " + error.getMessage());
        animSlideUp(mDownloadProgress, this);

        delay(() -> {
            hideGone(mDownloadProgress);

            animSlideInBottom(mErrorContainer, this);
            show(mErrorContainer);
        }, 400);
    }

    private void handleFailedInstall(Throwable error) {
        Crashlytics.logException(error);
        delay(() -> {
            Timber.e("Install failed: " + error.getMessage());
            snackbar(R.string.font_activity_install_failed, findViewById(R.id.bottom_bar));
            mProgressDialog.dismiss();
            animGrowFromCenter(mInstallButton, this);
            show(mInstallButton);
        }, 500);
    }

    private void setupPager() {
        initializeFragments();
        PreviewPagerAdapter pagerAdapter = new PreviewPagerAdapter(getSupportFragmentManager());
        mPreviewPager.setOffscreenPageLimit(2);
        mPreviewPager.setAdapter(pagerAdapter);
        mTabLayout.setOnTabSelectedListener(this);
        mTabLayout.setupWithViewPager(mPreviewPager);
        mTabLayout.setSelectedTabIndicatorColor(ActivityCompat.getColor(this, android.R.color.white));

        animateViews();
    }

    private void animateViews() {
        animShrinkToCenter(mDownloadProgress, this);

        delay(() -> {
            hideGone(mDownloadProgress);
            animSlideInBottom(mTabLayout, this);
            show(mTabLayout);
            reveal(this, mPreviewPager, mInstallButton, R.color.primary_accent);
            animGrowFromCenter(mInstallButton, this);
            show(mInstallButton);
        }, 400);
    }

    private void startInstall() {
        logEvent("Started install of " + mFontPackage.getName());
        mProgressDialog = ProgressDialog
                .show(this, null, getString(R.string.font_activity_install_progress), true, false);

        FontDownloader.downloadAllFonts(mFontPackage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .flatMap(v -> FontInstaller.install(mFontPackage, this))
                .subscribe(
                        next -> {},
                        error -> {
                            logEvent("Install of " + mFontPackage.getName() + " failed");
                            if (error instanceof FontDownloader.DownloadException)
                                handleFailedDownload(error.getCause());
                            else if (error instanceof FontInstaller.InstallException)
                                handleFailedInstall(error);
                        },
                        () -> {
                            logEvent("Install of " + mFontPackage.getName() + " succeeded");
                            onInstallComplete();
                        });
    }

    private Style getCurrentPageStyle() {
        return PREVIEW_STYLES.get(mCurrentPage);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.install_fab)
    public void installButtonClicked() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.font_activity_confirm_install)
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.dismiss())
                .setPositiveButton(R.string.yes, (dialog, id) -> startInstall())
                .create().show();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.retry)
    public void retryButtonClicked() {
        startDownload();
    }

    public void onInstallComplete() {
        mProgressDialog.dismiss();
        delay(() -> {
            mInstallButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_white));

            animGrowFromCenter(mInstallButton, this);
            show(mInstallButton);

            delay(() -> {
                if (!this.isFinishing()) new RebootDialog(this);
            }, 400);
        }, 400);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_font, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_case:
                toggleCase();
                return true;
            case R.id.try_font:
                showTryFontDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTryFontDialog() {
        if (mFragmentsInitialized) {
            new TryFontDialog(mFontPackage, getCurrentPageStyle(),
                    this::tryFontCallback, this).show();
        }
    }

    private void tryFontCallback(String input) {
        if (input.equals("") || input.isEmpty()) return;
        for (PreviewFragment fragment : mPreviewPages) {
            fragment.setPreviewText(input);
        }
    }

    private void toggleCase() {
        if (mFragmentsInitialized)
            for (PreviewFragment fragment : mPreviewPages)
                fragment.toggleCase();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mCurrentPage = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    private final class PreviewPagerAdapter extends FragmentPagerAdapter {

        private final String[] tabTitles = {
                getString(R.string.font_activity_tab_regular),
                getString(R.string.font_activity_tab_bold),
                getString(R.string.font_activity_tab_italic)
        };

        public PreviewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PreviewFragment fragment = (PreviewFragment) super.instantiateItem(container, position);
            mPreviewPages[position] = fragment;
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            return mPreviewPages[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return mPreviewPages.length;
        }
    }
}
