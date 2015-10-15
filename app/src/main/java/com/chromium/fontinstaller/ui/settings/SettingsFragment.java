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
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.chromium.fontinstaller.BuildConfig;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.SecretStuff;
import com.chromium.fontinstaller.core.CommandRunner;
import com.chromium.fontinstaller.ui.main.MainActivity;
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
import static com.chromium.fontinstaller.util.ViewUtils.toast;

public class SettingsFragment extends PreferenceFragment implements
        DonateDialogFragment.DonationClickListener {

    public static final String DONATE_SKU_SMALL = "com.chromium.fontster.donate";
    public static final String DONATE_SKU_MED = "com.chromium.fontster.donate_med";
    public static final String DONATE_SKU_LARGE = "com.chromium.fontster.donate_large";

    private static final int TAPS_TO_ENABLE_DEVELOPER_MODE = 7;

    private IabHelper billingHelper;
    private PreferencesManager preferences;
    private IabHelper.OnIabPurchaseFinishedListener purchaseListener;
    private Preference donate;
    private ProgressDialog progressDialog;

    private Toast tapsLeftToast;
    private int devModeCountdown;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        preferences = PreferencesManager.getInstance(getActivity());

        billingHelper = new IabHelper(getActivity(), SecretStuff.LICENSE_KEY);

        final boolean developerModeEnabled = preferences.getBoolean(Keys.ENABLE_DEVELOPER_MODE);
        devModeCountdown = developerModeEnabled ? -1 : TAPS_TO_ENABLE_DEVELOPER_MODE;

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        final PreferenceCategory developerOptions = (PreferenceCategory) findPreference("developer_options");

        if (developerModeEnabled) {
            final Preference installCustomFont = findPreference("install_custom_font");
            installCustomFont.setOnPreferenceClickListener(pref -> installCustomFont());
        } else {
            preferenceScreen.removePreference(developerOptions);
        }

        final CheckBoxPreference trueFont = (CheckBoxPreference) findPreference("trueFont");
        trueFont.setOnPreferenceChangeListener((pref, newValue) -> handleTrueFont(newValue));

        final Preference clearCache = findPreference("clearCache");
        clearCache.setOnPreferenceClickListener(pref -> clearCache());

        final Preference source = findPreference("viewSource");
        source.setOnPreferenceClickListener(pref -> viewSource());

        final Preference licenses = findPreference("licenses");
        licenses.setOnPreferenceClickListener(pref -> openLicensesDialog());

        final Preference appVersion = findPreference("appVersion");
        appVersion.setSummary(BuildConfig.VERSION_NAME + " - " + BuildConfig.BUILD_TYPE);
        appVersion.setOnPreferenceClickListener(pref -> {
            if (devModeCountdown < 0) {
                toast("Developer mode already enabled", getActivity());
            } else {
                devModeCountdown--;
                if (devModeCountdown == 0) enableDeveloperMode();
                else if (devModeCountdown > 0 && devModeCountdown < 5) showTapsLeftToast();
            }
            return true;
        });

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

    private boolean installCustomFont() {
        new FontPackPickerDialog(getActivity(), fontPackage -> {

        }).show();
        return true;
    }

    private void enableDeveloperMode() {
        if (tapsLeftToast != null) tapsLeftToast.cancel();
        toast("Developer mode enabled!", getActivity());
        preferences.setBoolean(Keys.ENABLE_DEVELOPER_MODE, true);
    }

    private void showTapsLeftToast() {
        final String s = (devModeCountdown == 1 ? "1 tap" : devModeCountdown + " taps")
                + " away from enabling developer mode";

        if (tapsLeftToast != null) tapsLeftToast.cancel();
        tapsLeftToast = Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT);
        tapsLeftToast.show();
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
        final DonateDialogFragment donateDialog = new DonateDialogFragment();
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
            final View v = getView();
            if (result.isFailure()) snackbar("Failed to make donation", v);
            else if (purchase.getSku().equals(sku)) snackbar("Donation complete, thanks :)", v);
        };

        return true;
    }

    private boolean handleTrueFont(Object newValue) {
        preferences.setBoolean(Keys.ENABLE_TRUEFONT, (boolean) newValue);
        showRestartDialog();
        return true;
    }

    private boolean clearCache() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Clearing cache...");
        progressDialog.show();

        final List<String> commands = new ArrayList<>();
        final File cache = new File(getActivity().getExternalCacheDir() + File.separator);

        for (File f : cache.listFiles())
            if (!f.getName().equals("Backup"))
                commands.add("rm -rf " + f.getAbsolutePath());

        CommandRunner.runCommands(commands)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(this::onCacheCleared)
                .subscribe();

        return true;
    }

    public void onCacheCleared() {
        preferences.setBoolean(Keys.TRUEFONTS_CACHED, false);
        progressDialog.dismiss();
        snackbar("Cache has been cleared", getView());
    }

    private boolean viewSource() {
        final Intent intent = new Intent();
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
}
