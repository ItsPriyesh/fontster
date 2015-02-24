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

package com.chromium.fontinstaller.ui.font;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.common.AutoScaleTextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class PreviewFragment extends Fragment {

    @InjectView(R.id.preview_text)
    AutoScaleTextView previewText;

    private FontPackage fontPackage;

    public PreviewFragment() {

    }

    public static PreviewFragment newInstance(FontPackage fontPackage) {
        PreviewFragment fragment = new PreviewFragment();
        fragment.setFontPackage(fontPackage);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        ButterKnife.inject(this, view);
        previewText.setTypeface(fontPackage.getFont(Style.REGULAR));
        previewText.setText(getResources().getString(R.string.alphabet_upper));

        return view;
    }

    public void setFontPackage(FontPackage fontPackage) {
        this.fontPackage = fontPackage;
    }

}
