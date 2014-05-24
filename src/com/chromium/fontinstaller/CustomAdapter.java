package com.chromium.fontinstaller;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
						" Please go to the settings of this app, choose the 'Don't display fonts in list' option," +
						" then try again.", context.getApplicationContext());
			}	
		}
		return row;
	}

	public static String removeSpaces (String line)
	{//method to remove spaces
		for (int x = 0 ; x < line.length () ; x++)
		{
			if (line.charAt (x) == ' ')
			{
				String newLine = line.substring (0, x) + line.substring (x+1);
				return removeSpaces (newLine);
			}
		}
		return line;
	}
}