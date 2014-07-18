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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Intent;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle; 
import android.os.Environment;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button; 
import android.widget.ListView;
import android.widget.TextView;
import android.view.*;

/**
 * This class inflates a ListView layout essentially
 * containing all font names read from a text file
 * located in the assets of this app. It also is 
 * responsible for font installations and previews
 * 
 * @author Priyesh
 *
 */
public class FontList extends Activity  {

	SharedPreferences prefs = null;
	private ListView lv;
	Dialog confirm;
	static Button reboot, positiveButton, negativeButton;
	static ProgressDialog downloadProgress, downloadPreviewProgress;
	static String fontDest, fontName, previewName, selectedFromList, longPressed;	
	static int dlLeft, sampleFontDL;
	static TextView alertTitle, alertMessage;
	ActionBar ab;
	boolean userScrolled;
	
	//Font url strings
	String urlRobotoBold, urlRobotoBoldItalic, urlRobotoItalic, 
	urlRobotoLight, urlRobotoLightItalic, urlRobotoRegular, urlRobotoThin, 
	urlRobotoThinItalic, urlRobotoCondensedBold, urlRobotoCondensedBoldItalic, 
	urlRobotoCondensedItalic, urlRobotoCondensedRegular, urlPreviewFont;		

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.font_list);
		prefs = getSharedPreferences("com.chromium.fontinstaller.fontlist", MODE_PRIVATE);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
		.addTestDevice("2797F5D9304B6B3A15771A0519A4F687")  // HTC Desire
		.addTestDevice("D674E5DF79F70B01D8866A5F99A2ACBA") // Samsung i9000
		.build();
		adView.loadAd(adRequest);

		fontDest = "/system/fonts"; //change path to /system/fonts when releasing

		lv = (ListView) findViewById(R.id.listView1);

		ArrayList<String> fontList = new ArrayList<String>();

		try { //Read text file containing font names and store into arraylist
			BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("fonts.txt")));

			String line = br.readLine();
			while (line != null) {
				fontList.add(line);
				line = br.readLine();
			}
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		//set font list arraylist to listview arrayadapter
		ArrayAdapter<String> adapter = new CustomAdapter(this, R.layout.list_item, R.id.fontTextView, fontList);

		lv.setAdapter(adapter); 

		/**
		 * FONT INSTALLATION
		 * First a confirmation dialog is show. When the user accepts
		 * the download URL's of each of the 12 font styles
		 * are constructed based on the font that has been selected.
		 * The app then checks to see if the font already exists on 
		 * the devices storage (meaning that the user has previously
		 * chosen this particular font). If it already exists, the fonts
		 * are directly copied from the storage into the /system/fonts
		 * directory. Once this has completed the reboot dialog is displayed
		 * prompting the user to restart their device. If the font does
		 * not exist, the URL's previously constructed are put through the
		 * DownloadManager class and sent as requests to the GitHub repository
		 * containing the fonts. Once all 12 fonts have been downloaded, they
		 * are pushed into the /system/fonts directory and the user is prompted
		 * to reboot their device.
		 */
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View clickView, int position, long id) {

				selectedFromList = (lv.getItemAtPosition(position).toString());

				fontName = removeSpaces(selectedFromList); //remove the spaces from the item so that it can later be passed into the URL string

				//urls for fonts
				urlRobotoBold = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/Roboto-Bold.ttf";
				urlRobotoBoldItalic = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/Roboto-BoldItalic.ttf";
				urlRobotoRegular = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/Roboto-Regular.ttf";
				urlRobotoItalic = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/Roboto-Italic.ttf";		
				urlRobotoLight = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/Roboto-Light.ttf";
				urlRobotoLightItalic = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/Roboto-LightItalic.ttf";
				urlRobotoThin = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/Roboto-Thin.ttf";
				urlRobotoThinItalic = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/Roboto-ThinItalic.ttf";
				urlRobotoCondensedBold = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/RobotoCondensed-Bold.ttf";
				urlRobotoCondensedBoldItalic = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/RobotoCondensed-BoldItalic.ttf";
				urlRobotoCondensedRegular = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/RobotoCondensed-Regular.ttf";
				urlRobotoCondensedItalic = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + fontName + "FontPack/RobotoCondensed-Italic.ttf";

				confirm = new Dialog(FontList.this);

				confirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
				confirm.setContentView(R.layout.two_button_alert);	
				TextView alertTitle = (TextView) confirm.findViewById(R.id.title);
				alertTitle.setText("Confirm installation");
				TextView alertMessage = (TextView) confirm.findViewById(R.id.message);
				alertMessage.setText("Are you sure you want to install " + selectedFromList + "?");
				positiveButton = (Button) confirm.findViewById(R.id.positive);
				negativeButton = (Button) confirm.findViewById(R.id.negative);

				positiveButton.setOnClickListener(new View.OnClickListener() { // confirm yes
					public void onClick(View v){

						confirm.cancel();

						File dfDir = new File(Environment.getExternalStorageDirectory() + "/Fontster/DownloadedFonts/"+fontName);

						if(dfDir.isDirectory()) {// user already downloaded the font, direct install start		
							//installation start
							AsyncTask<Void, Void, Void> directInstall = new AsyncTask<Void, Void, Void>()  { 
								//display progress dialog while fonts are copied in background
								ProgressDialog directInstallCopyProgress;

								@Override
								protected void onPreExecute() {
									super.onPreExecute();
									directInstallCopyProgress = new ProgressDialog (FontList.this);
									directInstallCopyProgress.setMessage("Copying to system...");
									directInstallCopyProgress.setCancelable(false);
									directInstallCopyProgress.setCanceledOnTouchOutside(false);
									directInstallCopyProgress.show();
								}

								@Override
								protected Void doInBackground(Void... params) {

									try {
										Process process = Runtime.getRuntime().exec("su");
										OutputStream stdin = process.getOutputStream();
										InputStream stderr = process.getErrorStream();
										InputStream stdout = process.getInputStream();
										stdin.write(("mount -o rw,remount /system\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Bold.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-BoldItalic.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Regular.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Italic.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Light.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-LightItalic.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Thin.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-ThinItalic.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/RobotoCondensed-Bold.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/RobotoCondensed-BoldItalic.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/RobotoCondensed-Regular.ttf " + fontDest +"\n").getBytes());
										stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/RobotoCondensed-Italic.ttf " + fontDest +"\n").getBytes());

										stdin.flush();
										stdin.close();
										process.waitFor();
										process.destroy();
										
									} catch (IOException | InterruptedException e) {
										e.printStackTrace();
									}
								/*	try {
										Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
										Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Bold.ttf " + fontDest});
										Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-BoldItalic.ttf " + fontDest});
										Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Regular.ttf " + fontDest});
										Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Italic.ttf " + fontDest}); 
										Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Light.ttf " + fontDest});
										Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-LightItalic.ttf " + fontDest});
										Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Thin.ttf " + fontDest});
										Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-ThinItalic.ttf " + fontDest});
										Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Bold.ttf " + fontDest});
										Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-BoldItalic.ttf " + fontDest});
										Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Regular.ttf " + fontDest});
										Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Italic.ttf " + fontDest});
									} 
									catch (IOException e) {
										e.printStackTrace();
									}*/
									return null;
								}

								@Override
								protected void onPostExecute(Void result) {
									super.onPostExecute(result);
									if (directInstallCopyProgress != null) {
										if (directInstallCopyProgress.isShowing()) {
											directInstallCopyProgress.dismiss();
										}
									}
									CustomAlerts.showRebootAlert("Installation successful", "You must reboot for the changes to take effect", "Reboot", FontList.this);
								}
							};
							directInstall.execute((Void[])null);
						}
						//direct install end

						else { //download fonts and then install						

							if (haveNetworkConnection()){ //connection available								

								// 12 requests for all font styles
								DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(urlRobotoBold));
								request1.allowScanningByMediaScanner();
								request1.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "Roboto-Bold.ttf");
								request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

								DownloadManager.Request request2 = new DownloadManager.Request(Uri.parse(urlRobotoBoldItalic));
								request2.allowScanningByMediaScanner();
								request2.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "Roboto-BoldItalic.ttf");
								request2.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);				

								DownloadManager.Request request3 = new DownloadManager.Request(Uri.parse(urlRobotoRegular));
								request3.allowScanningByMediaScanner();
								request3.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "Roboto-Regular.ttf");
								request3.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

								DownloadManager.Request request4 = new DownloadManager.Request(Uri.parse(urlRobotoItalic));
								request4.allowScanningByMediaScanner();
								request4.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "Roboto-Italic.ttf");
								request4.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

								DownloadManager.Request request5 = new DownloadManager.Request(Uri.parse(urlRobotoLight));
								request5.allowScanningByMediaScanner();
								request5.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "Roboto-Light.ttf");
								request5.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

								DownloadManager.Request request6 = new DownloadManager.Request(Uri.parse(urlRobotoLightItalic));
								request6.allowScanningByMediaScanner();
								request6.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "Roboto-LightItalic.ttf");
								request6.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

								DownloadManager.Request request7 = new DownloadManager.Request(Uri.parse(urlRobotoThin));
								request7.allowScanningByMediaScanner();
								request7.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "Roboto-Thin.ttf");
								request7.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

								DownloadManager.Request request8 = new DownloadManager.Request(Uri.parse(urlRobotoThinItalic));
								request8.allowScanningByMediaScanner();
								request8.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "Roboto-ThinItalic.ttf");
								request8.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

								DownloadManager.Request request9 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedBold));
								request9.allowScanningByMediaScanner();
								request9.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "RobotoCondensed-Bold.ttf");
								request9.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

								DownloadManager.Request request10 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedBoldItalic));
								request10.allowScanningByMediaScanner();
								request10.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "RobotoCondensed-BoldItalic.ttf");
								request10.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

								DownloadManager.Request request11 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedRegular));
								request11.allowScanningByMediaScanner();
								request11.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "RobotoCondensed-Regular.ttf");
								request11.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

								DownloadManager.Request request12 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedItalic));
								request12.allowScanningByMediaScanner();
								request12.setDestinationInExternalPublicDir("/Fontster/DownloadedFonts/"+fontName, "RobotoCondensed-Italic.ttf");
								request12.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

								DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

								//display a progress dialog just before the request for downloads are sent
								downloadProgress = new ProgressDialog(FontList.this);
								downloadProgress.setMessage("Downloading " + selectedFromList + ".");
								downloadProgress.show();

								//send all the download requests
								manager.enqueue(request1);
								manager.enqueue(request2);	
								manager.enqueue(request3);	
								manager.enqueue(request4);	
								manager.enqueue(request5);	
								manager.enqueue(request6);	
								manager.enqueue(request7);	
								manager.enqueue(request8);	
								manager.enqueue(request9);	
								manager.enqueue(request10);	
								manager.enqueue(request11);	
								manager.enqueue(request12);

								//number of initial requests
								dlLeft = 12;

								// listen for download completion, and close the progress dialog once it is detected
								BroadcastReceiver receiver = new BroadcastReceiver() {
									@Override
									public void onReceive(Context context, Intent intent) {
										String action = intent.getAction();
										if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
											dlLeft--; //every time a font is downloaded decrement this integer with an initial value of 12							
										}
										if (dlLeft == 0){ //once it reaches 0 (meaning all fonts were downloaded), display an alert
											downloadProgress.dismiss();

											//installation start
											AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()  { 
												//display progress dialog while fonts are copied in background
												ProgressDialog copyProgress;

												@Override
												protected void onPreExecute() {
													super.onPreExecute();
													copyProgress = new ProgressDialog (FontList.this);
													copyProgress.setMessage("Copying to system...");
													copyProgress.setCancelable(false);
													copyProgress.setCanceledOnTouchOutside(false);
													copyProgress.show();
												}

												@Override
												protected Void doInBackground(Void... params) {

													try {
														Process process = Runtime.getRuntime().exec("su");
														OutputStream stdin = process.getOutputStream();
														InputStream stderr = process.getErrorStream();
														InputStream stdout = process.getInputStream();
														stdin.write(("mount -o rw,remount /system\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Bold.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-BoldItalic.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Regular.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Italic.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Light.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-LightItalic.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-Thin.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/Roboto-ThinItalic.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/RobotoCondensed-Bold.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/RobotoCondensed-BoldItalic.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/RobotoCondensed-Regular.ttf " + fontDest +"\n").getBytes());
														stdin.write(("cp /sdcard/Fontster/DownloadedFonts/" + FontList.fontName + "/RobotoCondensed-Italic.ttf " + fontDest +"\n").getBytes());

														stdin.flush();
														stdin.close();
														process.waitFor();
														process.destroy();
														
													} catch (IOException | InterruptedException e) {
														e.printStackTrace();
													}
													
												/*	try {
														Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
														Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Bold.ttf " + fontDest});
														Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-BoldItalic.ttf " + fontDest});
														Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Regular.ttf " + fontDest});
														Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Italic.ttf " + fontDest}); 
														Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Light.ttf " + fontDest});
														Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-LightItalic.ttf " + fontDest});
														Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-Thin.ttf " + fontDest});
														Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/Roboto-ThinItalic.ttf " + fontDest});
														Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Bold.ttf " + fontDest});
														Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-BoldItalic.ttf " + fontDest});
														Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Regular.ttf " + fontDest});
														Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/Fontster/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Italic.ttf " + fontDest});
													} 
													catch (IOException e) {
														e.printStackTrace();
													} */
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
													CustomAlerts.showRebootAlert("Installation successful", "You must reboot for the changes to take effect", "Reboot", FontList.this);

												}
											};
											task.execute((Void[])null);
											//installation end
										}
									}
								};
								registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
							}
							else { //no connection
								CustomAlerts.showBasicAlert("No connection", "Your phone must be connected to the internet.", FontList.this);
							}
						}

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

		/**
		 * FONT PREVIEWING
		 * The font name that has been selected is passed through
		 * the removeSpaces method to create a new string that has 
		 * no spaces in it. Then a single URL is constructed only
		 * for the regular style font that has been chosen. The 
		 * download request is sent, and once it has completed the 
		 * app calls the preview alert from the CustomAlerts class.
		 * The download is skipped if the font is already available
		 * on the phones storage.
		 */
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //long press on listview item
			public boolean onItemLongClick(AdapterView<?> parent, View clickView, int position, long id) {

				longPressed = (lv.getItemAtPosition(position).toString());

				previewName = removeSpaces(longPressed);

				File sampleFont = new File(Environment.getExternalStorageDirectory() + "/Fontster/SampleFonts/" + previewName + "/sample.ttf");

				if(sampleFont.exists())  {
					//Create new typeface from downloaded regular preview font
					Typeface sampleFontReUsed = Typeface.createFromFile("/sdcard/Fontster/SampleFonts/" + previewName + "/sample.ttf");

					String testSentence = "The quick brown fox jumps over the lazy dog.\n";

					CustomAlerts.showPreviewAlert(longPressed, testSentence, sampleFontReUsed, previewName, FontList.this);
				}
				else {

					if (haveNetworkConnection()){ //connection available
						urlPreviewFont = "https://github.com/ItsPriyesh/FontsterFontsRepo/raw/master/" + previewName + "FontPack/Roboto-Regular.ttf";

						String path = "/Fontster/SampleFonts/" + previewName + "/";
						DownloadManager.Request downloadSample = new DownloadManager.Request(Uri.parse(urlPreviewFont));
						downloadSample.allowScanningByMediaScanner();
						downloadSample.setDestinationInExternalPublicDir(path, "sample.ttf");
						downloadSample.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

						//Send request
						DownloadManager sampleFontManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

						//display a progress dialog just before the request is sent
						downloadPreviewProgress = new ProgressDialog(FontList.this);
						downloadPreviewProgress.setMessage("Fetching preview font...");
						downloadPreviewProgress.show();

						sampleFontManager.enqueue(downloadSample);
						sampleFontDL = 1;

						// listen for download completion, and close the progress dialog once it is detected
						BroadcastReceiver receiver1 = new BroadcastReceiver() {
							@Override
							public void onReceive(Context context, Intent intent) {
								String action1 = intent.getAction();
								if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action1)) {
									sampleFontDL--; //reduce value to 0, indicating download completion					
								}
								if (sampleFontDL == 0){
									downloadPreviewProgress.dismiss();

									//Create new typeface from downloaded regular preview font
									Typeface sampleFont = Typeface.createFromFile("/sdcard/Fontster/SampleFonts/" + previewName + "/sample.ttf");

									String testSentence = "The quick brown fox jumps over the lazy dog.\n";

									CustomAlerts.showPreviewAlert(longPressed, testSentence, sampleFont, previewName, FontList.this);
								}
							}
						};
						registerReceiver(receiver1, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
					}
					else { //no connection
						CustomAlerts.showBasicAlert("No connection", "Your phone must be connected to the internet.", FontList.this);
					}

				}
				return true;
			}
		});

		/**
		 * Manages the theming of the actionbar in
		 * the FontList activity. When the user scrolls
		 * through the list, the actionbar will become 
		 * translucent grey. Once scrolling has stopped
		 * the actionbar will return to its initial
		 * opaque grey colour.
		 */
		lv.setOnScrollListener(new OnScrollListener(){
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//scrolling started
				if (userScrolled){
				ab = getActionBar();
				ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
				}
			}
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					//scrolling stopped
					ab = getActionBar();
					ColorDrawable translucentBlack = new ColorDrawable(getResources().getColor(R.color.translucent_black));
					ColorDrawable opaqueBlack = new ColorDrawable(getResources().getColor(R.color.ab_dark_grey));
					ColorDrawable[] color = {translucentBlack, opaqueBlack}; 
					TransitionDrawable trans = new TransitionDrawable(color);
					ab.setBackgroundDrawable(trans);
					trans.startTransition(500);
				}
				if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
		            userScrolled = true;
		        }  
			}
		});

	}	 

	/**
	 * Called only on the first run of the app.
	 * This displays general instructions on how to
	 * install and preview fonts when the FontList 
	 * activity is shown.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (prefs.getBoolean("firstrun", true)) { 
			CustomAlerts.showBasicAlertWithImage ("Instructions", "To install a font simply tap " +
					"on the one that you want.\n\nIf you would like to preview a font prior to installing, " +
					"press and hold it.", FontList.this);
			prefs.edit().putBoolean("firstrun", false).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.fontlist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.menu_help:
			CustomAlerts.showBasicAlertWithImage ("Instructions", "To install a font simply tap on the one that you want.\n\nIf you would like to preview a font prior to installing, press and hold it.", FontList.this);
			return true;
		default:
			return super.onOptionsItemSelected(menuItem);
		}
	}

	/**
	 * Recursively deletes the directory
	 * specified.
	 * @param path to the directory that must be deleted
	 * @return a boolean that specifies to delete or not to delete
	 */
	public static boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return(path.delete());
	}

	/**
	 * Checks if users device has an internet connection,
	 * to either WiFi or Mobile Data.
	 * 
	 * @return true if connection is available, and false if it is not
	 */
	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	/**
	 * This method removes spaces from strings and is used to construct
	 * download URL's from the original font names which usually have spaces
	 * in them. The URL must not contain spaces.
	 * 
	 * @param line is the the input string that is to be processed
	 * @return The initial input string is returned as a new string containing no spaces
	 */
	public static String removeSpaces (String line){
		for (int x = 0 ; x < line.length () ; x++){
			if (line.charAt (x) == ' '){
				String newLine = line.substring (0, x) + line.substring (x+1);
				return removeSpaces (newLine);
			}
		}
		return line;
	}
}
