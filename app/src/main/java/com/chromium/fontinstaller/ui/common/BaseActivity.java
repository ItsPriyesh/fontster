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

package com.chromium.fontinstaller.ui.common;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.SecretStuff;
import com.chromium.fontinstaller.ui.settings.SettingsFragment;
import com.chromium.fontinstaller.util.billing.IabHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseActivity extends ActionBarActivity {

    @InjectView(R.id.app_bar)
    protected Toolbar toolbar;

    private ActionBar actionBar;
    private IabHelper billingHelper;
    private boolean billingSetup = false;

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        ButterKnife.inject(this);

        billingHelper = new IabHelper(this, SecretStuff.LICENSE_KEY);
        billingHelper.startSetup(result -> billingSetup = result.isSuccess());

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initializeAd(AdView adView) {
        if (billingSetup) {
            billingHelper.queryInventoryAsync((result, inventory) -> {
                boolean userDonated = inventory.hasPurchase(SettingsFragment.DONATE_SKU);
                if (userDonated) adView.setVisibility(View.GONE);
                else {
                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice(getResources().getString(R.string.nexus_5_device_id))
                            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .build();
                    adView.loadAd(adRequest);
                }
            });
        }
    }

    protected void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    protected void disableToolbarElevation() {
        toolbar.setElevation(0);
    }

    protected void showToolbarBackButton() {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    protected void hide(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    protected void hideGone(View view) {
        view.setVisibility(View.GONE);
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }
}
