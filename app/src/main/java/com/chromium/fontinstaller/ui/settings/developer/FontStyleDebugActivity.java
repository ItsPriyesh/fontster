package com.chromium.fontinstaller.ui.settings.developer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontFamily;

public class FontStyleDebugActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_font_style_debug);
    final ViewGroup rootView = (ViewGroup) findViewById(R.id.root);
    for (Pair<String, Typeface> nameAndTypeface : FontFamily.getSystemTypefaces()) {
      TextView textView = new TextView(this);
      textView.setGravity(Gravity.CENTER);
      textView.setText(nameAndTypeface.first);
      textView.setTypeface(nameAndTypeface.second);
      rootView.addView(textView);
    }
  }
}
