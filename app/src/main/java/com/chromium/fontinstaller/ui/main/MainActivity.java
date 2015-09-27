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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.ui.backuprestore.BackupRestoreFragment;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.ui.fontlist.FontListFragment;
import com.chromium.fontinstaller.ui.settings.SettingsActivity;
import com.chromium.fontinstaller.util.RootUtils;
import com.google.android.gms.ads.AdView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements MaterialSearchView.OnQueryTextListener {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    //@Bind(R.id.drawer_list)
    //ListView drawerList;

    @Bind(R.id.nvView)
    NavigationView nvDrawer;

    @Bind(R.id.search_view)
    MaterialSearchView searchView;

    @Bind(R.id.adView)
    AdView adView;

    private ActionBarDrawerToggle drawerToggle;
    private FragmentManager fragmentManager;
    private FontListFragment fontListFragment;
    private BackupRestoreFragment backupRestoreFragment;
    static final String STATE_SELECTED_POSITION = "currentFrag";
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarTitle("Fontster");

        //initializeAd(adView);

        RootUtils.requestAccess();

        searchView.setOnQueryTextListener(this);

        if (savedInstanceState != null) {
            currentPosition =
                    savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        setupDrawerContent(nvDrawer);
        drawerLayout.setDrawerListener(drawerToggle);

        fragmentManager = getSupportFragmentManager();
        fontListFragment = new FontListFragment();
        backupRestoreFragment = new BackupRestoreFragment();

        swapFragment(fontListFragment);
    }


    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.fonts:
                swapFragment(fontListFragment);
                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
                drawerLayout.closeDrawers();
                currentPosition = 0;
                break;
            case R.id.backup:
                swapFragment(backupRestoreFragment);
                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
                drawerLayout.closeDrawers();
                currentPosition = 1;
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    private void swapFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView.setMenuItem(searchItem);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        Menu menu = nvDrawer.getMenu();
        selectDrawerItem(menu.getItem(currentPosition).setChecked(true));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
