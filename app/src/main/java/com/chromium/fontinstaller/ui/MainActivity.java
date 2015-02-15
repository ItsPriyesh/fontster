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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontDownloader;
import com.chromium.fontinstaller.events.DownloadCompleteEvent;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.ui.common.BaseActionBarActivity;
import com.chromium.fontinstaller.ui.common.FontListAdapter;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import butterknife.InjectView;


public class MainActivity extends BaseActionBarActivity {

    @InjectView(R.id.font_list_view)
    ListView fontListView;

    ArrayList<FontPackage> fontList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fontList = new ArrayList<>();
        populateFontList();

        FontListAdapter fontListAdapter = new FontListAdapter(this, fontList);
        fontListView.setAdapter(fontListAdapter);

       // Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);


    }

    public void download(View view) {
        FontPackage fontPackage = new FontPackage("Roboto Slab");
        FontDownloader fontDownloader = new FontDownloader(fontPackage, this);
        fontDownloader.download();
    }

    @Subscribe
    public void startInstallation(DownloadCompleteEvent event) {
        Toast.makeText(this, "Download complete", Toast.LENGTH_SHORT).show();
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

    private void populateFontList() {
        try {
            Scanner scanner = new Scanner(getAssets().open("fonts"));
            while (scanner.hasNextLine()) {
                fontList.add(new FontPackage(scanner.nextLine()));
            }
            scanner.close();
        } catch (IOException e) {

        }
    }
}
