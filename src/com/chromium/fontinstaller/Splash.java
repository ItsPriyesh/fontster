package com.chromium.fontinstaller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Splash extends Activity {

	ImageButton continueToApp, help;
	TextView prompt;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash);
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
	    	Window win = getWindow();
	    	win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    	win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	    }
	    
	    continueToApp = (ImageButton)findViewById(R.id.icon);
	    continueToApp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				continueToApp.setColorFilter(Color.argb(50, 255, 255, 255)); // White Tint
				Intent main = new Intent (Splash.this, MainActivity.class);
				startActivity (main);
				finish();
			}
		});
	    
	    help = (ImageButton)findViewById(R.id.help);
	    help.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				help.setColorFilter(Color.argb(50, 255, 255, 255)); // White Tint
				
				CustomAlerts.showSingleButtonAlert ("Welcome!", "It is strongly suggested that you backup your current fonts using " +
						"the backup option found in this app prior to installing any custom ones.\n\nFor further safety, " +
						"a recovery flashable zip of the stock fonts has been placed in your downloads folder. In the " +
						"unlikely, but possible event that you encounter issues, please flash this zip.\n", Splash.this);
			}
		});
	    
	    prompt = (TextView)findViewById(R.id.textView1);   
	    Typeface ProximaNovaLight = Typeface.createFromAsset(getAssets(),"fonts/ProximaNovaLight.ttf");
	    prompt.setTypeface(ProximaNovaLight);
	}

}
