package com.chromium.fontinstaller;

import java.io.DataOutputStream;
import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	Button button1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				
				try {
					Process process = Runtime.getRuntime().exec("su");
					Process process1 = Runtime.getRuntime().exec(new String[] { "su", "-c", "mount -o rw,remount /system"});
					Process process2 = Runtime.getRuntime().exec(new String[] { "su", "-c", "cp /sdcard/SpeedSoftware/Extracted/TerfensFontPack/fonts/Roboto-Bold.ttf /system"});

				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "not found",
							   Toast.LENGTH_LONG).show();				}
				
				
			}

		});
	}

}
