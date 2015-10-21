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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chromium.fontinstaller.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.splash_logo)
    ImageView mSplashLogo;

    @Bind(R.id.title)
    TextView mTitleView;

    @Bind(R.id.progress_circle)
    ProgressBar mProgressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        final View parent = findViewById(R.id.parent);
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();

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

        parent.postDelayed(() -> mSplashLogo.animate()
                .scaleX(1)
                .scaleY(1)
                .rotation(0)
                .setDuration(1000)
                .setInterpolator(interpolator)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mTitleView.animate()
                                .alpha(1)
                                .setDuration(200)
                                .setInterpolator(interpolator);
                        mProgressCircle.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(200)
                                .setInterpolator(interpolator);
                    }
                }), 400);
    }

}