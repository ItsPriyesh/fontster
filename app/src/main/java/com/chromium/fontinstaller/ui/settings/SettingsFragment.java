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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;

import com.chromium.fontinstaller.BuildConfig;
import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.SecretStuff;
import com.chromium.fontinstaller.core.CommandRunner;
import com.chromium.fontinstaller.events.CacheClearedEvent;
import com.chromium.fontinstaller.ui.main.MainActivity;
import com.chromium.fontinstaller.util.Licenses;
import com.chromium.fontinstaller.util.PreferencesManager;
import com.chromium.fontinstaller.util.billing.IabHelper;
import com.nispok.snackbar.Snackbar;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.psdev.licensesdialog.LicensesDialog;

public class SettingsFragment extends PreferenceFragment implements
        DonateDialogFragment.DonationClickListener {

    private IabHelper billingHelper;
    private PreferencesManager prefs;
    private IabHelper.OnIabPurchaseFinishedListener purchaseListener;
    private Preference donate;
    private ProgressDialog progressDialog;

    public static final String DONATE_SKU_SMALL = "com.chromium.fontster.donate";
    public static final String DONATE_SKU_MED = "com.chromium.fontster.donate_med";
    public static final String DONATE_SKU_LARGE = "com.chromium.fontster.donate_large";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        prefs = PreferencesManager.getInstance(getActivity());

        billingHelper = new IabHelper(getActivity(), SecretStuff.LICENSE_KEY);

        CheckBoxPreference trueFont = (CheckBoxPreference) findPreference("trueFont");
        trueFont.setOnPreferenceChangeListener((pref, newValue) -> handleTrueFont(newValue));

        Preference clearCache = findPreference("clearCache");
        clearCache.setOnPreferenceClickListener(pref -> clearCache());

        Preference source = findPreference("viewSource");
        source.setOnPreferenceClickListener(pref -> viewSource());

        Preference licenses = findPreference("licenses");
        licenses.setOnPreferenceClickListener(pref -> openLicensesDialog());

        Preference appVersion = findPreference("appVersion");
        appVersion.setSummary(BuildConfig.VERSION_NAME + " - " + BuildConfig.BUILD_TYPE);

        donate = findPreference("donate");

        billingHelper.startSetup(result -> {
            if (result.isSuccess()) {
                donate.setEnabled(true);
            } else {
                donate.setSummary("A problem was encountered while setting up In-App Billing");
            }
        });

        donate.setOnPreferenceClickListener(pref -> showDonationDialog());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (billingHelper != null) billingHelper.dispose();
        billingHelper = null;
    }

    public IabHelper getBillingHelper() {
        return billingHelper;
    }

    private boolean showDonationDialog() {
        DonateDialogFragment donateDialog = new DonateDialogFragment();
        donateDialog.show(((SettingsActivity) getActivity()).getSupportFragmentManager(),
                "DonateDialogFragment");
        donateDialog.setDonationClickListener(this);
        return true;
    }

    @Override
    public void onDonationClick(String sku) {
        makeDonation(sku);
    }

    private boolean makeDonation(String sku) {
        billingHelper.launchPurchaseFlow(getActivity(), sku, 1, purchaseListener, "");
        purchaseListener = (result, purchase) -> {
            if (result.isFailure()) {
                Snackbar.with(getActivity())
                        .text("Failed to make donation")
                        .show(getActivity());
            } else if (purchase.getSku().equals(sku)) {
                Snackbar.with(getActivity())
                        .text("Donation complete, thanks :)")
                        .show(getActivity());
            }
        };

        return true;
    }

    private boolean handleTrueFont(Object newValue) {
        prefs.setBoolean(PreferencesManager.KEY_ENABLE_TRUEFONT, (boolean) newValue);
        showRestartDialog();
        return true;
    }

    private boolean clearCache() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Clearing cache...");
        progressDialog.show();

        List<String> commands = new ArrayList<>();
        File cacheDir = new File(getActivity().getExternalCacheDir() + File.separator);

        for (File f : cacheDir.listFiles())
            if (!f.getName().equals("Backup"))
                commands.add("rm -rf " + f.getAbsolutePath());

        CommandRunner clearCacheTask = new CommandRunner(new CacheClearedEvent());
        clearCacheTask.execute(commands.toArray(new String[commands.size()]));

        return true;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onCacheCleared(CacheClearedEvent event) {
        progressDialog.dismiss();
        Snackbar.with(getActivity())
                .text("Cache has been cleared")
                .show(getActivity());
    }

    private boolean viewSource() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://github.com/ItsPriyesh/FontInstaller"));
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
                .setMessage("Restart the app for the change to take effect.")
                .setPositiveButton("Restart", (dialog, id) -> restartApp())
                .create().show();
    }

    private void restartApp() {
        ActivityCompat.finishAffinity(getActivity());
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}

