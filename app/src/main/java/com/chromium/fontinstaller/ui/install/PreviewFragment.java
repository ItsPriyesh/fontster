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

import butterknife.Bind;
import butterknife.ButterKnife;

public final class PreviewFragment extends Fragment {

    @Bind(R.id.preview_text)
    AutoScaleTextView mPreviewText;

    private FontPackage mFontPackage;
    private Style mStyle;
    private boolean mUpperCase = true;
    private String mAlphabetUpper, mAlphabetLower;

    private static final String FONT_NAME_KEY = "font_name_key";
    private static final String FONT_STYLE_KEY = "font_style_key";

    public PreviewFragment() { }

    public static PreviewFragment newInstance(FontPackage fontPackage, Style style) {
        final PreviewFragment fragment = new PreviewFragment();
        fragment.setFontPackage(fontPackage);
        fragment.setStyle(style);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        ButterKnife.bind(this, view);

        mAlphabetUpper = getString(R.string.alphabet_upper);
        mAlphabetLower = getString(R.string.alphabet_lower);

        if (savedInstanceState != null) {
            mFontPackage = new FontPackage(savedInstanceState.getString(FONT_NAME_KEY));
            mStyle = FontActivity.PREVIEW_STYLES.get(savedInstanceState.getInt(FONT_STYLE_KEY));
        }

        mPreviewText.setTypeface(mFontPackage.getTypeface(mStyle));
        mPreviewText.setText(getAlphabet());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FONT_NAME_KEY, mFontPackage.getName());
        outState.putInt(FONT_STYLE_KEY, FontActivity.PREVIEW_STYLES.indexOfValue(mStyle));
    }

    public void setPreviewText(String input) {
        this.mPreviewText.setText(input);
    }

    private void setFontPackage(FontPackage fontPackage) {
        this.mFontPackage = fontPackage;
    }

    private void setStyle(Style style) {
        this.mStyle = style;
    }

    private String getAlphabet() {
        return mUpperCase ? mAlphabetUpper : mAlphabetLower;
    }

    public void toggleCase() {
        if (mUpperCase) {
            mPreviewText.setText(mAlphabetLower);
            mUpperCase = false;
        } else {
            mPreviewText.setText(mAlphabetUpper);
            mUpperCase = true;
        }
        ViewUtils.animSlideInBottom(mPreviewText, getActivity());
    }
}
