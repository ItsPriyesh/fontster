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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Contains all AlertDialog's used throughout
 * the app. 
 * 
 * @author Priyesh
 *
 */
public class CustomAlerts{

	/**
	 * This displays a basic dialog box containing a
	 * title, message and context of the activity that
	 * it is being called from.
	 * 
	 * @param title
	 * @param message
	 * @param context
	 */
	public static void showBasicAlert (String title, String message, Context context) { 

		Dialog help = new Dialog(context);

		help.requestWindowFeature(Window.FEATURE_NO_TITLE);
		help.setContentView(R.layout.basic_alert);	
		TextView alertTitle = (TextView) help.findViewById(R.id.title);
		alertTitle.setText(title);
		TextView alertMessage = (TextView) help.findViewById(R.id.message);
		alertMessage.setText(message);
		help.show();
	}

	/**
	 * This displays a basic dialog box containing a
	 * title, message and context of the activity that
	 * it is being called from. In addition it contains
	 * an image inside of the description. This is used
	 * in the Instructions dialog to indicate to the user
	 * the significance of the checkmark icon.
	 * 
	 * @param title
	 * @param message
	 * @param context
	 */
	public static void showBasicAlertWithImage (String title, String message, Context context) { 

		Dialog help = new Dialog(context);

		help.requestWindowFeature(Window.FEATURE_NO_TITLE);
		help.setContentView(R.layout.basic_image_alert);	
		TextView alertTitle = (TextView) help.findViewById(R.id.title);
		alertTitle.setText(title);
		TextView alertMessage = (TextView) help.findViewById(R.id.message);
		alertMessage.setText(message);

		TextView imageMessage  = (TextView) help.findViewById (R.id.imageMessage); 

		Drawable d = context.getResources().getDrawable(R.drawable.ic_action_accept); 
		d.setBounds(0, 0, 40, 40); 

		ImageSpan is = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM); 

		SpannableString texts = new SpannableString(imageMessage.getText().toString().concat(""));
		texts.setSpan(is, 4, 5, 0);

		imageMessage.setText(texts);

