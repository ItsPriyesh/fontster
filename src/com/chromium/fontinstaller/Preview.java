package com.chromium.fontinstaller;

import java.io.File;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Window;
import android.widget.TextView;

public class Preview {

	static int sampleFontDL;
	static ProgressDialog downloadPreviewProgress;

	public static void fullPreviewAlert (String fontName, Context context){
		//urls for fonts
		String urlRobotoBold = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Bold.ttf";
		String urlRobotoBoldItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-BoldItalic.ttf";
		String urlRobotoRegular = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Regular.ttf";
		String urlRobotoItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Italic.ttf";		
		String urlRobotoLight = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Light.ttf";
		String urlRobotoLightItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-LightItalic.ttf";
		String urlRobotoThin = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-Thin.ttf";
		String urlRobotoThinItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/Roboto-ThinItalic.ttf";
		String urlRobotoCondensedBold = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Bold.ttf";
		String urlRobotoCondensedBoldItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-BoldItalic.ttf";
		String urlRobotoCondensedRegular = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Regular.ttf";
		String urlRobotoCondensedItalic = "https://github.com/Chromium1/Fonts/raw/master/" + fontName + "FontPack/RobotoCondensed-Italic.ttf";	

		// 12 requests for all font styles
		DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(urlRobotoBold));
		request1.allowScanningByMediaScanner();
		request1.setDestinationInExternalPublicDir("/SampleFonts", "Roboto-Bold.ttf");
		request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

