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

package com.chromium.fontinstaller.ui;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.ui.common.FontListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FontListFragment extends Fragment {

    @InjectView(R.id.font_list_view)
    RecyclerView recyclerView;

    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager listManager;
    private ArrayList<String> fontList;
    private Activity activity;

    public FontListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_font_list, container, false);
        ButterKnife.inject(this, view);
        activity = getActivity();

        fontList = new ArrayList<>();
        populateFontList();

        listAdapter = new FontListAdapter(activity, fontList);
        listManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(listManager);
        recyclerView.setAdapter(listAdapter);

        return view;
    }

    private void populateFontList() {
        try {
            Scanner scanner = new Scanner(activity.getAssets().open("fonts"));
            while (scanner.hasNextLine()) {
                fontList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {

        }
    }

}
