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
import com.chromium.fontinstaller.core.FontInstaller;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.ui.main.MainActivity;
import com.chromium.fontinstaller.util.AlertUtils;
import com.chromium.fontinstaller.util.Licenses;
import com.chromium.fontinstaller.util.PreferencesManager;
import com.chromium.fontinstaller.util.billing.IabHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.psdev.licensesdialog.LicensesDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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
            installCustomFont.setOnPreferenceClickListener(pref -> confirmCustomFontInstall());
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
                toast(R.string.settings_developer_mode_already_enabled, getActivity());
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
                donate.setSummary(R.string.settings_iab_setup_error);
            }
        });

        donate.setOnPreferenceClickListener(pref -> showDonationDialog());

    }

    private boolean confirmCustomFontInstall() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.settings_confirm_custom_font_install_title)
                .setMessage(R.string.settings_confirm_custom_font_install_message)
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.yes, (dialog, which) ->
                        new FontPackPickerDialog(getActivity(), this::installCustomFont).show())
                .create().show();
        return true;
    }

    private void installCustomFont(FontPackage fontPackage) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.settings_custom_font_install_progress));
        progressDialog.show();

        FontInstaller.install(fontPackage, getActivity())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(done -> {
                    progressDialog.dismiss();
                    AlertUtils.showRebootAlert(getActivity());
                }, error -> {
                    Timber.i(error.getMessage());
                    progressDialog.dismiss();
                    snackbar(R.string.settings_custom_font_install_failed, getView());
                });
    }

    private void enableDeveloperMode() {
        if (tapsLeftToast != null) tapsLeftToast.cancel();
        toast(R.string.settings_developer_mode_enabled, getActivity());
        preferences.setBoolean(Keys.ENABLE_DEVELOPER_MODE, true);
    }

    private void showTapsLeftToast() {
        final String s = String.format(getString(R.string.settings_taps_left), devModeCountdown == 1
                ? getString(R.string.settings_single_tap)
                : devModeCountdown + getString(R.string.settings_multiple_taps));

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
            if (result.isFailure()) snackbar(R.string.settings_donation_failed, v);
            else if (purchase.getSku().equals(sku)) snackbar(R.string.settings_donation_success, v);
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
        progressDialog.setMessage(getString(R.string.settings_clear_cache_progress));
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
                .setPositiveButton(R.string.settings_restart_dialog_button, (dialog, id) -> restartApp())
                .create().show();
    }

    private void restartApp() {
        ActivityCompat.finishAffinity(getActivity());
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
