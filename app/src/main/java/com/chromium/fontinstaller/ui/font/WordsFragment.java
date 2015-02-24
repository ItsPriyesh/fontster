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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.common.AutoScaleTextView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WordsFragment extends Fragment {

    @InjectView(R.id.text1)
    AutoScaleTextView text1;

    @InjectView(R.id.text2)
    AutoScaleTextView text2;

    @InjectView(R.id.text3)
    AutoScaleTextView text3;

    @InjectView(R.id.text4)
    AutoScaleTextView text4;

    private static String[] words;
    private AutoScaleTextView[] textViews;
    private FontPackage fontPackage;

    public WordsFragment() {

    }

    public static WordsFragment newInstance(FontPackage fontPackage) {
        WordsFragment fragment = new WordsFragment();
        fragment.setFontPackage(fontPackage);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_words, container, false);
        ButterKnife.inject(this, view);
        textViews = new AutoScaleTextView[] {text1, text2, text3, text4};
        words = getActivity().getResources().getStringArray(R.array.words);

        Random random = new Random();
        text1.setTypeface(fontPackage.getFont(Style.BOLD));
        text2.setTypeface(fontPackage.getFont(Style.REGULAR));
        text3.setTypeface(fontPackage.getFont(Style.LIGHT));
        text4.setTypeface(fontPackage.getFont(Style.THIN));

        Set set = new HashSet();
        while (set.size() < textViews.length) set.add(random.nextInt(words.length));
        for (AutoScaleTextView textView : textViews) {
            textView.setText(set.);
        }

        return view;
    }

    public void setFontPackage(FontPackage fontPackage) {
        this.fontPackage = fontPackage;
    }

}
