package com.chromium.fontinstaller;

import java.io.IOException;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	Button openFontList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		openFontList = (Button)findViewById(R.id.openFontList);
		openFontList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent fontListActivity = new Intent(MainActivity.this, FontList.class);
				startActivity(fontListActivity);
				
			}
		});
		
	}

}
