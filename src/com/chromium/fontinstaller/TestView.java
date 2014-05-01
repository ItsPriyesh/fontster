package com.chromium.fontinstaller;

import android.app.Activity;
import android.os.Bundle;

public class TestView extends Activity {

	//Inflates the test view layout
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_view);
	}
}
