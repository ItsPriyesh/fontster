package com.chromium.fontinstaller;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AboutEE extends Activity{

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(R.layout.about_ee);
	    
	    ImageView imageView = (ImageView)findViewById(R.id.imageView);
		imageView.setImageResource(R.drawable.lol);
	}
}
