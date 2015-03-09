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

import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class BaseActivity extends ActionBarActivity {

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

    protected void initializeAd(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getResources().getString(R.string.nexus_5_device_id))
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
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
