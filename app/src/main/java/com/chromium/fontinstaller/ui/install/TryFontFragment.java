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
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public final class TryFontFragment extends DialogFragment {

    @Bind(R.id.title)
    TextView mTitleView;
    @Bind(R.id.input)
    EditText mInputView;

    private FontPackage mFontPackage;

    private Style mStyle;
    private Action1<String> mTryCallback;

    private static final String FONT_NAME_KEY = "font_name";

    public TryFontFragment() { }

    public static TryFontFragment newInstance(FontPackage fontPackage, Style style, Action1<String> tryCallback) {
        TryFontFragment fragment = new TryFontFragment();
        fragment.setFontPackage(fontPackage);
        fragment.setFontStyle(style);
        fragment.setTryCallback(tryCallback);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FONT_NAME_KEY, mFontPackage.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_try_font, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            mFontPackage = new FontPackage(savedInstanceState.getString(FONT_NAME_KEY));
        }

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mTitleView.setText(mFontPackage.getName());
        mInputView.setTypeface(mFontPackage.getTypeface(mStyle));

        return view;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.done_button)
    public void doneButtonClicked() {
        mTryCallback.call(mInputView.getText().toString());
        dismiss();
    }

    private void setTryCallback(Action1<String> callback) {
        this.mTryCallback = callback;
    }

    private void setFontPackage(FontPackage fontPackage) {
        this.mFontPackage = fontPackage;
    }

    private void setFontStyle(Style style) {
        this.mStyle = style;
    }

}