		help.show();
	}

	/**
	 * This method is called when the user selects the
	 * 'Request a font' button inside of the About activity.
	 * It contains a textfield for the user to input their
	 * desired font, and a button to send the request.
	 * 
	 * This request feature works essentially through email.
	 * The method contains the credentials for the email 
	 * address and presets the subject and recipient, while
	 * the actual message is made up from the text that the user
	 * has input into the font name field.
	 * 
	 * @param context
	 */
	public static void showRequestAlert (final Context context) { 

		final Dialog req = new Dialog(context);

		req.requestWindowFeature(Window.FEATURE_NO_TITLE);
		req.setContentView(R.layout.request_alert);	

		final EditText fontReqET = (EditText) req.findViewById(R.id.reqFont);

		Button sendReq = (Button) req.findViewById(R.id.sendReq);
		sendReq.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				req.dismiss();			

				AsyncTask<Void, Void, String> sendEmail = new AsyncTask <Void, Void, String>() {
					ProgressDialog sendProgress;

					@Override
					protected String doInBackground(Void... params)
					{
						try {
							GMailSender sender = new GMailSender("fontsterapp@gmail.com","Fontster123");
							sender.sendMail("Font Request", fontReqET.getText().toString(), "fontsterapp@gmail.com", "priyesh.96@hotmail.com");
						}
						catch(Exception e)
						{
							Log.e("error",e.getMessage(),e);
							return "Email Not Sent";
						}
						return "Email Sent";
					}

					@Override
					protected void onPostExecute(String result)
					{
						sendProgress.dismiss();
						CustomAlerts.showBasicAlert("Sent successfully", "Your request for '" + fontReqET.getText().toString() + "' " +
								"has been sent to the development team. We will try to add it as soon as possible.", context);
					}
					@Override
					protected void onPreExecute()
					{
						sendProgress = new ProgressDialog(context);
						sendProgress.setMessage("Sending...");
						sendProgress.show();
					}

					@Override
					protected void onProgressUpdate(Void... values)
					{
					}
				};
				sendEmail.execute((Void[])null);
			}			
		});
		req.show();
	}

	/**
	 * This displays the dialog shown when the user long presses
	 * a font in the FontList activity which initiates a preview.
	 * The dialog shows the name of the font, and a sample sentence
	 * containing every letter of the English alphabet (pangram) in
	 * the selected font. In addition it has a textfield allowing the
	 * user to try out the font using their own custom text. Finally
	 * there is a button labelled 'View all styles' which will display
	 * all 12 font classes (bold, italic, etc.) when clicked.
	 * 
	 * @param title
	 * @param message
	 * @param font - the actual TypeFace variable that is to be set
	 * @param fontName - the name of the font stored as a string
	 * @param context
	 */
	public static void showPreviewAlert (String title, String message, Typeface font, final String fontName, final Context context) { 

		final Dialog preview = new Dialog(context);

		preview.requestWindowFeature(Window.FEATURE_NO_TITLE);
		preview.setContentView(R.layout.preview_alert);	

		TextView alertTitle = (TextView) preview.findViewById(R.id.title);
		alertTitle.setTypeface(font);
		alertTitle.setText(title);

		TextView alertMessage = (TextView) preview.findViewById(R.id.message);
		alertMessage.setTypeface(font);
		alertMessage.setText(message);

		EditText testFont = (EditText) preview.findViewById(R.id.testFont);
		testFont.setTypeface (font);

		Button viewAllVariants = (Button) preview.findViewById(R.id.viewAllVariants);
		viewAllVariants.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				preview.dismiss();
				Preview.fullPreviewAlert(fontName, context);
			}			
		});

		preview.show();
	}

	/**
	 * Display's a dialog prompting the user to reboot
	 * their device after installing a font. 
	 * 
	 * @param title
	 * @param message
	 * @param button - When clicked, this will reboot the device.
	 * @param context
	 */
	public static void showRebootAlert (String title, String message, String button, Context context) { 

		Dialog reboot = new Dialog(context);

		Typeface fallbackCondensed = Typeface.createFromFile ("/sdcard/Fontster/FontFallback/RobotoCondensed-Regular.ttf");
		Typeface fallbackLight = Typeface.createFromFile ("/sdcard/Fontster/FontFallback/Roboto-Light.ttf");

		reboot.requestWindowFeature(Window.FEATURE_NO_TITLE);
		reboot.setContentView(R.layout.single_button_alert);	

		TextView alertTitle = (TextView) reboot.findViewById(R.id.title);
		alertTitle.setTypeface(fallbackCondensed);
		alertTitle.setText(title);

		TextView alertMessage = (TextView) reboot.findViewById(R.id.message);
		alertMessage.setTypeface(fallbackLight);
		alertMessage.setText(message);

		Button positiveButton = (Button) reboot.findViewById(R.id.positive);
		positiveButton.setTypeface(fallbackLight);
		positiveButton.setText(button);

		positiveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){

				try {
					Process reboot = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot"});
				}
				catch (IOException e) {
					e.printStackTrace();
				}

			}			
		});

		reboot.show();
	}

	/**
	 * Basic dialog containing a title, message and generic button.
	 * When button is clicked the dialog will be dismessed.
	 * 
	 * @param title
	 * @param message
	 * @param context
	 */
	public static void showSingleButtonAlert (String title, String message, Context context) { 

		final Dialog singleButtonDialog = new Dialog(context);

		singleButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		singleButtonDialog.setContentView(R.layout.single_button_alert);	
		TextView alertTitle = (TextView) singleButtonDialog.findViewById(R.id.title);
		alertTitle.setText(title);
		TextView alertMessage = (TextView) singleButtonDialog.findViewById(R.id.message);
		alertMessage.setText(message);
		Button positiveButton = (Button) singleButtonDialog.findViewById(R.id.positive);
		positiveButton.setText("OK");

		positiveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				singleButtonDialog.dismiss();
			}			
		});

		singleButtonDialog.show();
	}
}
