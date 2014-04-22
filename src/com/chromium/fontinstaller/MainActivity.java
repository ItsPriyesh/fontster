package com.chromium.fontinstaller;

import java.io.File;
import java.io.IOException;
import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	SharedPreferences prefs = null;
	Button openFontList, backup;

	String stockFontURL = "https://github.com/Chromium1/Fonts/raw/master/RestoreStockFonts.zip";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_card);
		prefs = getSharedPreferences("com.chromium.fontinstaller", MODE_PRIVATE);

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

		backup = (Button)findViewById(R.id.backup);
		backup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				if(v == backup) {
					backup.setBackgroundResource(R.drawable.layer_card_background_pressed);
				}
				Intent backupRestoreActivity = new Intent(MainActivity.this, BackupRestore.class);
				startActivity(backupRestoreActivity);
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (prefs.getBoolean("firstrun", true)) { //stuff to do on first app opening

			try{
				Process getSU = Runtime.getRuntime().exec("su");
				Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
				Process makeFallbackDir = Runtime.getRuntime().exec(new String[] { "su", "-c", "mkdir /sdcard/FontFallback"});
				Process process1 = Runtime.getRuntime().exec(new String[] { "cp /system/fonts/Roboto-Bold.ttf /sdcard/FontFallback"});
				Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-BoldItalic.ttf /sdcard/FontFallback"});
				Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Regular.ttf /sdcard/FontFallback"});
				Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Italic.ttf /sdcard/FontFallback"}); 
				Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Light.ttf /sdcard/FontFallback"});
				Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-LightItalic.ttf /sdcard/FontFallback"});
				Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Thin.ttf /sdcard/FontFallback"});
				Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-ThinItalic.ttf /sdcard/FontFallback"});
				Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/RobotoCondensed-Bold.ttf /sdcard/FontFallback"});
				Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/RobotoCondensed-BoldItalic.ttf /sdcard/FontFallback"});
				Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/RobotoCondensed-Regular.ttf /sdcard/FontFallback"});
				Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/RobotoCondensed-Italic.ttf /sdcard/FontFallback"});
			}
			catch(IOException e){
				Toast.makeText(getApplicationContext(), "You dont have root.", Toast.LENGTH_LONG).show();
			}

			DownloadManager.Request downloadStockFontZip = new DownloadManager.Request(Uri.parse(stockFontURL));
			downloadStockFontZip.allowScanningByMediaScanner();
			downloadStockFontZip.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "RestoreStockFonts.zip");
			downloadStockFontZip.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
			
			DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			manager.enqueue(downloadStockFontZip);
			
			showCustomWelcomeAlert ("Welcome!", "It is strongly suggested that you backup your current fonts using " +
					"the backup option found in this app prior to installing any custom ones.\n\nFor further safety, " +
					"a recovery flashable zip of the stock fonts has been placed in your downloads folder. In the " +
					"unlikely, but possible event that you encounter issues, please flash this zip.\n");
			
			prefs.edit().putBoolean("firstrun", false).commit();
		}
	}
	
	public void showCustomWelcomeAlert (String title, String message) { 
		final Dialog reboot = new Dialog(this);

		reboot.requestWindowFeature(Window.FEATURE_NO_TITLE);
		reboot.setContentView(R.layout.alert_buttons);	
		TextView alertTitle = (TextView) reboot.findViewById(R.id.title);
		alertTitle.setText(title);
		TextView alertMessage = (TextView) reboot.findViewById(R.id.message);
		alertMessage.setText(message);
		Button positiveButton = (Button) reboot.findViewById(R.id.positive);
		positiveButton.setText("OK");

		positiveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				reboot.dismiss();
			}			
		});

		reboot.show();
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
