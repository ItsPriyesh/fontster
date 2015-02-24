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

package com.chromium.fontinstaller.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.ui.font.FontActivity;

import java.util.ArrayList;

/**
 * Created by priyeshpatel on 15-02-15.
 */
public class FontListAdapter extends RecyclerView.Adapter<FontListAdapter.ViewHolder> {

    private static ArrayList<String> fontNames;

    private static Context context;

    public FontListAdapter(Context context, ArrayList<String> fontNames) {
        this.fontNames = fontNames;
        this.context = context;
    }

    @Override
    public FontListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.font_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.fontName.setText(fontNames.get(position));
    }

    @Override
    public int getItemCount() {
        return fontNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView fontName;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            fontName = (TextView) view;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, FontActivity.class);
            intent.putExtra("FONT_NAME", fontNames.get(getPosition()));
            context.startActivity(intent);
        }
    }

}