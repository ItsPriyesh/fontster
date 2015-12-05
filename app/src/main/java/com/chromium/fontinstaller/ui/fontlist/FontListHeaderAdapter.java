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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;

import java.util.List;

public final class FontListHeaderAdapter implements StickyHeadersAdapter<FontListHeaderAdapter.ViewHolder> {

  private final List<String> mItems;

  public FontListHeaderAdapter(List<String> items) {
    this.mItems = items;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent) {
    View itemView = LayoutInflater.from(
        parent.getContext()).inflate(R.layout.font_list_header_item, parent, false);

    return new ViewHolder(itemView);
  }

  @Override public void onBindViewHolder(ViewHolder headerViewHolder, int position) {
    headerViewHolder.letter.setText(mItems.get(position).subSequence(0, 1));
  }

  @Override public long getHeaderId(int position) {
    return mItems.get(position).charAt(0);
  }

  public final static class ViewHolder extends RecyclerView.ViewHolder {
    final TextView letter;

    public ViewHolder(View itemView) {
      super(itemView);
      letter = (TextView) itemView.findViewById(R.id.letter);
    }
  }
}