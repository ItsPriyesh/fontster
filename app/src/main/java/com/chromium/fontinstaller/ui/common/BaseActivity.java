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

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.SecretStuff;
import com.chromium.fontinstaller.ui.settings.SettingsFragment;
import com.chromium.fontinstaller.util.ViewUtils;
import com.chromium.fontinstaller.util.billing.IabHelper;
import com.chromium.fontinstaller.util.billing.Inventory;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {

  @Bind(R.id.app_bar)
  protected Toolbar mToolbar;

  private IabHelper mBillingHelper;
  private final Handler mHandler = new Handler();

  @Override public void setContentView(int layoutResId) {
    super.setContentView(layoutResId);
    ButterKnife.bind(this);

    mBillingHelper = new IabHelper(this, SecretStuff.LICENSE_KEY);

    setSupportActionBar(mToolbar);
    final ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      this.finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  protected void initializeAd(AdView adView) {
    mBillingHelper.startSetup(result -> {
      if (result.isSuccess()) {
        Timber.i("Billing setup");
        mBillingHelper.queryInventoryAsync((queriedResult, inventory) -> {
          Timber.i("User Donated: " + userDonated(inventory));
          if (userDonated(inventory)) hideAd(adView);
          else displayAd(adView);
        });
      }
    });
  }

  private boolean userDonated(Inventory inventory) {
    return inventory != null && (inventory.hasPurchase(SettingsFragment.DONATE_SKU_SMALL) ||
        inventory.hasPurchase(SettingsFragment.DONATE_SKU_MED) ||
        inventory.hasPurchase(SettingsFragment.DONATE_SKU_LARGE));
  }

  private void displayAd(AdView adView) {
    AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(getResources().getString(R.string.nexus_5_device_id))
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build();
    adView.loadAd(adRequest);
  }

  protected void delay(Runnable runnable, long delay) {
    mHandler.postDelayed(runnable, delay);
  }

  private void hideAd(AdView adView) {
    adView.setVisibility(View.GONE);
  }

  public void setToolbarTitle(String title) {
    mToolbar.setTitle(title);
  }

  protected void disableToolbarElevation() {
    if (ViewUtils.isLollipop()) mToolbar.setElevation(0);
  }

  protected void showToolbarBackButton() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

  protected void logEvent(String message) {
    Answers.getInstance().logContentView(new ContentViewEvent().putContentName(message));
  }
}
