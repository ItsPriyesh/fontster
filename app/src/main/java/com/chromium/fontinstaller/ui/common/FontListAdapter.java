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

package com.chromium.fontinstaller.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;

import java.util.ArrayList;

/**
 * Created by priyeshpatel on 15-02-15.
 */
public class FontListAdapter extends ArrayAdapter<FontPackage> {

    private ArrayList<FontPackage> fonts;

    public FontListAdapter(Context context, ArrayList<FontPackage> fonts) {
        super(context, 0, fonts);
        this.fonts = fonts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.font_list_item, parent, false);
        }

        TextView fontName = (TextView) convertView.findViewById(R.id.font_name);
        fontName.setText(fonts.get(position).getName());
        return convertView;
    }
}