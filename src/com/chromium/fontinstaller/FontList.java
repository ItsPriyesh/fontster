package com.chromium.fontinstaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.*;

public class FontList extends Activity {

	private ListView lv;

	String fontURL, fontName, selectedFromList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.font_list);

		lv = (ListView) findViewById(R.id.listView1);

		fontURL = "https://github.com/Chromium1/Fonts/tree/master/" + fontName + "FontPack";

		ArrayList<String> fontList = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("fonts.txt")));

			String line = br.readLine();
			while (line != null) {
				fontList.add(line);
				line = br.readLine();
			}
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fontList);

		lv.setAdapter(arrayAdapter); 
		
	    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View clickView, int position, long id) {
	        	selectedFromList = (lv.getItemAtPosition(position).toString());
	        	
	        	Toast.makeText(getApplicationContext(), selectedFromList,
						   Toast.LENGTH_LONG).show();
	        	
	        	fontName = removeSpaces(selectedFromList); //remove the spaces from the item so that it can later be passed into the URL string
	        }
	    });			
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