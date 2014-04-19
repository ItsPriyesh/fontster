package com.chromium.fontinstaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import android.view.*;

public class FontList extends Activity  {

	SharedPreferences prefs = null;
	private ListView lv;
	static Button reboot;
	static ProgressDialog downloadProgress, copyProgress, downloadPreviewProgress;
	static String fontDest, fontName, previewName, selectedFromList, longPressed;	
	static int dlLeft, sampleFontDL;
	static TextView alertTitle, alertMessage;

	//Font url strings
	String urlRobotoBold, urlRobotoBoldItalic, urlRobotoItalic, 
	urlRobotoLight, urlRobotoLightItalic, urlRobotoRegular, urlRobotoThin, 
	urlRobotoThinItalic, urlRobotoCondensedBold, urlRobotoCondensedBoldItalic, 
	urlRobotoCondensedItalic, urlRobotoCondensedRegular, urlPreviewFont;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.font_list);
		prefs = getSharedPreferences("com.chromium.fontinstaller", MODE_PRIVATE);

		fontDest = "/system"; //change path to /system/fonts when releasing

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
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fontList);

		lv.setAdapter(arrayAdapter); 

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
				downloadProgress.setTitle("Downloading");
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

							AlertDialog.Builder builder = new AlertDialog.Builder(FontList.this);
							builder.setMessage(fontName + " successfully downloaded. Press OK to install.")
							.setTitle ("Fonts downloaded")
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()  { 
										//display progress dialog while fonts are copied in background
										ProgressDialog progressDialog;

										@Override
										protected void onPreExecute() {
											super.onPreExecute();
											progressDialog = new ProgressDialog (FontList.this);
											progressDialog.setMessage("Copying to system...");
											progressDialog.show();
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
											if (progressDialog != null) {
												if (progressDialog.isShowing()) {
													progressDialog.dismiss();
												}
											}
										}
									};
									task.execute((Void[])null);

									//one copying has finished display alertdialog with reboot prompt
									AlertDialog.Builder builder2 = new AlertDialog.Builder(FontList.this);
									builder2.setMessage("You must reboot for the changes to take effect.")
									.setTitle ("Reboot")
									.setCancelable(false)
									.setPositiveButton("Reboot", new DialogInterface.OnClickListener() { //reboot the phone
										public void onClick(DialogInterface dialog, int id) {
											try{ 
												Process reboot = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot"});
											}
											catch(IOException e){
												Toast.makeText(getApplicationContext(), "Reboot failed.", Toast.LENGTH_LONG).show();
											}

										}
									})
									.setNegativeButton("Later", new DialogInterface.OnClickListener() { //do nothing (let user reboot later at will)
										public void onClick(DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
									AlertDialog alert2 = builder2.create();
									alert2.show();
								}
							})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { //close dialog
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
							AlertDialog alert = builder.create();
							alert.show();	
						}
					}
				};
				registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
			}
		});				

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //long press on listview item
			public boolean onItemLongClick(AdapterView<?> parent, View clickView, int position, long id) {

				longPressed = (lv.getItemAtPosition(position).toString());

				previewName = removeSpaces(longPressed);

				urlPreviewFont = "https://github.com/Chromium1/Fonts/raw/master/" + previewName + "FontPack/Roboto-Regular.ttf";

				//Setup request to download regular font style for user preview
				DownloadManager.Request downloadSample = new DownloadManager.Request(Uri.parse(urlPreviewFont));
				downloadSample.allowScanningByMediaScanner();
				downloadSample.setDestinationInExternalPublicDir("/SampleFonts", "sample.ttf");
				downloadSample.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

				//Delete old samples
				File oldSample = new File("/sdcard/SampleFonts/sample.ttf");
				boolean deletedOldSample = oldSample.delete();

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
							Typeface sampleFont = Typeface.createFromFile("/sdcard/SampleFonts/sample.ttf");

							String testSentence = "The quick brown fox jumps over the lazy dog.";
							
							showCustomPreviewAlert(longPressed, testSentence, sampleFont);
						}
					}
				};
				registerReceiver(receiver1, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

				return true;
			}
		});
	}	 

	@Override
	protected void onResume() {
		super.onResume();

		if (prefs.getBoolean("firstrun", true)) { //stuff to do on first app opening

			showCustomAlert("Instructions","To install a font simply tap on the one that you want.\n\nIf you would like to preview a font prior to installing, long press it." );
			
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
			showCustomAlert("Instructions","To install a font simply tap on the one that you want.\n\nIf you would like to preview a font prior to installing, long press it." );
			return true;
		}
		return false;
	}
	
	public void showCustomAlert (String title, String message) { //method to show custom styled dialog. params are the title and message of the alert
		Dialog help = new Dialog(this);

		help.requestWindowFeature(Window.FEATURE_NO_TITLE);
		help.setContentView(R.layout.alert);	
		TextView alertTitle = (TextView) help.findViewById(R.id.title);
		alertTitle.setText(title);
		TextView alertMessage = (TextView) help.findViewById(R.id.message);
		alertMessage.setText(message);
		help.show();
	}

	public void showCustomPreviewAlert (String title, String message, Typeface font) { //method for preview dialog. has extra param for typeface
		Dialog preview = new Dialog(this);

		preview.requestWindowFeature(Window.FEATURE_NO_TITLE);
		preview.setContentView(R.layout.preview_alert);	
		TextView alertTitle = (TextView) preview.findViewById(R.id.title);
		alertTitle.setText(title);
		TextView alertMessage = (TextView) preview.findViewById(R.id.message);
		alertMessage.setTypeface(font);
		alertMessage.setText(message);
		
		preview.show();
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