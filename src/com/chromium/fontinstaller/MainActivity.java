package com.chromium.fontinstaller;

import java.io.File;
import java.io.IOException;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	Button openFontList, cleanFontDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try{
			Process getSU = Runtime.getRuntime().exec("su");
		}
		catch(IOException e){
			Toast.makeText(getApplicationContext(), "y u no root",
					-							   Toast.LENGTH_LONG).show();		
		}
		openFontList = (Button)findViewById(R.id.openFontList);
		openFontList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent fontListActivity = new Intent(MainActivity.this, FontList.class);
				startActivity(fontListActivity);

			}
		});

		cleanFontDir = (Button)findViewById(R.id.cleanFontDir);
		cleanFontDir.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				File file = new File("/sdcard/DownloadedFonts");

				if (file.exists()) {
					String wipe = "rm -r /sdcard/DownloadedFonts";
					Runtime runtime = Runtime.getRuntime();
					try {
						runtime.exec(wipe);
					} catch (IOException e) { 

					}
				}
				else
					Toast.makeText(getApplicationContext(), "Nothing to clean.",
							-							   Toast.LENGTH_LONG).show();
			}
		});

	}

}
