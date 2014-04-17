package com.chromium.fontinstaller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	SharedPreferences prefs = null;
	Button openFontList, cleanFontDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

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

	@Override
	protected void onResume() {
		super.onResume();

		if (prefs.getBoolean("firstrun", true)) { //stuff to do on first app opening

			try{
				Process getSU = Runtime.getRuntime().exec("su");
			}
			catch(IOException e){
				Toast.makeText(getApplicationContext(), "You dont have root.", Toast.LENGTH_LONG).show();
			}

			CopyAssets();

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Welcome! A flashable zip of the stock fonts has been copied onto your phone. In the event that you encounter a bootloop, enter recovery and flash the zip found on your SD card named 'StockFonts.zip'.");
			builder.setCancelable(true);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();

			prefs.edit().putBoolean("firstrun", false).commit();
		}
	}

	private void CopyAssets() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("Files");
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		for(String filename : files) {
			System.out.println("File name => "+filename);
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open("Files/"+filename);
				out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() +"/" + filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch(Exception e) {
				Log.e("tag", e.getMessage());
			}
		}
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

}
