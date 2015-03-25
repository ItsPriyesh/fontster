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

package com.chromium.fontinstaller.ui.install;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.common.AutoScaleTextView;
import com.chromium.fontinstaller.util.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PreviewFragment extends Fragment {

    @InjectView(R.id.preview_text)
    AutoScaleTextView previewText;

    private FontPackage fontPackage;
    private Style style;
    private boolean upperCase = true;
    private static String alphabetUpper, alphabetLower;
    private static final String STATE_FONT_PACKAGE = "fontPackage";
    private static final String STATE_FONT_STYLE = "style";
    private static final String STATE_UPPER_CASE = "upperCase";

    public PreviewFragment() {

    }

    public static PreviewFragment newInstance(FontPackage fontPackage, Style style) {
        PreviewFragment fragment = new PreviewFragment();
        fragment.setFontPackage(fontPackage);
        fragment.setStyle(style);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        ButterKnife.inject(this, view);

        alphabetUpper = getString(R.string.alphabet_upper);
        alphabetLower = getString(R.string.alphabet_lower);

        if (savedInstanceState != null) {
            fontPackage = savedInstanceState.getParcelable(STATE_FONT_PACKAGE);
            style = savedInstanceState.getParcelable(STATE_FONT_STYLE);
            upperCase = savedInstanceState.getBoolean(STATE_UPPER_CASE);
        }

        previewText.setTypeface(fontPackage.getTypeface(style));
        previewText.setText(getAlphabet());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(STATE_FONT_PACKAGE, fontPackage);
        savedInstanceState.putParcelable(STATE_FONT_STYLE, style);
        savedInstanceState.putBoolean(STATE_UPPER_CASE, upperCase);
    }

    private void setFontPackage(FontPackage fontPackage) {
        this.fontPackage = fontPackage;
    }

    private void setStyle(Style style) {
        this.style = style;
    }

    private String getAlphabet() {
        if (upperCase) return alphabetUpper;
        else return alphabetLower;
    }

    public void toggleCase() {
        if (upperCase) {
            previewText.setText(alphabetLower);
            upperCase = false;
        } else {
            previewText.setText(alphabetUpper);
            upperCase = true;
        }
        ViewUtils.animSlideInBottom(previewText, getActivity());
    }
}
