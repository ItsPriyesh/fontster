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
import android.widget.TextView;
import android.widget.Toast;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontDownloader;
import com.chromium.fontinstaller.core.FontInstaller;
import com.chromium.fontinstaller.events.DownloadCompleteEvent;
import com.chromium.fontinstaller.events.InstallCompleteEvent;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.util.AlertUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FontActivity extends BaseActivity {

    @InjectView(R.id.font_name)
    TextView fontTitle;

    private String fontName;
    private FontPackage fontPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);
        ButterKnife.inject(this);

        fontName = getIntent().getStringExtra("FONT_NAME");
        fontPackage = new FontPackage(fontName);

        fontTitle.setText(fontName);
    }

    @OnClick(R.id.install_button)
    public void installButtonClicked() {
        FontDownloader fontDownloader = new FontDownloader(fontPackage, this);
        fontDownloader.download();
    }

    @Subscribe
    public void downloadComplete(DownloadCompleteEvent event) {
        Toast.makeText(this, "Download complete", Toast.LENGTH_SHORT).show();

        FontInstaller fontInstaller = new FontInstaller(fontPackage, this);
        fontInstaller.install();
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
