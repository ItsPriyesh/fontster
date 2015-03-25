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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

public class TryFontFragment extends SupportBlurDialogFragment {

    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.input)
    EditText input;

    private FontPackage fontPackage;
    private Style style;

    public TryFontFragment() {
        // Required empty public constructor
    }

    public static TryFontFragment newInstance(FontPackage fontPackage, Style style) {
        TryFontFragment fragment = new TryFontFragment();
        fragment.setFontPackage(fontPackage);
        fragment.setFontStyle(style);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_try_font, container, false);
        ButterKnife.inject(this, view);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        title.setText(fontPackage.getName());
        input.setTypeface(fontPackage.getTypeface(style));

        return view;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.done_button)
    public void doneButtonClicked() {
        dismiss();
    }

    private void setFontPackage(FontPackage fontPackage) {
        this.fontPackage = fontPackage;
    }

    private void setFontStyle(Style style) {
        this.style = style;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return true;
    }

    @Override
    protected boolean isDimmingEnable() {
        return true;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return true;
    }

}
