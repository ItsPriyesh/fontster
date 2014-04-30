package com.chromium.fontinstaller;

import java.io.BufferedReader; //imports BufferedReader
import java.io.File; //imports java.io.File
import java.io.IOException; //imports java.io.IOException
import java.io.InputStreamReader; //imports java.io.InputStreamreader
import java.util.ArrayList; //imports java.util.ArrayList
import android.content.Intent; //imports android.content.Intent
import android.app.Activity; //imports android.app.Acticity
import android.app.Dialog; //imports android.app.Dialog
import android.app.DownloadManager; //imports android.app.DownloadManager
import android.app.ProgressDialog; //imports android.app.ProgressDialog
import android.content.BroadcastReceiver; //imports content.BroadcastReveiver
import android.content.Context; //imports android.content.Context
import android.content.IntentFilter; //imports android.content.IntentFilter
import android.content.SharedPreferences; //imports android.content.SharedPreferences
import android.graphics.Typeface; //imports android.graphics.Typeface
import android.net.Uri; //imports android.net.Uri
import android.os.AsyncTask; //imports android.os.AsyncTask
import android.os.Bundle; //imports android.os.Bundle
import android.os.Environment; //imports android.os.Environment
import android.widget.AdapterView; //imports android.widget.AdapterView
import android.widget.ArrayAdapter; //imports android.widget.ArrayAdapter
import android.widget.Button; //imports android.widget.Button
import android.widget.ListView; //imports android.widget.ListView
import android.widget.TextView; //imports android.widget.TextView
import android.widget.Toast; //imports android.widget.Toast
import android.view.*; //imports android.view.*

public class FontList extends Activity  {

	public static SharedPreferences prefs = null;
	private ListView lv;
	Dialog confirm;
	static Button reboot, positiveButton, negativeButton;
	static ProgressDialog downloadProgress, downloadPreviewProgress;
	static String fontDest, fontName, previewName, selectedFromList, longPressed;	
	static int dlLeft, sampleFontDL;
	static TextView alertTitle, alertMessage;

	static String currentlyInstalledFont = "Stock";
	
