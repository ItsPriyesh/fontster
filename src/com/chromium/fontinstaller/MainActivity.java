package com.chromium.fontinstaller;

import java.io.File;
import java.io.IOException;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	SharedPreferences prefs = null;
	Button openFontList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_card);
		prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

		openFontList = (Button)findViewById(R.id.installFont);
		openFontList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				if(v == openFontList) {
					openFontList.setBackgroundResource(R.drawable.layer_card_background_pressed);
				}
				Intent fontListActivity = new Intent(MainActivity.this, FontList.class);
				startActivity(fontListActivity);

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


			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Welcome!");
			builder.setMessage("A flashable zip of the stock fonts has been copied onto your phone. In the event that you encounter a bootloop, enter recovery and flash the zip found on your SD card named 'StockFonts.zip'.");
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == R.id.menu_about) { //about button in actionbar
			openAbout();
			return true;
		}
		return false;
	}   

	private void openAbout() { //open about section
		Intent about = new Intent(MainActivity.this, About.class);
		startActivity(about);
	}
}
