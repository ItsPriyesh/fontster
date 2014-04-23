package com.chromium.fontinstaller;

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
	String fallbackCondensed = "https://github.com/Chromium1/Fonts/raw/master/StockRoboto/RobotoCondensed-Regular.ttf";
	String fallbackLight = "https://github.com/Chromium1/Fonts/raw/master/StockRoboto/Roboto-Light.ttf";
	
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
				Process makeFallbackDir = Runtime.getRuntime().exec(new String[] { "su", "-c", "mkdir /sdcard/FontFallback"});
			}
			catch(IOException e){
				Toast.makeText(getApplicationContext(), "You dont have root.", Toast.LENGTH_LONG).show();
			}

			DownloadManager.Request downloadStockFontZip = new DownloadManager.Request(Uri.parse(stockFontURL));
			downloadStockFontZip.allowScanningByMediaScanner();
			downloadStockFontZip.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "RestoreStockFonts.zip");
			downloadStockFontZip.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
			
			DownloadManager.Request downloadCondensedFallback = new DownloadManager.Request(Uri.parse(fallbackCondensed));
			downloadCondensedFallback.allowScanningByMediaScanner();
			downloadCondensedFallback.setDestinationInExternalPublicDir("/FontFallback/", "RobotoCondensed-Regular.ttf");
			downloadCondensedFallback.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

			DownloadManager.Request downloadLightFallback = new DownloadManager.Request(Uri.parse(fallbackLight));
			downloadLightFallback.allowScanningByMediaScanner();
			downloadLightFallback.setDestinationInExternalPublicDir("/FontFallback/", "Roboto-Light.ttf");
			downloadLightFallback.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
			
			DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			manager.enqueue(downloadStockFontZip);
			manager.enqueue(downloadCondensedFallback);
			manager.enqueue(downloadLightFallback);
			
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
