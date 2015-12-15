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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.chromium.fontinstaller.BuildConfig;
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

  @Bind(R.id.parent)
  View mParent;

  private static final long INTRO_ANIMATION_DURATION = 950L;
  private static final long OUTRO_ANIMATION_DURATION = 500L;
  private static final long INBETWEEN_ANIMATION_DURATION = 200L;

  private static final int PERMISSION_REQUEST = 0xaf;

  private static final String[] STORAGE_PERMISSIONS = {
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE
  };

  private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();

  private Handler mHandler = new Handler(Looper.getMainLooper());

  private final Runnable mIntroAnimation = () ->
      mSplashLogo.animate()
          .scaleX(1)
          .scaleY(1)
          .rotation(0)
          .setDuration((long) (INTRO_ANIMATION_DURATION * 0.8))
          .setInterpolator(INTERPOLATOR)
          .setListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
              super.onAnimationEnd(animation);
              mTitleView.animate()
                  .alpha(1)
                  .setDuration((long) (INTRO_ANIMATION_DURATION * 0.2))
                  .setInterpolator(INTERPOLATOR);
              delay(mCheckForRoot, INBETWEEN_ANIMATION_DURATION);
            }
          });

  private final Runnable mOutroAnimation = () -> {
    mSplashLogo.animate()
        .translationY(mParent.getBottom() + mSplashLogo.getHeight())
        .setDuration(OUTRO_ANIMATION_DURATION)
        .setInterpolator(INTERPOLATOR);

    mTitleView.animate()
        .alpha(0)
        .setDuration(OUTRO_ANIMATION_DURATION)
        .setInterpolator(INTERPOLATOR);
  };

  private final Runnable mGoToMain = () -> {
    delay(mOutroAnimation, INBETWEEN_ANIMATION_DURATION);
    delay(() -> {
      final Intent intent = new Intent(this, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
    }, INBETWEEN_ANIMATION_DURATION + OUTRO_ANIMATION_DURATION);
  };

  private final Runnable mRequestStorageAccess = () -> {
    if (!storagePermissionsGranted()) {
      ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, PERMISSION_REQUEST);
    }
  };

  private final Runnable mCheckForRoot = () -> Observable.defer(() ->
      Observable.just(BuildConfig.DEBUG || Shell.SU.available()))
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(available -> {
        if (available) {
          delay(storagePermissionsGranted() ? mGoToMain : mRequestStorageAccess,
              INBETWEEN_ANIMATION_DURATION);
        } else {
          showMissingPermissionDialog(
              R.string.splash_no_root_title,
              R.string.splash_no_root_message,
              this.mCheckForRoot);
        }
      });

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    ButterKnife.bind(this);

    mTitleView.setTypeface(Typeface.createFromAsset(getAssets(), "Quicksand-Regular.ttf"));

    mSplashLogo.setScaleX(0);
    mSplashLogo.setScaleY(0);
    mSplashLogo.setRotation(-1080);
    mSplashLogo.setVisibility(View.VISIBLE);

    mHandler.postDelayed(mIntroAnimation, INBETWEEN_ANIMATION_DURATION);
  }

  @Override protected void onStop() {
    super.onStop();
    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
      mHandler = null;
    }
  }

  private void delay(Runnable runnable, long delay) {
    if (mHandler != null) {
      mHandler.postDelayed(runnable, delay);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                         @NonNull int[] grantResults) {
    if (requestCode == PERMISSION_REQUEST) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        delay(mGoToMain, INBETWEEN_ANIMATION_DURATION);
      } else {
        showMissingPermissionDialog(
            R.string.splash_no_storage_access_title,
            R.string.splash_no_storage_access_message,
            mRequestStorageAccess);
      }
    }
  }

  private void showMissingPermissionDialog(int titleResId, int messageResId, Runnable retry) {
    if (isFinishing()) return;
    new AlertDialog.Builder(this)
        .setTitle(titleResId)
        .setMessage(messageResId)
        .setCancelable(false)
        .setPositiveButton(R.string.retry, ((dialog, which) -> retry.run()))
        .setNegativeButton(R.string.exit, (dialog, which) -> finish())
        .create().show();
  }

  private boolean storagePermissionsGranted() {
    return ContextCompat.checkSelfPermission(this, STORAGE_PERMISSIONS[0])
        == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, STORAGE_PERMISSIONS[1])
        == PackageManager.PERMISSION_GRANTED;
  }

}