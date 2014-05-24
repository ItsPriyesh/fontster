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
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	//TODO
	// - Remove Install from Storage option
	// - Display each fontname in its actual font (WIP)
	// - Make bars translucent in other activities
	
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

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
		.addTestDevice("2797F5D9304B6B3A15771A0519A4F687")  // HTC Desire
		.addTestDevice("D674E5DF79F70B01D8866A5F99A2ACBA") // Samsung i9000
		.build();
		adView.loadAd(adRequest);	

		openFontList = (Button)findViewById(R.id.installFont);
		openFontList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent fontListActivity = new Intent(MainActivity.this, FontList.class);
				startActivity(fontListActivity);

			}
		});

		backup = (Button)findViewById(R.id.backup);
		backup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent backupRestoreActivity = new Intent(MainActivity.this, BackupRestore.class);
				startActivity(backupRestoreActivity);
			}
		});

		testView = (Button)findViewById(R.id.test);
		testView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent testViewActivity = new Intent(MainActivity.this, TestView.class);
				startActivity(testViewActivity);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (prefs.getBoolean("firstrun", true)) { //stuff to do on first app opening

			Intent openSplash = new Intent (MainActivity.this, Splash.class);
			startActivity(openSplash);
			
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

	private void openShare() {
		Intent share = new Intent(); 
		share.setAction(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, "Check out Fontster, at fontster.cf!" );  
		startActivity(Intent.createChooser(share, "Share Fontster"));
	}
}
