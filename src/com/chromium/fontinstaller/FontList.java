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

	String fontName, selectedFromList;
	
	//Font url strings
	String urlRobotoBold, urlRobotoBoldItalic, urlRobotoItalic, 
	urlRobotoLight, urlRobotoLightItalic, urlRobotoRegular, urlRobotoThin, 
	urlRobotoThinItalic, urlRobotoCondensedBold, urlRobotoCondensedBoldItalic, 
	urlRobotoCondensedItalic, urlRobotoCondensedRegular;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.font_list);

		lv = (ListView) findViewById(R.id.listView1);

		urlRobotoBold = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Bold.ttf";
		urlRobotoBoldItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-ItalicBold.ttf";
		urlRobotoRegular = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Regular.ttf";
		urlRobotoItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Italic.ttf";		
		urlRobotoLight = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Light.ttf";
		urlRobotoLightItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-LightItalic.ttf";
		urlRobotoThin = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Thin.ttf";
		urlRobotoThinItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-ThinItalic.ttf";
		urlRobotoCondensedBold = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Bold.ttf";
		urlRobotoCondensedBoldItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-BoldItalic.ttf";
		urlRobotoCondensedRegular = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Regular.ttf";
		urlRobotoCondensedItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Italic.ttf";
		
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

				fontName = removeSpaces(selectedFromList); //remove the spaces from the item so that it can later be passed into the URL string

				Toast.makeText(getApplicationContext(), fontName,
						Toast.LENGTH_LONG).show();	        	
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