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


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.chromium.fontinstaller.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DonateDialogFragment extends DialogFragment {

    @InjectView(R.id.radio_group)
    RadioGroup radioGroup;

    private DonationClickListener listener;

    public interface DonationClickListener {
        public void onDonationClick(String sku);
    }

    public DonateDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate_dialog, container, false);
        ButterKnife.inject(this, view);

        getDialog().setTitle("Donation amount");

        listener = (DonationClickListener) getParentFragment();

        return view;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.done_button)
    private void dispatchDonationClick() {
        String sku;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.donate_small:
                sku = SettingsFragment.DONATE_SKU_SMALL;
                break;
            case R.id.donate_medium:
                sku = SettingsFragment.DONATE_SKU_MED;
                break;
            case R.id.donate_large:
                sku = SettingsFragment.DONATE_SKU_LARGE;
                break;
            default:
                sku = SettingsFragment.DONATE_SKU_SMALL;
                break;
        }
        listener.onDonationClick(sku);
    }
}
