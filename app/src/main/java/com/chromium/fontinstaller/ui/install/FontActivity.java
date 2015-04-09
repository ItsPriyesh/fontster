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

import android.os.Bundle;
import android.os.Handler;
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
import com.chromium.fontinstaller.events.DownloadCompleteEvent;
import com.chromium.fontinstaller.events.InstallCompleteEvent;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.ui.common.SlidingTabLayout;
import com.chromium.fontinstaller.util.AlertUtils;
import com.chromium.fontinstaller.util.ViewUtils;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;
import butterknife.OnClick;

public class FontActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.font_name)
    TextView fontTitle;

    @InjectView(R.id.install_fab)
    FloatingActionButton installButton;

    @InjectView(R.id.download_progress)
    ProgressBar downloadProgress;

    @InjectView(R.id.install_progress)
    ProgressBar installProgress;

    @InjectView(R.id.preview_pager)
    ViewPager previewPager;

    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;

    @InjectView(R.id.error_container)
    ViewGroup errorContainer;

    private String fontName;
    private boolean fragmentsInitialized = false;
    private int currentPage = 0;
    private FontPackage fontPackage;
    private PreviewPagerAdapter pagerAdapter;
    private PreviewFragment regularFragment;
    private PreviewFragment boldFragment;
    private PreviewFragment italicFragment;
    private PreviewFragment[] previewPages = new PreviewFragment[3];
    private final String[] tabTitles = {"Regular", "Bold", "Italic"};

    public static final String FONT_NAME = "fontName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_font);
        disableToolbarElevation();
        showToolbarBackButton();

        fontName = getIntent().getStringExtra(FONT_NAME);

        fontPackage = new FontPackage(fontName);
        startDownload();

        fontTitle.setText(fontName);

        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.primary_accent));
        slidingTabLayout.setDistributeEvenly(true);
    }

    private void initializeFragments() {
        regularFragment = PreviewFragment.newInstance(fontPackage, Style.REGULAR);
        boldFragment = PreviewFragment.newInstance(fontPackage, Style.BOLD);
        italicFragment = PreviewFragment.newInstance(fontPackage, Style.ITALIC);


        previewPages[0] = regularFragment;
        previewPages[1] = boldFragment;
        previewPages[2] = italicFragment;

        fragmentsInitialized = true;
    }

    private void startDownload() {
        if (isVisible(errorContainer)) hide(errorContainer);

        show(downloadProgress);
        FontDownloader fontDownloader = new FontDownloader(fontPackage, this);
        fontDownloader.downloadAll();
    }

    private void handleFailedDownload() {
        ViewUtils.animSlideUp(downloadProgress, this);

        new Handler().postDelayed(() -> {
            hideGone(downloadProgress);

            ViewUtils.animSlideInBottom(errorContainer, this);
            show(errorContainer);
        }, 400);
    }

    private void setupPager() {
        initializeFragments();
        pagerAdapter = new PreviewPagerAdapter(getSupportFragmentManager());
        previewPager.setOffscreenPageLimit(2);
        previewPager.setAdapter(pagerAdapter);
        slidingTabLayout.setOnPageChangeListener(this);
        slidingTabLayout.setViewPager(previewPager);

        animateViews();
    }

    private void animateViews() {
        ViewUtils.animSlideUp(downloadProgress, this);

        new Handler().postDelayed(() -> {
            hideGone(downloadProgress);

            ViewUtils.animSlideInBottom(slidingTabLayout, this);
            show(slidingTabLayout);

            ViewUtils.reveal(this, previewPager, installButton, R.color.primary_accent);

            ViewUtils.animGrowFromCenter(installButton, this);
            show(installButton);

        }, 400);
    }

    private void startInstall() {
        ViewUtils.animGrowFromCenter(installProgress, this);
        show(installProgress);

        FontInstaller fontInstaller = new FontInstaller(fontPackage, this);
        fontInstaller.install();
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
        startInstall();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.retry)
    public void retryButtonClicked() {
        startDownload();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDownloadComplete(DownloadCompleteEvent event) {
        if (event.wasSuccessful()) setupPager();
        else handleFailedDownload();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onInstallComplete(InstallCompleteEvent event) {
        new Handler().postDelayed(() -> {
            ViewUtils.animShrinkToCenter(installProgress, this);
            hide(installProgress);

            installButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_white));
            installButton.setColorNormal(getResources().getColor(R.color.secondary_accent));

            ViewUtils.animGrowFromCenter(installButton, this);
            show(installButton);

            if (!this.isFinishing()) AlertUtils.showRebootAlert(this);
        }, 2000);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
            TryFontFragment dialog = TryFontFragment.newInstance(fontPackage, getCurrentPageStyle());
            dialog.show(getSupportFragmentManager(), "TryFontFragment");
        }
    }

    private void toggleCase() {
        if (fragmentsInitialized)
            for (PreviewFragment fragment : previewPages)
                fragment.toggleCase();
    }

    private class PreviewPagerAdapter extends FragmentPagerAdapter {
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
