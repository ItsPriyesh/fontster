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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.chromium.fontinstaller.util.AlertUtils;
import com.crashlytics.android.Crashlytics;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.chromium.fontinstaller.util.ViewUtils.animGrowFromCenter;
import static com.chromium.fontinstaller.util.ViewUtils.animSlideInBottom;
import static com.chromium.fontinstaller.util.ViewUtils.animSlideUp;
import static com.chromium.fontinstaller.util.ViewUtils.animShrinkToCenter;
import static com.chromium.fontinstaller.util.ViewUtils.reveal;
import static com.chromium.fontinstaller.util.ViewUtils.snackbar;

public class FontActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    @Bind(R.id.font_name)
    TextView fontTitle;

    @Bind(R.id.install_fab)
    FloatingActionButton installButton;

    @Bind(R.id.download_progress)
    ProgressBar downloadProgress;

    @Bind(R.id.preview_pager)
    ViewPager previewPager;

    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;

    @Bind(R.id.error_container)
    ViewGroup errorContainer;

    private int currentPage = 0;
    private FontPackage fontPackage;
    private PreviewFragment[] previewPages = new PreviewFragment[3];
    private boolean fragmentsInitialized = false;
    private ProgressDialog progressDialog;

    public static final String FONT_NAME = "font_name";

    public static Intent getLaunchIntent(final Context context, final String fontName) {
        final Intent intent = new Intent(context, FontActivity.class);
        intent.putExtra(FontActivity.FONT_NAME, fontName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);
        disableToolbarElevation();
        showToolbarBackButton();
        setToolbarTitle("");

        final String fontName = getIntent().getStringExtra(FONT_NAME);

        fontPackage = new FontPackage(fontName);
        startDownload();

        fontTitle.setText(fontName);
    }

    private void initializeFragments() {
        PreviewFragment regularFragment = PreviewFragment.newInstance(fontPackage, Style.REGULAR);
        PreviewFragment boldFragment = PreviewFragment.newInstance(fontPackage, Style.BOLD);
        PreviewFragment italicFragment = PreviewFragment.newInstance(fontPackage, Style.ITALIC);

        previewPages[0] = regularFragment;
        previewPages[1] = boldFragment;
        previewPages[2] = italicFragment;

        fragmentsInitialized = true;
    }

    private void startDownload() {
        if (isVisible(errorContainer)) hide(errorContainer);
        show(downloadProgress);

        FontDownloader.downloadStyledFonts(fontPackage, Style.REGULAR, Style.BOLD, Style.ITALIC)
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
        animSlideUp(downloadProgress, this);

        delay(() -> {
            hideGone(downloadProgress);

            animSlideInBottom(errorContainer, this);
            show(errorContainer);
        }, 400);
    }

    private void handleFailedInstall(Throwable error) {
        Crashlytics.logException(error);
        delay(() -> {
            Timber.e("Install failed: " + error.getMessage());
            snackbar(R.string.font_activity_install_failed, findViewById(R.id.bottom_bar));
            progressDialog.dismiss();
            animGrowFromCenter(installButton, this);
            show(installButton);
        }, 500);
    }

    private void setupPager() {
        initializeFragments();
        PreviewPagerAdapter pagerAdapter = new PreviewPagerAdapter(getSupportFragmentManager());
        previewPager.setOffscreenPageLimit(2);
        previewPager.setAdapter(pagerAdapter);
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(previewPager);
        tabLayout.setSelectedTabIndicatorColor(getColor(android.R.color.white));

        animateViews();
    }

    private void animateViews() {
        animShrinkToCenter(downloadProgress, this);

        delay(() -> {
            hideGone(downloadProgress);
            animSlideInBottom(tabLayout, this);
            show(tabLayout);
            reveal(this, previewPager, installButton, R.color.primary_accent);
            animGrowFromCenter(installButton, this);
            show(installButton);
        }, 400);
    }

    private void startInstall() {
        progressDialog = ProgressDialog
                .show(this, null, getString(R.string.font_activity_install_progress), true, false);

        FontDownloader.downloadAllFonts(fontPackage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .last()
                .flatMap(v -> FontInstaller.install(fontPackage, this))
                .doOnCompleted(this::onInstallComplete)
                .subscribe(
                        next -> {
                        },
                        error -> {
                            if (error instanceof FontDownloader.DownloadException)
                                handleFailedDownload(error.getCause());
                            else if (error instanceof FontInstaller.InstallException)
                                handleFailedInstall(error);
                        });
    }

    private Style getCurrentPageStyle() {
        switch (currentPage) {
            case 0:
                return Style.REGULAR;
            case 1:
                return Style.BOLD;
            case 2:
                return Style.ITALIC;
            default:
                return Style.REGULAR;
        }
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
        progressDialog.dismiss();
        delay(() -> {
            installButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_white));

            animGrowFromCenter(installButton, this);
            show(installButton);

            delay(() -> {
                if (!this.isFinishing()) AlertUtils.showRebootAlert(this);
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
        if (fragmentsInitialized) {
            TryFontFragment dialog = TryFontFragment.newInstance(fontPackage, getCurrentPageStyle(), this::tryFontCallback);
            dialog.show(getSupportFragmentManager(), "TryFontFragment");
        }
    }

    private void tryFontCallback(String input) {
        if (input.equals("") || input.isEmpty()) return;
        for (PreviewFragment fragment : previewPages) {
            fragment.setPreviewText(input);
        }
    }

    private void toggleCase() {
        if (fragmentsInitialized)
            for (PreviewFragment fragment : previewPages)
                fragment.toggleCase();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        currentPage = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    private class PreviewPagerAdapter extends FragmentPagerAdapter {

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
            previewPages[position] = fragment;
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            return previewPages[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return previewPages.length;
        }
    }
}
