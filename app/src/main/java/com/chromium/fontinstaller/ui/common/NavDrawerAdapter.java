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
import android.widget.ImageView;
import android.widget.TextView;

import com.chromium.fontinstaller.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by priyeshpatel on 15-02-18.
 */
public class NavDrawerAdapter extends ArrayAdapter<NavDrawerItem> {
    private ArrayList<NavDrawerItem> items;

    public NavDrawerAdapter(Context context, ArrayList<NavDrawerItem> items) {
        super(context, 0, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.nav_drawer_item, parent, false);

        TextView title = ButterKnife.findById(convertView, R.id.title);
        ImageView icon = ButterKnife.findById(convertView, R.id.icon);

        title.setText(items.get(position).getTitle());
        icon.setImageDrawable(items.get(position).getIcon());

        return convertView;
    }
}
