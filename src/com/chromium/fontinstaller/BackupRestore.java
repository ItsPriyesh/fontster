package com.chromium.fontinstaller;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BackupRestore extends Activity {

	Button backup, restore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup_restore);

		backup = (Button)findViewById(R.id.backup);
		backup.setOnClickListener(new View.OnClickListener() { //copy fonts from system to sd
			public void onClick(View v){

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
					}
				};
				copyToSD.execute((Void[])null);

			}
		});

		restore = (Button)findViewById(R.id.restore);
		restore.setOnClickListener(new View.OnClickListener() { //copy backed up fonts from sd to system
			public void onClick(View v){

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
					}
				};
				copyToSystem.execute((Void[])null);
			}			
		});
	}
}