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

package com.chromium.fontinstaller.ui.settings.developer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontInstaller;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.util.RebootDialog;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.chromium.fontinstaller.util.ViewUtils.snackbar;

public class DeveloperSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.developer_settings);

        findPreference("install_custom_font").setOnPreferenceClickListener(p -> {
            confirmCustomFontInstall();
            return true;
        });
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
                    new RebootDialog(getActivity());
                }, error -> {
                    Timber.i(error.getMessage());
                    progressDialog.dismiss();
                    snackbar(R.string.settings_custom_font_install_failed, getView());
                });
    }
}
