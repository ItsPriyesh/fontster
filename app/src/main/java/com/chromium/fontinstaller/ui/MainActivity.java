package com.chromium.fontinstaller.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.util.FontDownloader;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Ion;

import java.io.File;

import timber.log.Timber;


public class MainActivity extends ActionBarActivity {
    Future<File> downloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);


    }

    public void download(View view) {
        Timber.i("clicked");
        FontPackage fontPackage = new FontPackage("Lato");
        FontDownloader fontDownloader = new FontDownloader(fontPackage, this);
       fontDownloader.download();

       /* String path = "/sdcard/Fonts/Roboto.ttf";
        //downloading =
        Ion.with(this)
                .load("https://raw.githubusercontent.com/ItsPriyesh/FontsterFontsRepo/master/LatoFontPack/Roboto-Light.ttf")
                .write(new File(getExternalCacheDir() + "/Roboto.ttf"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e != null) {
                            Timber.i("Error downloading " + result);
                            return;
                        }
                        Timber.i("Downloaded " + result);
                    }
                });*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