		DownloadManager.Request request2 = new DownloadManager.Request(Uri.parse(urlRobotoBoldItalic));
		request2.allowScanningByMediaScanner();
		request2.setDestinationInExternalPublicDir("/SampleFonts", "Roboto-BoldItalic.ttf");
		request2.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);				

		DownloadManager.Request request3 = new DownloadManager.Request(Uri.parse(urlRobotoRegular));
		request3.allowScanningByMediaScanner();
		request3.setDestinationInExternalPublicDir("/SampleFonts", "Roboto-Regular.ttf");
		request3.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

		DownloadManager.Request request4 = new DownloadManager.Request(Uri.parse(urlRobotoItalic));
		request4.allowScanningByMediaScanner();
		request4.setDestinationInExternalPublicDir("/SampleFonts", "Roboto-Italic.ttf");
		request4.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

		DownloadManager.Request request5 = new DownloadManager.Request(Uri.parse(urlRobotoLight));
		request5.allowScanningByMediaScanner();
		request5.setDestinationInExternalPublicDir("/SampleFonts", "Roboto-Light.ttf");
		request5.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

		DownloadManager.Request request6 = new DownloadManager.Request(Uri.parse(urlRobotoLightItalic));
		request6.allowScanningByMediaScanner();
		request6.setDestinationInExternalPublicDir("/SampleFonts", "Roboto-LightItalic.ttf");
		request6.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

		DownloadManager.Request request7 = new DownloadManager.Request(Uri.parse(urlRobotoThin));
		request7.allowScanningByMediaScanner();
		request7.setDestinationInExternalPublicDir("/SampleFonts", "Roboto-Thin.ttf");
		request7.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

		DownloadManager.Request request8 = new DownloadManager.Request(Uri.parse(urlRobotoThinItalic));
		request8.allowScanningByMediaScanner();
		request8.setDestinationInExternalPublicDir("/SampleFonts", "Roboto-ThinItalic.ttf");
		request8.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);	

		DownloadManager.Request request9 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedBold));
		request9.allowScanningByMediaScanner();
		request9.setDestinationInExternalPublicDir("/SampleFonts", "RobotoCondensed-Bold.ttf");
		request9.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

		DownloadManager.Request request10 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedBoldItalic));
		request10.allowScanningByMediaScanner();
		request10.setDestinationInExternalPublicDir("/SampleFonts", "RobotoCondensed-BoldItalic.ttf");
		request10.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

		DownloadManager.Request request11 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedRegular));
		request11.allowScanningByMediaScanner();
		request11.setDestinationInExternalPublicDir("/SampleFonts", "RobotoCondensed-Regular.ttf");
		request11.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

		DownloadManager.Request request12 = new DownloadManager.Request(Uri.parse(urlRobotoCondensedItalic));
		request12.allowScanningByMediaScanner();
		request12.setDestinationInExternalPublicDir("/SampleFonts", "RobotoCondensed-Italic.ttf");
		request12.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

		//Delete old samples
		File oldSample1 = new File("/sdcard/SampleFonts/Roboto-Bold.ttf");
		File oldSample2 = new File("/sdcard/SampleFonts/Roboto-BoldItalic.ttf");
		File oldSample3 = new File("/sdcard/SampleFonts/Roboto-Regular.ttf");
		File oldSample4 = new File("/sdcard/SampleFonts/Roboto-Italic.ttf");
		File oldSample5 = new File("/sdcard/SampleFonts/Roboto-Light.ttf");
		File oldSample6 = new File("/sdcard/SampleFonts/Roboto-LightItalic.ttf");
		File oldSample7 = new File("/sdcard/SampleFonts/Roboto-Thin.ttf");
		File oldSample8 = new File("/sdcard/SampleFonts/Roboto-ThinItalic.ttf");
		File oldSample9 = new File("/sdcard/SampleFonts/RobotoCondensed-Bold.ttf");
		File oldSample10 = new File("/sdcard/SampleFonts/RobotoCondensed-BoldItalic.ttf");
		File oldSample11 = new File("/sdcard/SampleFonts/RobotoCondensed-Regular.ttf");
		File oldSample12 = new File("/sdcard/SampleFonts/RobotoCondensed-Italic.ttf");

		boolean deletedOldSample1 = oldSample1.delete();
		boolean deletedOldSample2 = oldSample2.delete();
		boolean deletedOldSample3 = oldSample3.delete();
		boolean deletedOldSample4 = oldSample4.delete();
		boolean deletedOldSample5 = oldSample5.delete();
		boolean deletedOldSample6 = oldSample6.delete();
		boolean deletedOldSample7 = oldSample7.delete();
		boolean deletedOldSample8 = oldSample8.delete();
		boolean deletedOldSample9 = oldSample9.delete();
		boolean deletedOldSample10 = oldSample10.delete();
		boolean deletedOldSample11 = oldSample11.delete();
		boolean deletedOldSample12 = oldSample12.delete();

		//Send request
		DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

		//display a progress dialog just before the request is sent
		downloadPreviewProgress = new ProgressDialog(context);
		downloadPreviewProgress.setMessage("Fetching all variants...");
		downloadPreviewProgress.show();

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
		sampleFontDL = 12;

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

					//Create new typefaces from downloaded preview fonts
					Typeface regular = Typeface.createFromFile("/sdcard/SampleFonts/Roboto-Regular.ttf");
					Typeface italic = Typeface.createFromFile("/sdcard/SampleFonts/Roboto-Italic.ttf");
					Typeface bold = Typeface.createFromFile("/sdcard/SampleFonts/Roboto-Bold.ttf");
					Typeface boldItalic = Typeface.createFromFile("/sdcard/SampleFonts/Roboto-BoldItalic.ttf");
					Typeface light = Typeface.createFromFile("/sdcard/SampleFonts/Roboto-Light.ttf");
					Typeface lightItalic = Typeface.createFromFile("/sdcard/SampleFonts/Roboto-LightItalic.ttf");
					Typeface thin = Typeface.createFromFile("/sdcard/SampleFonts/Roboto-Thin.ttf");
					Typeface thinItalic = Typeface.createFromFile("/sdcard/SampleFonts/Roboto-ThinItalic.ttf");
					Typeface condensed = Typeface.createFromFile("/sdcard/SampleFonts/RobotoCondensed-Regular.ttf");
					Typeface condensedItalic = Typeface.createFromFile("/sdcard/SampleFonts/RobotoCondensed-Italic.ttf");
					Typeface condensedBold = Typeface.createFromFile("/sdcard/SampleFonts/RobotoCondensed-Bold.ttf");
					Typeface condensedBoldItalic = Typeface.createFromFile("/sdcard/SampleFonts/RobotoCondensed-BoldItalic.ttf");

					//open dialog with all styles here
					Dialog fullPreview = new Dialog(context);

					fullPreview.requestWindowFeature(Window.FEATURE_NO_TITLE);
					fullPreview.setContentView(R.layout.full_preview_alert);

					TextView regularTV = (TextView) fullPreview.findViewById(R.id.regular);
					regularTV.setTypeface(regular);

					TextView italicTV = (TextView) fullPreview.findViewById(R.id.italic);
					italicTV.setTypeface(italic);

					TextView boldTV = (TextView) fullPreview.findViewById(R.id.bold);
					boldTV.setTypeface(bold);

					TextView boldItalicTV = (TextView) fullPreview.findViewById(R.id.bolditalic);
					boldItalicTV.setTypeface(boldItalic);

					TextView lightTV = (TextView) fullPreview.findViewById(R.id.light);
					lightTV.setTypeface(light);

					TextView lightItalicTV = (TextView) fullPreview.findViewById(R.id.lightitalic);
					lightItalicTV.setTypeface(lightItalic);

					TextView thinTV = (TextView) fullPreview.findViewById(R.id.thin);
					thinTV.setTypeface(thin);

					TextView thinItalicTV = (TextView) fullPreview.findViewById(R.id.thinitalic);
					thinItalicTV.setTypeface(thinItalic);

					TextView condTV = (TextView) fullPreview.findViewById(R.id.condensed);
					condTV.setTypeface(condensed);

					TextView condItalicTV = (TextView) fullPreview.findViewById(R.id.condenseditalic);
					condItalicTV.setTypeface(condensedItalic);

					TextView condBoldTV = (TextView) fullPreview.findViewById(R.id.condensedbold);
					condBoldTV.setTypeface(condensedBold);

					TextView condBoldItalicTV = (TextView) fullPreview.findViewById(R.id.condensedbolditalic);
					condBoldItalicTV.setTypeface(condensedBoldItalic);

					fullPreview.show();
				}
			}
		};
		context.registerReceiver(receiver1, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


	}
}
