package com.chromium.fontinstaller;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StorageInstall extends Activity {

	Button selectRegular;
	String fontFile = null;
	String regPath, regFile;
	TextView regPathTV;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storage_install);

		regPathTV = (TextView)findViewById(R.id.regPath);
		
		selectRegular = (Button)findViewById(R.id.selectRegular);
		selectRegular.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				showFileListDialog(Environment.getExternalStorageDirectory().toString(), StorageInstall.this);
				
			}
		});
		
		install = (Button)findViewById(R.id.install);
		install.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				regPath = regPathTV.getText().toString();
				
				int index = regPath.lastIndexOf("/");
				regFile = regPath.substring(index + 1);

				//installation start
				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()  { 
					//display progress dialog while fonts are copied in background
					ProgressDialog copyProgress;

					@Override
					protected void onPreExecute() {
						super.onPreExecute();						
						copyProgress = new ProgressDialog (StorageInstall.this);
						copyProgress.setMessage("Installing specified fonts...");
						copyProgress.setCancelable(false);
						copyProgress.setCanceledOnTouchOutside(false);
						copyProgress.show();
					}

					@Override
					protected Void doInBackground(Void... params) {					
						
						try {
							Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
							Process makeTempDir = Runtime.getRuntime().exec(new String[] { "su", "-c", "mkdir /sdcard/TempFonts"}); //make temporary folder for fonts
							
							//copy to temp dir
							Process copyReg1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp " + regPath + " /sdcard/TempFonts"});
							
							//rename
							Process renameReg = Runtime.getRuntime().exec(new String[] { "su", "-c", "mv /sdcard/TempFonts/" + regFile + " /sdcard/TempFonts/Roboto-Regular.ttf"});
					
							//copy from temp dir to system
							Process installReg = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/TempFonts/Roboto-Regular.ttf /system/fonts"});
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);			
						if (copyProgress != null) {
							if (copyProgress.isShowing()) {
								copyProgress.dismiss();
							}
						}
						CustomAlerts.showRebootAlert("Installation successful", "You must reboot for the changes to take effect", "Reboot", StorageInstall.this);

					}
				};
				task.execute((Void[])null);
				//installation end
				
			}
		});		

	}


	/*
	 * Credits for this mini file explorer go to
	 * the user schwiz on StackOverflow
	 */
	private File[] fileList;
	private String[] filenameList;
	private File[] loadFileList(String directory) {
		File path = new File(directory);

		if(path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					return true;
				}
			};

			//if null return an empty array instead
			File[] list = path.listFiles(filter); 
			return list == null? new File[0] : list;
		} else {
			return new File[0];
		}
	}

	public void showFileListDialog(final String directory, final Context context){
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(context);

		File[] tempFileList = loadFileList(directory);

		//if directory is root, no need to up one directory
		if(directory.equals("/")){
			fileList = new File[tempFileList.length];
			filenameList = new String[tempFileList.length];

			//iterate over tempFileList
			for(int i = 0; i < tempFileList.length; i++){
				fileList[i] = tempFileList[i];
				filenameList[i] = tempFileList[i].getName();
			}
		} else {
			fileList = new File[tempFileList.length+1];
			filenameList = new String[tempFileList.length+1];

			//add an "up" option as first item
			fileList[0] = new File(upOneDirectory(directory));
			filenameList[0] = "...";

			//iterate over tempFileList
			for(int i = 0; i < tempFileList.length; i++){
				fileList[i+1] = tempFileList[i];
				filenameList[i+1] = tempFileList[i].getName();
			}
		}

		builder.setTitle("Choose your font file:");

		builder.setItems(filenameList, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				File chosenFile = fileList[which];

				if(chosenFile.isDirectory()) {
					showFileListDialog(chosenFile.getAbsolutePath(), context);
				}
				else {
					dialog.cancel();
					fontFile = chosenFile.getAbsolutePath();
					regPathTV.setText(fontFile);
				}
			}
		});
		
		dialog = builder.create();
		dialog.show();
	}

	public String upOneDirectory(String directory){
		String[] dirs = directory.split("/");
		StringBuilder stringBuilder = new StringBuilder("");

		for(int i = 0; i < dirs.length-1; i++)
			stringBuilder.append(dirs[i]).append("/");

		return stringBuilder.toString();
	}

}
