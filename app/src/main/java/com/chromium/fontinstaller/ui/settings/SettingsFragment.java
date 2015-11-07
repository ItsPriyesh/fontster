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

package com.chromium.fontinstaller.ui.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.chromium.fontinstaller.BuildConfig;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.SecretStuff;
import com.chromium.fontinstaller.core.CommandRunner;
import com.chromium.fontinstaller.ui.main.MainActivity;
import com.chromium.fontinstaller.ui.settings.developer.DeveloperSettingsActivity;
import com.chromium.fontinstaller.util.Licenses;
import com.chromium.fontinstaller.util.PreferencesManager;
import com.chromium.fontinstaller.util.billing.IabHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.psdev.licensesdialog.LicensesDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.chromium.fontinstaller.util.PreferencesManager.Keys;
import static com.chromium.fontinstaller.util.ViewUtils.snackbar;

public class SettingsFragment extends PreferenceFragment implements
        DonateDialogFragment.DonationClickListener {

    private static final int TAPS_TO_ENTER_DEV_SETTINGS = 8;

    public static final String DONATE_SKU_SMALL = "com.chromium.fontster.mDonate";
    public static final String DONATE_SKU_MED = "com.chromium.fontster.donate_med";
    public static final String DONATE_SKU_LARGE = "com.chromium.fontster.donate_large";

    private IabHelper mBillingHelper;
    private PreferencesManager mPreferences;
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseListener;
    private ProgressDialog mProgressDialog;
    private int mVersionTaps = 0;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mPreferences = PreferencesManager.getInstance(getActivity());

        findPreferenceById(R.string.pref_key_true_font)
                .setOnPreferenceChangeListener((pref, newValue) -> handleTrueFont(newValue));

        findPreferenceById(R.string.pref_key_clear_cache)
                .setOnPreferenceClickListener(pref -> clearCache());

        findPreferenceById(R.string.pref_key_view_source)
                .setOnPreferenceClickListener(pref -> viewSource());

        findPreferenceById(R.string.pref_key_licenses)
                .setOnPreferenceClickListener(pref -> openLicensesDialog());

        final Preference appVersion = findPreferenceById(R.string.pref_key_app_version);
        appVersion.setSummary(BuildConfig.VERSION_NAME + " - " + BuildConfig.BUILD_TYPE);
        appVersion.setOnPreferenceClickListener(pref -> {
            if (++mVersionTaps == TAPS_TO_ENTER_DEV_SETTINGS) {
                mVersionTaps = 0;
                startActivity(new Intent(getActivity(), DeveloperSettingsActivity.class));
            }
            return true;
        });

        final Preference donate = findPreferenceById(R.string.pref_key_donate);
        donate.setOnPreferenceClickListener(pref -> showDonationDialog());

        mBillingHelper = new IabHelper(getActivity(), SecretStuff.LICENSE_KEY);
        mBillingHelper.startSetup(result -> {
            if (result.isSuccess()) {
                donate.setEnabled(true);
            } else {
                donate.setSummary(R.string.settings_iab_setup_error);
            }
        });

    }

    private Preference findPreferenceById(int key) {
        return findPreference(getString(key));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBillingHelper != null) mBillingHelper.dispose();
        mBillingHelper = null;
    }

    public IabHelper getBillingHelper() {
        return mBillingHelper;
    }

    private boolean showDonationDialog() {
        final DonateDialogFragment donateDialog = new DonateDialogFragment();
        final FragmentManager fm = ((SettingsActivity) getActivity()).getSupportFragmentManager();
        donateDialog.show(fm, "DonateDialogFragment");
        donateDialog.setDonationClickListener(this);
        return true;
    }

    @Override
    public void onDonationClick(String sku) {
        makeDonation(sku);
    }

    private boolean makeDonation(String sku) {
        mBillingHelper.launchPurchaseFlow(getActivity(), sku, 1, mPurchaseListener, "");
        mPurchaseListener = (result, purchase) -> {
            final View v = getView();
            if (result.isFailure()) snackbar(R.string.settings_donation_failed, v);
            else if (purchase.getSku().equals(sku)) snackbar(R.string.settings_donation_success, v);
        };

        return true;
    }

    private boolean handleTrueFont(Object newValue) {
        mPreferences.setBoolean(Keys.ENABLE_TRUEFONT, (boolean) newValue);
        showRestartDialog();
        return true;
    }

    private boolean clearCache() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.settings_clear_cache_progress));
        mProgressDialog.show();

        final List<String> commands = new ArrayList<>();
        final File cache = new File(getActivity().getExternalCacheDir() + File.separator);

        if (cache.listFiles() != null) {
            for (File f : cache.listFiles())
                if (!f.getName().equals("Backup"))
                    commands.add("rm -rf " + f.getAbsolutePath());

            CommandRunner.runCommands(commands)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(this::onCacheCleared)
                    .subscribe();
        } else {
            snackbar(getString(R.string.settings_clear_cache_failed), getView());
        }

        return true;
    }

    public void onCacheCleared() {
        mPreferences.setBoolean(Keys.TRUEFONTS_CACHED, false);
        mProgressDialog.dismiss();
        snackbar(R.string.settings_clear_cache_success, getView());
    }

    private boolean viewSource() {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(getString(R.string.settings_link_github)));
        startActivity(intent);
        return true;
    }

    private boolean openLicensesDialog() {
        new LicensesDialog.Builder(getActivity())
                .setNotices(Licenses.getNotices())
                .build().show();
        return true;
    }

    private void showRestartDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.settings_restart_dialog_message)
                .setPositiveButton(R.string.settings_restart_dialog_button, (d, i) -> restartApp())
                .create().show();
    }

    private void restartApp() {
        ActivityCompat.finishAffinity(getActivity());
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
