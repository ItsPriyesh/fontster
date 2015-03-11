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

package com.chromium.fontinstaller.ui.fontlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.ui.fontinstall.FontActivity;
import com.chromium.fontinstaller.util.FileUtils;
import com.chromium.fontinstaller.util.PreferencesManager;

import java.util.ArrayList;

/**
 * Created by priyeshpatel on 15-02-15.
 */
public class FontListAdapter extends RecyclerView.Adapter<FontListAdapter.ViewHolder> {

    private PreferencesManager prefs;
    private static ArrayList<String> fontNames;
    private static Context context;
    private static boolean enableTrueFont;
    private static LruCache<String, Typeface> fontCache;


    public FontListAdapter(Context context, ArrayList<String> fontNames, boolean enableTrueFont) {
        this.fontNames = fontNames;
        this.context = context;
        this.enableTrueFont = enableTrueFont;

        setHasStableIds(true);
        prefs = PreferencesManager.getInstance(context);

        fontCache = new LruCache<>(FileUtils.getMaxCacheSize(context));
    }

    @Override
    public FontListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.font_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String currentFontName = fontNames.get(position);

        holder.fontName.setText(currentFontName);

        if (enableTrueFont) {
            Typeface currentFont = fontCache.get(currentFontName);
            if (currentFont == null) {
                currentFont = new FontPackage(currentFontName).getTypeface(Style.REGULAR);
                fontCache.put(currentFontName, currentFont);
            }
            holder.fontName.setTypeface(currentFont);
        }
    }

    @Override
    public long getItemId(int position) {
        return fontNames.get(position).hashCode();
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