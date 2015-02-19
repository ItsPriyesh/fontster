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

package com.chromium.fontinstaller.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.ui.common.NavDrawerAdapter;
import com.chromium.fontinstaller.ui.common.NavDrawerItem;
import com.chromium.fontinstaller.util.RootUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;


public class MainActivity extends BaseActivity {

    @InjectView(R.id.app_bar)
    Toolbar toolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.drawer_list)
    ListView drawerList;

    private ActionBarDrawerToggle drawerToggle;
    private FragmentManager fragmentManager;
    private Fragment fontListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        toolbar.setSubtitle("Select a font");
        setSupportActionBar(toolbar);

        RootUtils.requestAccess();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerList.setAdapter(new NavDrawerAdapter(this, generateNavItems()));

        fragmentManager = getFragmentManager();
        fontListFragment = new FontListFragment();

        startFragment(fontListFragment);
    }

    private ArrayList<NavDrawerItem> generateNavItems() {
        ArrayList<NavDrawerItem> items = new ArrayList<>(3);
        String[] titles = getResources().getStringArray(R.array.nav_drawer_titles);
        String[] icons = getResources().getStringArray(R.array.nav_drawer_icons);

        for (int i = 0; i < Math.min(titles.length, icons.length); i++) {
            items.add(new NavDrawerItem(titles[i],
                    getDrawable(getResources().getIdentifier(icons[i], "drawable", getPackageName()))));
        }

        return items;
    }

    private void startFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @OnItemClick(R.id.drawer_list)
    public void onNavItemClicked(int position) {
        switch (position) {
            case 0:
                startFragment(fontListFragment);
                break;
            case 1:
                break;
            case 2:
                break;
        }
        drawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (drawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
