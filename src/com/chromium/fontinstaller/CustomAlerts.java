package com.chromium.fontinstaller;

import java.io.IOException;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class CustomAlerts{

	/*
	 * Basic Dialog with Title and Message (NO BUTTONS)
	 */
	public static void showBasicAlert (String title, String message, Context context) { 
		//method to show custom styled dialog. params are the title, the message, and the context of the alert

		Dialog help = new Dialog(context);

		help.requestWindowFeature(Window.FEATURE_NO_TITLE);
		help.setContentView(R.layout.basic_alert);	
		TextView alertTitle = (TextView) help.findViewById(R.id.title);
		alertTitle.setText(title);
		TextView alertMessage = (TextView) help.findViewById(R.id.message);
		alertMessage.setText(message);
		help.show();
	}

	
	public static void showBasicAlertWithImage (String title, String message, Context context) { 
		//method to show custom styled dialog. params are the title, the message, and the context of the alert

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

	/*
	 *  Dialog with Title, Message, and EditText all with custom Typeface (FOR PREVIEWING)
	 */
	public static void showPreviewAlert (String title, String message, Typeface font, final String fontName, final Context context) { 
		//method for preview dialog. has extra param for typeface

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

	/*
	 * Reboot Dialog with Title, Message, and a Button for reboot function
	 */
	public static void showRebootAlert (String title, String message, String button, Context context) { 
		//method to show custom styled dialog. params are the title, message and button of the alert

		Dialog reboot = new Dialog(context);

		Typeface fallbackCondensed = Typeface.createFromFile("/sdcard/FontFallback/RobotoCondensed-Regular.ttf");
		Typeface fallbackLight = Typeface.createFromFile("/sdcard/FontFallback/Roboto-Light.ttf");

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

	/*
	 * Dialog with Title, Message, and single Button that dismisses dialog
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
