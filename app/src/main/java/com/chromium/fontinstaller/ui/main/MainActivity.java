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

package com.chromium.fontinstaller.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chromium.fontinstaller.BuildConfig;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.ui.backuprestore.BackupRestoreFragment;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.ui.fontlist.FontListFragment;
import com.chromium.fontinstaller.ui.install.FontActivity;
import com.chromium.fontinstaller.ui.settings.SettingsActivity;
import com.chromium.fontinstaller.util.ViewUtils;
import com.google.android.gms.ads.AdView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements MaterialSearchView.SearchViewListener {

  @Bind(R.id.drawer_layout)
  DrawerLayout mDrawerLayout;

  @Bind(R.id.navigation_view)
  NavigationView mNavigationView;

  @Bind(R.id.search_view)
  MaterialSearchView mSearchView;

  @Bind(R.id.ad_view)
  AdView mAdView;

  private ActionBarDrawerToggle mDrawerToggle;
  private FragmentManager mFragmentManager;
  private FontListFragment mFontListFragment;
  private BackupRestoreFragment mBackupRestoreFragment;

  private boolean mShouldShowSearch = true;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setToolbarTitle(getString(R.string.app_name));

    if (!BuildConfig.DEBUG) initializeAd(mAdView);

    mDrawerToggle = new ActionBarDrawerToggle(
        this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);

    setupDrawerContent(mNavigationView);
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    mFragmentManager = getSupportFragmentManager();
    mFontListFragment = new FontListFragment();
    mBackupRestoreFragment = new BackupRestoreFragment();

    swapFragment(mFontListFragment);

    final String[] fontList = getResources().getStringArray(R.array.font_list);
    mSearchView.setOnSearchViewListener(this);
    mSearchView.setSuggestionIcon(null);
    mSearchView.setSuggestions(fontList);
    mSearchView.setPadding(0, ViewUtils.getStatusBarHeight(this), 0, 0);
    mSearchView.setOnItemClickListener((parent, view, position, id) -> {
      final String fontName = getFontNameFromListItem(view);
      final Intent intent = FontActivity.getLaunchIntent(this, fontName);
      startActivity(intent);
    });
  }

  private String getFontNameFromListItem(View view) {
    return ((TextView) view.findViewById(R.id.suggestion_text)).getText().toString();
  }

  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(menuItem -> {
      mDrawerLayout.closeDrawers();
      selectDrawerItem(menuItem);
      return true;
    });
  }

  private void selectDrawerItem(MenuItem menuItem) {
    final int selectedId = menuItem.getItemId();
    mShouldShowSearch = (selectedId == R.id.fonts);
    switch (selectedId) {
      case R.id.fonts:
        swapFragment(mFontListFragment);
        menuItem.setChecked(true);
        setTitle(getString(R.string.app_name));
        invalidateOptionsMenu();
        break;
      case R.id.backup:
        swapFragment(mBackupRestoreFragment);
        menuItem.setChecked(true);
        setTitle(getString(R.string.drawer_item_backup_restore));
        invalidateOptionsMenu();
        break;
      case R.id.settings:
        final Intent intent = new Intent(this, SettingsActivity.class);
        delay(() -> startActivity(intent), 200);
        break;
    }
  }

  private void swapFragment(Fragment fragment) {
    mFragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);

    final MenuItem searchItem = menu.findItem(R.id.action_search);
    searchItem.setVisible(mShouldShowSearch);
    mSearchView.setMenuItem(searchItem);

    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    mDrawerToggle.syncState();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override public void onBackPressed() {
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START) || mSearchView.isSearchOpen()) {
      mDrawerLayout.closeDrawers();
      return;
    }
    super.onBackPressed();
  }

  @Override public void onSearchViewShown() {
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawers();
    }
  }

  @Override public void onSearchViewClosed() { }
}
