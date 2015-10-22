/*
 * Copyright 2015 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chromium.fontinstaller.ui.splash;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.ui.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.chainfire.libsuperuser.Shell;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.splash_logo)
    ImageView mSplashLogo;

    @Bind(R.id.title)
    TextView mTitleView;

    @Bind(R.id.progress_circle)
    ProgressBar mProgressCircle;

    @Bind(R.id.parent)
    View mParent;

    private static final long SPLASH_DELAY = 2000L;

    private static final int PERMISSION_REQUEST = 0xaf;
    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private static final Observable<Boolean> ROOT_CHECK = Observable.defer(() -> Observable
            .just(Shell.SU.available())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()));

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mTitleView.setTypeface(Typeface.createFromAsset(getAssets(), "Quicksand-Regular.ttf"));

        mProgressCircle.setScaleX(0);
        mProgressCircle.setScaleY(0);
        mProgressCircle.setVisibility(View.VISIBLE);
        mProgressCircle.getIndeterminateDrawable()
                .setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        mSplashLogo.setScaleX(0);
        mSplashLogo.setScaleY(0);
        mSplashLogo.setRotation(-1080);
        mSplashLogo.setVisibility(View.VISIBLE);

        mParent.postDelayed(() -> mSplashLogo.animate()
                .scaleX(1)
                .scaleY(1)
                .rotation(0)
                .setDuration(1000)
                .setInterpolator(INTERPOLATOR)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mTitleView.animate()
                                .alpha(1)
                                .setDuration(200)
                                .setInterpolator(INTERPOLATOR);
                        showSpinner(true);
                    }
                }), 400);

        new Handler().postDelayed(this::checkForRoot, SPLASH_DELAY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToMain();
            } else {
                showMissingPermissionDialog(
                        R.string.splash_no_storage_access_title,
                        R.string.splash_no_storage_access_message,
                        this::requestStoragePermissions);
            }
        }
    }

    private void showSpinner(boolean show) {
        mProgressCircle.animate()
                .scaleX(show ? 1 : 0)
                .scaleY(show ? 1 : 0)
                .setDuration(200)
                .setInterpolator(INTERPOLATOR);
    }

    private interface RetryListener { void onRetry(); }

    private void showMissingPermissionDialog(int titleResId, int messageResId, RetryListener l) {
        new AlertDialog.Builder(this)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setCancelable(false)
                .setPositiveButton(R.string.retry, ((dialog, which) -> l.onRetry()))
                .setNegativeButton(R.string.ok, (dialog, which) -> finish())
                .create().show();
    }

    private void checkForRoot() {
        ROOT_CHECK.subscribe(available -> {
            if (available) {
                if (storagePermissionsGranted()) {
                    goToMain();
                } else requestStoragePermissions();
            } else {
                showSpinner(false);
                showMissingPermissionDialog(
                        R.string.splash_no_root_title,
                        R.string.splash_no_root_message,
                        this::checkForRoot);
            }
        });
    }

    private void goToMain() {
        mHandler.postDelayed(() -> {
            showSpinner(false);
            final View doneIcon = findViewById(R.id.done_icon);
            doneIcon.setScaleX(0);
            doneIcon.setScaleY(0);
            doneIcon.setVisibility(View.VISIBLE);
            doneIcon.animate()
                    .scaleX(1).scaleY(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR);
        }, SPLASH_DELAY / 2);
        mHandler.postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, SPLASH_DELAY);
    }

    private boolean storagePermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, STORAGE_PERMISSIONS[0])
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, STORAGE_PERMISSIONS[1])
                        != PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermissions() {
        if (storagePermissionsGranted()) {
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, PERMISSION_REQUEST);
        }
    }
}