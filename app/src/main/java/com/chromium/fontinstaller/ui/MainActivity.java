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

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.ui.common.FontListAdapter;
import com.chromium.fontinstaller.util.RootUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity {

    @InjectView(R.id.app_bar)
    Toolbar toolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.font_list_view)
    RecyclerView recyclerView;

    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager listManager;

    ArrayList<String> fontList;
    FontPackage fontPackage;
    ActionBarDrawerToggle drawerToggle;

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

        fontList = new ArrayList<>();
        populateFontList();

        listAdapter = new FontListAdapter(this, fontList);
        listManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(listManager);
        recyclerView.setAdapter(listAdapter);
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

    private void listItemClicked(int position) {
        Intent intent = new Intent(this, FontActivity.class);
        intent.putExtra("FONT_NAME", fontList.get(position));
        startActivity(intent);
    }

    private void populateFontList() {
        try {
            Scanner scanner = new Scanner(getAssets().open("fonts"));
            while (scanner.hasNextLine()) {
                fontList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {

        }
    }
}
