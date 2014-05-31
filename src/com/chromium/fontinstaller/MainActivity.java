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

import java.io.IOException;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.support.v7.app.ActionBarActivity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	//TODO
	/*
	 * 
	 */
	
	SharedPreferences prefs = null;
	Button openFontList, backup, testView;

	String stockFontURL = "https://github.com/Chromium1/Fonts/raw/master/RestoreStockFonts.zip";
	String fallbackCondensed = "https://github.com/Chromium1/Fonts/raw/master/StockRoboto/RobotoCondensed-Regular.ttf";
	String fallbackLight = "https://github.com/Chromium1/Fonts/raw/master/StockRoboto/Roboto-Light.ttf";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_card);
		prefs = getSharedPreferences("com.chromium.fontinstaller", MODE_PRIVATE);

		// Check if device is running KitKat or higher and enable translucent system bars for better aesthetics
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
	    	Window win = getWindow();
	    	win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		
		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
		.addTestDevice("2797F5D9304B6B3A15771A0519A4F687")  // HTC Desire
		.addTestDevice("D674E5DF79F70B01D8866A5F99A2ACBA") // Samsung i9000
		.build();
		adView.loadAd(adRequest);	

		// Open Install a font
		openFontList = (Button)findViewById(R.id.installFont);
		openFontList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent fontListActivity = new Intent(MainActivity.this, FontList.class);
				startActivity(fontListActivity);

			}
		});

		// Open Backup and restore fonts
		backup = (Button)findViewById(R.id.backup);
		backup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent backupRestoreActivity = new Intent(MainActivity.this, BackupRestore.class);
				startActivity(backupRestoreActivity);
			}
		});

		// Open View currently installed fonts
		testView = (Button)findViewById(R.id.test);
		testView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent testViewActivity = new Intent(MainActivity.this, TestView.class);
				startActivity(testViewActivity);
			}
		});
	}

	/**
	 * Called only on the first run of the app.
	 * This downloads in the background a recovery
	 * zip that can be installed in the event that
	 * the users device becomes soft bricked. It also
	 * attempts to gain root access to the device for
	 * future operations.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (prefs.getBoolean("firstrun", true)) { 
			Intent openSplash = new Intent (MainActivity.this, Splash.class);
			startActivity(openSplash);
			
			try{
				Process getSU = Runtime.getRuntime().exec("su");
				Process makeFallbackDir = Runtime.getRuntime().exec(new String[] { "su", "-c", "mkdir -p /sdcard/Fontster/FontFallback"});
			}
			catch(IOException e){
				CustomAlerts.showBasicAlert ("You don't have root",
				"In order to use this app your device must be rooted. Fontster will not work properly without root", 
				MainActivity.this);
			}

			DownloadManager.Request downloadStockFontZip = new DownloadManager.Request(Uri.parse(stockFontURL));
			downloadStockFontZip.allowScanningByMediaScanner();
			downloadStockFontZip.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "RestoreStockFonts.zip");
			downloadStockFontZip.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

			DownloadManager.Request downloadCondensedFallback = new DownloadManager.Request(Uri.parse(fallbackCondensed));
			downloadCondensedFallback.allowScanningByMediaScanner();
			downloadCondensedFallback.setDestinationInExternalPublicDir("/Fontster/FontFallback/", "RobotoCondensed-Regular.ttf");
			downloadCondensedFallback.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

			DownloadManager.Request downloadLightFallback = new DownloadManager.Request(Uri.parse(fallbackLight));
			downloadLightFallback.allowScanningByMediaScanner();
			downloadLightFallback.setDestinationInExternalPublicDir("/Fontster/FontFallback/", "Roboto-Light.ttf");
			downloadLightFallback.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

			DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			manager.enqueue(downloadStockFontZip);
			manager.enqueue(downloadCondensedFallback);
			manager.enqueue(downloadLightFallback);
			
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
	public boolean onOptionsItemSelected(MenuItem menuItem) { //about button in actionbar
		switch (menuItem.getItemId()) {
		case R.id.menu_about:
			openAbout();
			return true;
		case R.id.share:
			openShare();
			return true;
		default:
			return super.onOptionsItemSelected(menuItem);
		}
	}   

	private void openAbout() { //open about section
		Intent about = new Intent(MainActivity.this, About.class);
		startActivity(about);
	}

	/**
	 * Opens the share intent to allow
	 * the user to send a link of Fontster
	 * via other social applications.
	 */
	private void openShare() {
		Intent share = new Intent(); 
		share.setAction(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, "Check out Fontster, http://goo.gl/ybK5ST!" );  
		startActivity(Intent.createChooser(share, "Share Fontster"));
	}
}
