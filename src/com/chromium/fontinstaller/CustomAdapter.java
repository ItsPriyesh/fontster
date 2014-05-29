/*
 * Copyright (C) 2014 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chromium.fontinstaller;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {
	Context context;
	int layoutResourceId;
	int textViewResourceId;
	ArrayList<String> data = new ArrayList<String>();

	public CustomAdapter(Context context, int layoutResourceId, int textViewResourceId, ArrayList<String> fontList) {

		super(context, layoutResourceId,textViewResourceId, fontList);
		this.layoutResourceId = layoutResourceId;
		this.textViewResourceId = textViewResourceId;
		this.context = context;
		this.data = fontList;
	}

	/**
	 * This is called when the FontList activity is opened in order to
	 * populate the list. First it checks if the directory of each font
	 * exists - if it exists, it will display a checkmark indicator beside
	 * that font - if it does not exist, no checkmark will be displayed.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater(); 
		row = inflater.inflate(layoutResourceId, parent, false);

		ImageView alreadyDownloaded = (ImageView) row.findViewById(R.id.alreadyDownloaded);
		TextView textView = (TextView) row.findViewById(R.id.fontTextView);
		textView.setText(data.get(position));

		File dfDir = new File(Environment.getExternalStorageDirectory() + "/DownloadedFonts/"+ (removeSpaces(data.get(position))));

		if(dfDir.isDirectory()) {
			alreadyDownloaded.setVisibility(View.VISIBLE);
		}

		String fontNameWithoutSpaces = (removeSpaces(data.get(position)));

		File previewListDir = new File(Environment.getExternalStorageDirectory() + "/ListPreviews");

		if (previewListDir.isDirectory()) {

			File specificFontFile = new File(Environment.getExternalStorageDirectory() + "/ListPreviews/" + fontNameWithoutSpaces + "FontPack/Roboto-Regular.ttf");
			if (specificFontFile.exists()){
				// create new typeface from /sdcard/previewfont 
				Typeface preview = Typeface.createFromFile("/sdcard/ListPreviews/" + fontNameWithoutSpaces + "FontPack/Roboto-Regular.ttf");
				// set textView to the new typeface
				textView.setTypeface(preview);
			}
			else {
				CustomAlerts.showBasicAlert("Unable to load fonts", "Some fonts failed to be set." +
						" Please go to the settings of this app, choose the 'Disable True Font Display' option," +
						" then try again.", context);
			}	
		}
		return row;
	}

	/**
	 * This method removes spaces from strings and is used to construct
	 * download URL's from the original font names which usually have spaces
	 * in them. The URL must not contain spaces.
	 * 
	 * @param line is the the input string that is to be processed
	 * @return The initial inputted string is returned as a new string containing no spaces
	 */
	public static String removeSpaces (String line) {
		for (int x = 0 ; x < line.length () ; x++) {
			if (line.charAt (x) == ' '){
				String newLine = line.substring (0, x) + line.substring (x+1);
				return removeSpaces (newLine);
			}
		}
		return line;
	}
}
