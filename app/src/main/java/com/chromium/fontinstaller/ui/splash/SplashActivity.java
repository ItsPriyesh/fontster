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
import android.content.Intent;
import android.os.Bundle;
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
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.chainfire.libsuperuser.Shell;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

  @Bind(R.id.splash_logo)
  ImageView mSplashLogo;

  @Bind(R.id.title)
  TextView mTitleView;

  @Bind(R.id.parent)
  View mParent;

  private static final long INTRO_ANIMATION_DURATION = 950L;
  private static final long EXIT_ANIMATION_DURATION = 500L;
  private static final long TRANSITION_DURATION = 200L;

  private static final String[] STORAGE_PERMISSIONS = {
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE
  };

  private static final Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
  private boolean mAnimating = false;

  private void startIntroAnimation(Action0 onComplete) {
    mAnimating = true;
    mSplashLogo.animate()
        .scaleX(1)
        .scaleY(1)
        .rotation(0)
        .setDuration((long) (INTRO_ANIMATION_DURATION * 0.8))
        .setInterpolator(sInterpolator)
        .withEndAction(() -> mTitleView.animate()
            .alpha(1)
            .setDuration((long) (INTRO_ANIMATION_DURATION * 0.2))
            .setInterpolator(sInterpolator)
            .withEndAction(onComplete::call));
  }

  private void startExitAnimation(Action0 onComplete) {
    mSplashLogo.animate()
        .setStartDelay(TRANSITION_DURATION)
        .translationY(mParent.getBottom() + mSplashLogo.getHeight())
        .setDuration(EXIT_ANIMATION_DURATION)
        .setInterpolator(sInterpolator);

    mTitleView.animate()
        .setStartDelay(TRANSITION_DURATION)
        .alpha(0)
        .setDuration(EXIT_ANIMATION_DURATION)
        .setInterpolator(sInterpolator)
        .withEndAction(onComplete::call);
  }

  private Observable<Boolean> requestStorageAccess() {
    return RxPermissions.getInstance(this).request(STORAGE_PERMISSIONS);
  }

  private Observable<Boolean> checkForRoot() {
    return Observable.just(BuildConfig.DEBUG || Shell.SU.available())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private void enterApplication() {
    final Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    ButterKnife.bind(this);
  }

  @Override protected void onResume() {
    super.onResume();

    if (mAnimating) return;

    mSplashLogo.setScaleX(0);
    mSplashLogo.setScaleY(0);
    mSplashLogo.setRotation(-1080);
    mSplashLogo.setVisibility(View.VISIBLE);

    startIntroAnimation(() -> checkForRoot().subscribe(rootAvailable -> {
      if (rootAvailable) requestStorageAccess().subscribe(permissionsGranted -> {
        if (permissionsGranted) startExitAnimation(this::enterApplication);
        else errorDialog(
            R.string.splash_no_storage_access_title,
            R.string.splash_no_storage_access_message
        );
      });
      else errorDialog(
          R.string.splash_no_root_title,
          R.string.splash_no_root_message
      );
    }));
  }

  @Override protected void onStop() {
    super.onStop();
    mAnimating = false;
  }

  private void errorDialog(int titleResId, int messageResId) {
    if (isFinishing()) return;
    new AlertDialog.Builder(this)
        .setTitle(titleResId)
        .setMessage(messageResId)
        .setCancelable(false)
        .setPositiveButton(R.string.exit, (dialog, which) -> finish())
        .create().show();
  }

}