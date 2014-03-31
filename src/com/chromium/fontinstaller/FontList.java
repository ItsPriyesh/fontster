package com.chromium.fontinstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.Intent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.*;

public class FontList extends Activity {

	private ListView lv;

	static ProgressDialog downloadProgress;

	String fontName, selectedFromList;

	//Font url strings
	String urlRobotoBold, urlRobotoBoldItalic, urlRobotoItalic, 
	urlRobotoLight, urlRobotoLightItalic, urlRobotoRegular, urlRobotoThin, 
	urlRobotoThinItalic, urlRobotoCondensedBold, urlRobotoCondensedBoldItalic, 
	urlRobotoCondensedItalic, urlRobotoCondensedRegular;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.font_list);

		lv = (ListView) findViewById(R.id.listView1);

		ArrayList<String> fontList = new ArrayList<String>();

		try {
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

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fontList);

		lv.setAdapter(arrayAdapter); 

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View clickView, int position, long id) {
				selectedFromList = (lv.getItemAtPosition(position).toString());

				fontName = removeSpaces(selectedFromList); //remove the spaces from the item so that it can later be passed into the URL string

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
				
				File file = new File("/sdcard/DownloadedFonts/"+fontName + "/Roboto-BoldItalic.ttf");

				if (file.exists()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(FontList.this);
					builder.setMessage("You have already previously downloaded this font. Press OK to install.")
					.setTitle ("Fonts already downloaded")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							try {
								Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
								Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Bold.ttf /system"});
								Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-BoldItalic.ttf /system"});
								Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Regular.ttf /system"});
								Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Italic.ttf /system"});
								Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Light.ttf /system"});
								Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-LightItalic.ttf /system"});
								Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Thin.ttf /system"});
								Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-ThinItalic.ttf /system"});
								Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/RobotoCondensed-Bold.ttf /system"});
								Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/RobotoCondensed-BoldItalic.ttf /system"});
								Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/RobotoCondensed-Regular.ttf /system"});
								Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/RobotoCondensed-Italic.ttf /system"});

							}
							catch (IOException e) {
								Toast.makeText(getApplicationContext(), "not found",
										Toast.LENGTH_LONG).show();			
							}
						}
					});
					AlertDialog alert = builder.create();
					alert.show();	
				}
				else {
					//display a progress dialog just before the request for downloads are sent
					downloadProgress = ProgressDialog.show(FontList.this, "Downloading", "Downloading " + selectedFromList + ".", true);				
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
				}
				// listen for download completion, and close the progress dialog once it is detected
				BroadcastReceiver receiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
							downloadProgress.dismiss();
							
							AlertDialog.Builder builder2 = new AlertDialog.Builder(FontList.this);
							builder2.setMessage(fontName + " successfully downloaded. Press OK to install.")
							.setTitle ("Fonts downloaded")
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									try {
										Process mountSystem = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
										Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Bold.ttf /system"});
										Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-BoldItalic.ttf /system"});
										Process process3 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Regular.ttf /system"});
										Process process4 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Italic.ttf /system"});
										Process process5 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Light.ttf /system"});
										Process process6 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-LightItalic.ttf /system"});
										Process process7 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-Thin.ttf /system"});
										Process process8 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/Roboto-ThinItalic.ttf /system"});
										Process process9 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/RobotoCondensed-Bold.ttf /system"});
										Process process10 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/RobotoCondensed-BoldItalic.ttf /system"});
										Process process11 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/RobotoCondensed-Regular.ttf /system"});
										Process process12 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/DownloadedFonts/"+fontName + "/RobotoCondensed-Italic.ttf /system"});

									}
									catch (IOException e) {
										Toast.makeText(getApplicationContext(), "not found",
												Toast.LENGTH_LONG).show();			
									}
								}
							});
							AlertDialog alert2 = builder2.create();
							alert2.show();	
							
						}
					}
				};

				registerReceiver(receiver, new IntentFilter(
						DownloadManager.ACTION_DOWNLOAD_COMPLETE));
			}
		});						
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