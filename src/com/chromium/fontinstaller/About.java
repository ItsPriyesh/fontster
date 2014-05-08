package com.chromium.fontinstaller;

import java.io.File;
import java.io.IOException;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class About extends PreferenceActivity {

	int easterEggClicks = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about_preference);	

		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		Preference clearCache = (Preference) findPreference("clearCache");
		clearCache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {

				File downloadDir = new File("/sdcard/DownloadedFonts");
				File previewDir = new File("/sdcard/SampleFonts");

				if (downloadDir.exists() || previewDir.exists()) {
					AsyncTask<Void, Void, Void> cleanCache = new AsyncTask<Void, Void, Void>()  { 
						//display progress dialog while fonts are copied in background
						ProgressDialog cleanCacheProgress;

						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							cleanCacheProgress = new ProgressDialog (About.this);
							cleanCacheProgress.setMessage("Clearing downloaded fonts...");
							cleanCacheProgress.show();
						}

						@Override
						protected Void doInBackground(Void... params) {

							String downloads = "rm -r /sdcard/DownloadedFonts";
							String previews = "rm -r /sdcard/SampleFonts";
							Runtime runtime = Runtime.getRuntime();
							try {
								runtime.exec(downloads);
								runtime.exec(previews);
							}
							catch (IOException e) { 

							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							if (cleanCacheProgress != null) {
								if (cleanCacheProgress.isShowing()) {
									cleanCacheProgress.dismiss();
								}
							}
							CustomAlerts.showBasicAlert ("Done", "Cached fonts have been deleted.", About.this);

						}
					};
					cleanCache.execute((Void[])null);

				}
				else
					CustomAlerts.showBasicAlert ("Nothing to clean", "There are currently no cached fonts.", About.this);
				return true; 
			}
		});

		Preference requestFont = (Preference) findPreference("fontRequest");
		requestFont.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				// Create a new dialog with a text edit (font name input)
				// and a button to send the request to me via email

				CustomAlerts.showRequestAlert(About.this);
				
				return true; 
			}
		});

		Preference reboot = (Preference) findPreference("reboot");
		reboot.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {

				final Dialog confirm = new Dialog(About.this);

				confirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
				confirm.setContentView(R.layout.two_button_alert);	
				TextView alertTitle = (TextView) confirm.findViewById(R.id.title);
				alertTitle.setText("Confirm reboot");
				TextView alertMessage = (TextView) confirm.findViewById(R.id.message);
				alertMessage.setText("Are you sure you want to reboot?");
				Button positiveButton = (Button) confirm.findViewById(R.id.positive);
				Button negativeButton = (Button) confirm.findViewById(R.id.negative);

				positiveButton.setOnClickListener(new View.OnClickListener() { // confirm yes
					public void onClick(View v){
						confirm.cancel();
						try {
							Process softReboot = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot"});	
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				});	
				negativeButton.setOnClickListener(new View.OnClickListener() { // confirm no
					public void onClick(View v){
						confirm.cancel();
					}			
				});
				confirm.show();
				return true; 
			}
		});

		Preference restartSysUI = (Preference) findPreference("restartSysUI");
		restartSysUI.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				try {
					Process restartSysUI = Runtime.getRuntime().exec(new String[] { "su", "-c", "pkill com.android.systemui"});	
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				return true; 
			}
		});

		Preference source = (Preference) findPreference("source");
		source.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent git = new Intent();
				git.setAction(Intent.ACTION_VIEW);
				git.addCategory(Intent.CATEGORY_BROWSABLE);
				git.setData(Uri.parse("https://github.com/Chromium1/FontInstaller"));
				startActivity(git);
				return true; 
			}
		});

		Preference site = (Preference) findPreference("website");
		site.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent site = new Intent();
				site.setAction(Intent.ACTION_VIEW);
				site.addCategory(Intent.CATEGORY_BROWSABLE);
				site.setData(Uri.parse("https://fontster.cf"));
				startActivity(site);
				return true; 
			}
		});

		Preference contact = (Preference) findPreference("contact");
		contact.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent email = new Intent();
				email.setAction(Intent.ACTION_VIEW);
				email.addCategory(Intent.CATEGORY_BROWSABLE);
				email.setData(Uri.parse("mailto:priyesh.96@hotmail.com"));
				startActivity(email);
				return true; 
			}
		});

		Preference donate = (Preference) findPreference("donate");
		donate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent donate = new Intent();
				donate.setAction(Intent.ACTION_VIEW);
				donate.addCategory(Intent.CATEGORY_BROWSABLE);
				donate.setData(Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations" +
						"&business=XR9WBFXGZ9G5E&lc=CA&item_name=Font%20Installer&currency_code" +
						"=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted"));
				startActivity(donate);
				return true; 
			}
		});

		Preference ethan = (Preference) findPreference("ethan");
		ethan.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				easterEggClicks--;
				if (easterEggClicks < 0){
					Intent aboutEE = new Intent(About.this, AboutEE.class);
					startActivity(aboutEE);
					easterEggClicks = 10;
				}
				return true; 
			}
		});
	}

}
