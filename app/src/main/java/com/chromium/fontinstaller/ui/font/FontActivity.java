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

package com.chromium.fontinstaller.ui.font;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontDownloader;
import com.chromium.fontinstaller.events.DownloadCompleteEvent;
import com.chromium.fontinstaller.events.InstallCompleteEvent;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.util.AlertUtils;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FontActivity extends BaseActivity {

    @InjectView(R.id.app_bar)
    Toolbar toolbar;

    @InjectView(R.id.font_name)
    TextView fontTitle;

    @InjectView(R.id.install_fab)
    FloatingActionButton installButton;

    @InjectView(R.id.progress)
    ProgressBar progressBar;

    @InjectView(R.id.preview_pager)
    ViewPager previewPager;

    private String fontName;
    private FontPackage fontPackage;
    //  private FragmentManager fragmentManager;
    //   private PreviewFragment previewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fontName = getIntent().getStringExtra("FONT_NAME");
        fontPackage = new FontPackage(fontName);
        FontDownloader fontDownloader = new FontDownloader(fontPackage, this);
        fontDownloader.download();

        fontTitle.setText(fontName);
//        previewFragment = new PreviewFragment();


    }

    private class PreviewPagerAdapter extends FragmentPagerAdapter {
        public PreviewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PreviewFragment.newInstance(fontPackage);
                case 1:
                    return WordsFragment.newInstance(fontPackage);
                default:
                    return PreviewFragment.newInstance(fontPackage);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

  /*  private void startFragment(Fragment fragment) {
        previewFragment.setFontPackage(fontPackage);
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }*/

    @OnClick(R.id.install_fab)
    public void installButtonClicked() {
        FontDownloader fontDownloader = new FontDownloader(fontPackage, this);
        fontDownloader.download();
    }

    @Subscribe
    public void downloadComplete(DownloadCompleteEvent event) {

        int cx = (previewPager.getLeft() + previewPager.getRight()) / 2;
        int cy = (previewPager.getTop() + previewPager.getBottom()) / 2;

        int finalRadius = Math.max(previewPager.getWidth(), previewPager.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(previewPager, cx, cy, 0, finalRadius);

        previewPager.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
        previewPager.setAdapter(new PreviewPagerAdapter(getSupportFragmentManager()));


        //  startFragment(previewFragment);

        // FontInstaller fontInstaller = new FontInstaller(fontPackage, this);
        //fontInstaller.install();
    }

    @Subscribe
    public void installComplete(InstallCompleteEvent event) {
        AlertUtils.showRebootAlert(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_font, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
