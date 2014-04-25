package com.chromium.fontinstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater(); 
		row = inflater.inflate(layoutResourceId, parent, false);


		String item = data.get(position);
		ImageView ib = (ImageView)row.findViewById(R.id.alreadyDownloaded);
		TextView textView = (TextView)row.findViewById(R.id.fontTextView);
		textView.setText(data.get(position));



		File dfDir = new File(Environment.getExternalStorageDirectory() + "/DownloadedFonts/"+ (removeSpaces(data.get(position))));
		
		if(dfDir.isDirectory()) {
			//if (item.equals("Aleo") ){
			ib.setVisibility(View.VISIBLE);
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