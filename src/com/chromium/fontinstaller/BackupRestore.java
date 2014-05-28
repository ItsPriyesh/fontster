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

import java.io.File;
import java.io.IOException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class BackupRestore extends Activity {

	Button backup, restore, deleteBackup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup_restore);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
		.addTestDevice("2797F5D9304B6B3A15771A0519A4F687")  // HTC Desire
		.addTestDevice("D674E5DF79F70B01D8866A5F99A2ACBA") // Samsung i9000
		.build();
		adView.loadAd(adRequest);

		backup = (Button)findViewById(R.id.backup);
		backup.setOnClickListener(new View.OnClickListener() { //copy fonts from system to sd
			public void onClick(View v){

				final Dialog confirm = new Dialog(BackupRestore.this);

				confirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
				confirm.setContentView(R.layout.two_button_alert);	
				TextView alertTitle = (TextView) confirm.findViewById(R.id.title);
				alertTitle.setText("Confirm backup");
				TextView alertMessage = (TextView) confirm.findViewById(R.id.message);
				alertMessage.setText("Are you sure you want to backup your currently installed font?");
				Button positiveButton = (Button) confirm.findViewById(R.id.positive);
				Button negativeButton = (Button) confirm.findViewById(R.id.negative);

				positiveButton.setOnClickListener(new View.OnClickListener() { // confirm yes
					public void onClick(View v){
						confirm.cancel();

						AsyncTask<Void, Void, Void> copyToSD = new AsyncTask<Void, Void, Void>()  { 
							//display progress dialog while fonts are copied in background
							ProgressDialog progressDialog;

							@Override
							protected void onPreExecute() {
								super.onPreExecute();
								progressDialog = new ProgressDialog (BackupRestore.this);
								progressDialog.setMessage("Backing up fonts to SD card...");
								progressDialog.show();
							}

							@Override
							protected Void doInBackground(Void... params) {

								try {
									Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
									Process makeBackupDir = Runtime.getRuntime().exec(new String[] { "su", "-c", "mkdir /sdcard/FontBackup"});
									Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Bold.ttf /sdcard/FontBackup"});
									Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-BoldItalic.ttf /sdcard/FontBackup"});
									Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Regular.ttf /sdcard/FontBackup"});
									Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Italic.ttf /sdcard/FontBackup"}); 
									Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Light.ttf /sdcard/FontBackup"});
									Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-LightItalic.ttf /sdcard/FontBackup"});
									Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-Thin.ttf /sdcard/FontBackup"});
									Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/Roboto-ThinItalic.ttf /sdcard/FontBackup"});
									Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/RobotoCondensed-Bold.ttf /sdcard/FontBackup"});
									Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/RobotoCondensed-BoldItalic.ttf /sdcard/FontBackup"});
									Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/RobotoCondensed-Regular.ttf /sdcard/FontBackup"});
									Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /system/fonts/RobotoCondensed-Italic.ttf /sdcard/FontBackup"});
								} 
								catch (IOException e) {
									e.printStackTrace();
								}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);
								if (progressDialog != null) {
									if (progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
								}
								CustomAlerts.showBasicAlert("Backup complete", "Your current fonts have been safely saved onto your phones storage.", BackupRestore.this);
							}
						};
						copyToSD.execute((Void[])null); 
					}
				});	

				negativeButton.setOnClickListener(new View.OnClickListener() { // confirm no
					public void onClick(View v){
						confirm.cancel();
					}			
				});
				confirm.show();
			}
		});

		restore = (Button)findViewById(R.id.restore);
		restore.setOnClickListener(new View.OnClickListener() { //copy backed up fonts from sd to system
			public void onClick(View v){

				File backupDir = new File("/sdcard/FontBackup");

				if (backupDir.exists()) { //Only restore if a backup exists

					final Dialog confirm2 = new Dialog(BackupRestore.this);

					confirm2.requestWindowFeature(Window.FEATURE_NO_TITLE);
					confirm2.setContentView(R.layout.two_button_alert);	
					TextView alertTitle = (TextView) confirm2.findViewById(R.id.title);
					alertTitle.setText("Confirm restore");
					TextView alertMessage = (TextView) confirm2.findViewById(R.id.message);
					alertMessage.setText("Are you sure you want to restore your previously backed up fonts?");
					Button positiveButton = (Button) confirm2.findViewById(R.id.positive);
					Button negativeButton = (Button) confirm2.findViewById(R.id.negative);

					positiveButton.setOnClickListener(new View.OnClickListener() { // confirm yes
						public void onClick(View v){
							confirm2.cancel();

							AsyncTask<Void, Void, Void> copyToSystem = new AsyncTask<Void, Void, Void>()  { 
								//display progress dialog while fonts are copied in background
								ProgressDialog progressDialog;

								@Override
								protected void onPreExecute() {
									super.onPreExecute();
									progressDialog = new ProgressDialog (BackupRestore.this);
									progressDialog.setMessage("Restoring fonts from SD card to system...");
									progressDialog.show();
								}

								@Override
								protected Void doInBackground(Void... params) {

									try {
										Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
										Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/Roboto-Bold.ttf /system/fonts"});
										Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/Roboto-BoldItalic.ttf /system/fonts"});
										Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/Roboto-Regular.ttf /system/fonts"});
										Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/Roboto-Italic.ttf /system/fonts"}); 
										Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/Roboto-Light.ttf /system/fonts"});
										Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/Roboto-LightItalic.ttf /system/fonts"});
										Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/Roboto-Thin.ttf /system/fonts"});
										Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/Roboto-ThinItalic.ttf /system/fonts"});
										Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/RobotoCondensed-Bold.ttf /system/fonts"});
										Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/RobotoCondensed-BoldItalic.ttf /system/fonts"});
										Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/RobotoCondensed-Regular.ttf /system/fonts"});
										Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/FontBackup/RobotoCondensed-Italic.ttf /system/fonts"});
									} 
									catch (IOException e) {
										e.printStackTrace();
									}
									return null;
								}

								@Override
								protected void onPostExecute(Void result) {
									super.onPostExecute(result);
									if (progressDialog != null) {
										if (progressDialog.isShowing()) {
											progressDialog.dismiss();
										}
									}
									CustomAlerts.showRebootAlert("Restore complete","Your previously backed up fonts were reinstalled. Reboot for the changes to take effect.", "Reboot", BackupRestore.this);
								}
							};
							copyToSystem.execute((Void[])null);
						}
					});
					negativeButton.setOnClickListener(new View.OnClickListener() { // confirm no
						public void onClick(View v){
							confirm2.cancel();
						}			
					});
					confirm2.show();
				}
				else {
					CustomAlerts.showBasicAlert("No backup", "There is currently no backup available. You must create a backup prior to being able to restore.", BackupRestore.this);
				}
			}			
		});

		deleteBackup = (Button)findViewById(R.id.deleteBackup);
		deleteBackup.setOnClickListener(new View.OnClickListener() { //copy fonts from system to sd
			public void onClick(View v){

				File backupDir = new File("/sdcard/FontBackup");
				if (backupDir.exists()) { 

					final Dialog confirm3 = new Dialog(BackupRestore.this);

					confirm3.requestWindowFeature(Window.FEATURE_NO_TITLE);
					confirm3.setContentView(R.layout.two_button_alert);	
					TextView alertTitle = (TextView) confirm3.findViewById(R.id.title);
					alertTitle.setText("Confirm delete");
					TextView alertMessage = (TextView) confirm3.findViewById(R.id.message);
					alertMessage.setText("Are you sure you want to delete your font backup?");
					Button positiveButton = (Button) confirm3.findViewById(R.id.positive);
					Button negativeButton = (Button) confirm3.findViewById(R.id.negative);

					positiveButton.setOnClickListener(new View.OnClickListener() { // confirm yes
						public void onClick(View v){
							confirm3.cancel();

							AsyncTask<Void, Void, Void> delBackup = new AsyncTask<Void, Void, Void>()  { 
								//display progress dialog while fonts are copied in background
								ProgressDialog prog;

								@Override
								protected void onPreExecute() {
									super.onPreExecute();
									prog = new ProgressDialog (BackupRestore.this);
									prog.setMessage("Deleting backup...");
									prog.show();
								}

								@Override
								protected Void doInBackground(Void... params) {

									String backup = "rm -r /sdcard/FontBackup";
									Runtime runtime = Runtime.getRuntime();
									try {
										runtime.exec(backup);
									}
									catch (IOException e) { 

									}
									return null;
								}

								@Override
								protected void onPostExecute(Void result) {
									super.onPostExecute(result);
									if (prog != null) {
										if (prog.isShowing()) {
											prog.dismiss();
										}
									}
									CustomAlerts.showBasicAlert ("Done", "Your backup has been deleted.", BackupRestore.this);

								}
							};
							delBackup.execute((Void[])null);
						}
					});
					negativeButton.setOnClickListener(new View.OnClickListener() { // confirm no
						public void onClick(View v){
							confirm3.cancel();
						}			
					});
					confirm3.show();
				}
				else {
					CustomAlerts.showBasicAlert("No backup", "You have not made a backup.", BackupRestore.this);
				}
			}			
		});
	}
}