	//Font url strings
	String urlRobotoBold, urlRobotoBoldItalic, urlRobotoItalic, 
	urlRobotoLight, urlRobotoLightItalic, urlRobotoRegular, urlRobotoThin, 
	urlRobotoThinItalic, urlRobotoCondensedBold, urlRobotoCondensedBoldItalic, 
	urlRobotoCondensedItalic, urlRobotoCondensedRegular, urlPreviewFont;		

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.font_list);
		prefs = getSharedPreferences("com.chromium.fontinstaller.fontlist", MODE_PRIVATE);
		
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putString("installedFont", currentlyInstalledFont);
		editor.commit();
		
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

		// Font Installing
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View clickView, int position, long id) {
				
				selectedFromList = (lv.getItemAtPosition(position).toString());

				fontName = removeSpaces(selectedFromList); //remove the spaces from the item so that it can later be passed into the URL string

				//urls for fonts
				urlRobotoBold = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Bold.ttf";
				urlRobotoBoldItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-BoldItalic.ttf";
				urlRobotoRegular = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Regular.ttf";
				urlRobotoItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Italic.ttf";		
				urlRobotoLight = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Light.ttf";
				urlRobotoLightItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-LightItalic.ttf";
				urlRobotoThin = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Thin.ttf";
				urlRobotoThinItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-ThinItalic.ttf";
				urlRobotoCondensedBold = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Bold.ttf";
				urlRobotoCondensedBoldItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-BoldItalic.ttf";
				urlRobotoCondensedRegular = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Regular.ttf";
				urlRobotoCondensedItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Italic.ttf";

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

						File dfDir = new File(Environment.getExternalStorageDirectory() + "/DownloadedFonts/"+fontName);

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
										Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
										Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Bold.ttf " + fontDest});
										Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-BoldItalic.ttf " + fontDest});
										Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Regular.ttf " + fontDest});
										Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Italic.ttf " + fontDest}); 
										Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Light.ttf " + fontDest});
										Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-LightItalic.ttf " + fontDest});
										Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Thin.ttf " + fontDest});
										Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-ThinItalic.ttf " + fontDest});
										Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Bold.ttf " + fontDest});
										Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-BoldItalic.ttf " + fontDest});
										Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Regular.ttf " + fontDest});
										Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Italic.ttf " + fontDest});
									} 
									catch (IOException e) {
										e.printStackTrace();
									}
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

							// 12 requests for all font styles
							DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(urlRobotoBold));
							request1.allowScanningByMediaScanner();
							request1.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "Roboto-Bold.ttf");
							request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

							DownloadManager.Request request2 = new DownloadManager.Request(Uri.parse(urlRobotoBoldItalic));
							request2.allowScanningByMediaScanner();
							request2.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "Roboto-BoldItalic.ttf");
							request2.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);				

							DownloadManager.Request request3 = new DownloadManager.Request(Uri.parse(urlRobotoRegular));
							request3.allowScanningByMediaScanner();
							request3.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "Roboto-Regular.ttf");
							request3.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

							DownloadManager.Request request4 = new DownloadManager.Request(Uri.parse(urlRobotoItalic));
							request4.allowScanningByMediaScanner();
							request4.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "Roboto-Italic.ttf");
							request4.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

							DownloadManager.Request request5 = new DownloadManager.Request(Uri.parse(urlRobotoLight));
							request5.allowScanningByMediaScanner();
							request5.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "Roboto-Light.ttf");
							request5.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

							DownloadManager.Request request6 = new DownloadManager.Request(Uri.parse(urlRobotoLightItalic));
							request6.allowScanningByMediaScanner();
							request6.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "Roboto-LightItalic.ttf");
							request6.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

							DownloadManager.Request request7 = new DownloadManager.Request(Uri.parse(urlRobotoThin));
							request7.allowScanningByMediaScanner();
							request7.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "Roboto-Thin.ttf");
							request7.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

							DownloadManager.Request request8 = new DownloadManager.Request(Uri.parse(urlRobotoThinItalic));
							request8.allowScanningByMediaScanner();
							request8.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "Roboto-ThinItalic.ttf");
							request8.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

							DownloadManager.Request request9 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedBold));
							request9.allowScanningByMediaScanner();
							request9.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "RobotoCondensed-Bold.ttf");
							request9.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

							DownloadManager.Request request10 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedBoldItalic));
							request10.allowScanningByMediaScanner();
							request10.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "RobotoCondensed-BoldItalic.ttf");
							request10.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

							DownloadManager.Request request11 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedRegular));
							request11.allowScanningByMediaScanner();
							request11.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "RobotoCondensed-Regular.ttf");
							request11.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

							DownloadManager.Request request12 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedItalic));
							request12.allowScanningByMediaScanner();
							request12.setDestinationInExternalPublicDir("/DownloadedFonts/"+fontName, "RobotoCondensed-Italic.ttf");
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
													Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
													Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Bold.ttf " + fontDest});
													Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-BoldItalic.ttf " + fontDest});
													Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Regular.ttf " + fontDest});
													Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Italic.ttf " + fontDest}); 
													Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Light.ttf " + fontDest});
													Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-LightItalic.ttf " + fontDest});
													Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-Thin.ttf " + fontDest});
													Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/Roboto-ThinItalic.ttf " + fontDest});
													Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Bold.ttf " + fontDest});
													Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-BoldItalic.ttf " + fontDest});
													Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Regular.ttf " + fontDest});
													Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+FontList.fontName + "/RobotoCondensed-Italic.ttf " + fontDest});
												} 
												catch (IOException e) {
													e.printStackTrace();
												}
												return null;
											}

											@Override
											protected void onPostExecute(Void result) {
												super.onPostExecute(result);
												currentlyInstalledFont = selectedFromList;
												prefs = getSharedPreferences("com.chromium.fontinstaller.fontlist", MODE_PRIVATE);		
												editor.putString("installedFont", currentlyInstalledFont);
												editor.commit();
												
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

		// Font Previewing
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //long press on listview item
			public boolean onItemLongClick(AdapterView<?> parent, View clickView, int position, long id) {

				longPressed = (lv.getItemAtPosition(position).toString());

				previewName = removeSpaces(longPressed);

				File sampleFont = new File(Environment.getExternalStorageDirectory() + "/SampleFonts/" + previewName + "/sample.ttf");
				
				if(sampleFont.exists())  {
					//Create new typeface from downloaded regular preview font
					Typeface sampleFontReUsed = Typeface.createFromFile("/sdcard/SampleFonts/" + previewName + "/sample.ttf");

					String testSentence = "The quick brown fox jumps over the lazy dog.\n";

					CustomAlerts.showPreviewAlert(longPressed, testSentence, sampleFontReUsed, previewName, FontList.this);
				}
				else {

					urlPreviewFont = "https://github.com/Chromium1/Fonts/raw/master/" + previewName + "FontPack/Roboto-Regular.ttf";

					String path = "/SampleFonts/" + previewName + "/";
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
								Typeface sampleFont = Typeface.createFromFile("/sdcard/SampleFonts/" + previewName + "/sample.ttf");

								String testSentence = "The quick brown fox jumps over the lazy dog.\n";

								CustomAlerts.showPreviewAlert(longPressed, testSentence, sampleFont, previewName, FontList.this);
							}
						}
					};
					registerReceiver(receiver1, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
				}
				return true;
			}
		});
	}	 

	@Override
	protected void onResume() {
		super.onResume();

		if (prefs.getBoolean("firstrun", true)) { //stuff to do on first app opening

		//	CustomAlerts.showBasicAlert("Instructions", "To install a font simply tap on the one that you want.\n\nIf you would like to preview a font prior to installing, long press it.", FontList.this);
			CustomAlerts.showBasicAlertWithImage ("Instructions", "To install a font simply tap on the one that you want.\n\nIf you would like to preview a font prior to installing, press and hold it.", FontList.this);

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
		if (menuItem.getItemId() == R.id.menu_help) {
			//CustomAlerts.showBasicAlert("Instructions", "To install a font simply tap on the one that you want.\n\nIf you would like to preview a font prior to installing, long press it.", FontList.this);
			CustomAlerts.showBasicAlertWithImage ("Instructions", "To install a font simply tap on the one that you want.\n\nIf you would like to preview a font prior to installing, press and hold it.", FontList.this);

			return true;
		}
		return false;
	}

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
		return( path.delete() );
	}

	public static String removeSpaces (String line)
	{//method to remove spaces
		for (int x = 0 ; x < line.length () ; x++)
		{
			if (line.charAt (x) == ' ')
			{
				String newLine = line.substring (0, x) + line.substring (x+1);
				return removeSpaces (newLine);
			}
		}
		return line;
	}

}
