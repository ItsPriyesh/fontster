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

package com.chromium.fontinstaller.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroupOverlay;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.chromium.fontinstaller.R;

import timber.log.Timber;

public class ViewUtils {

  public static int dpToPixels(int dp, Context context) {
    final float SCALE = context.getResources().getDisplayMetrics().density;
    return (int) (dp * SCALE + 0.5f);
  }

  public static boolean isLollipop() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  public static int getStatusBarHeight(Context context) {
    final Resources resources = context.getResources();
    final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
    return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
  }

  public static void reveal(Activity activity, View view, View sourceView, int colorRes) {
    if (activity == null || view == null || sourceView == null) return;
    if (isLollipop()) {
      final ViewGroupOverlay groupOverlay =
          (ViewGroupOverlay) activity.getWindow().getDecorView().getOverlay();

      final Rect displayRect = new Rect();
      view.getGlobalVisibleRect(displayRect);

      // Make reveal cover the display and status bar.
      final View revealView = new View(activity);
      revealView.setTop(displayRect.top);
      revealView.setBottom(displayRect.bottom);
      revealView.setLeft(displayRect.left);
      revealView.setRight(displayRect.right);
      revealView.setBackgroundColor(ContextCompat.getColor(activity, colorRes));
      groupOverlay.add(revealView);

      final int[] clearLocation = new int[2];
      sourceView.getLocationInWindow(clearLocation);
      clearLocation[0] += sourceView.getWidth() / 2;
      clearLocation[1] += sourceView.getHeight() / 2;

      final int revealCenterX = clearLocation[0] - revealView.getLeft();
      final int revealCenterY = clearLocation[1] - revealView.getTop();

      final double x1_2 = Math.pow(revealView.getLeft() - revealCenterX, 2);
      final double x2_2 = Math.pow(revealView.getRight() - revealCenterX, 2);
      final double y_2 = Math.pow(revealView.getTop() - revealCenterY, 2);
      final float revealRadius = (float) Math.max(Math.sqrt(x1_2 + y_2), Math.sqrt(x2_2 + y_2));

      try {
        final Animator revealAnimator =
            ViewAnimationUtils.createCircularReveal(revealView,
                revealCenterX, revealCenterY, 0.0f, revealRadius);
        revealAnimator.setDuration(
            activity.getResources().getInteger(android.R.integer.config_mediumAnimTime));

        final Animator alphaAnimator = ObjectAnimator.ofFloat(revealView, View.ALPHA, 0.0f);
        alphaAnimator.setDuration(
            activity.getResources().getInteger(android.R.integer.config_shortAnimTime));
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.abc_fade_in));
            view.setVisibility(View.VISIBLE);
          }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator).before(alphaAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animator) {
            groupOverlay.remove(revealView);
          }
        });

        animatorSet.start();
      } catch (IllegalStateException e) {
        Timber.i("View is detached - not animating");
      }
    } else {
      view.setVisibility(View.VISIBLE);
    }
  }

  public static void animGrowFromCenter(View view, Context context) {
    if (context == null || view == null) return;
    view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.grow_from_center));
  }

  public static void animShrinkToCenter(View view, Context context) {
    if (context == null || view == null) return;
    view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shrink_to_center));
  }

  public static void animSlideInBottom(View view, Context context) {
    if (context == null || view == null) return;
    view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom));
  }

  public static void animSlideUp(View view, Context context) {
    if (context == null || view == null) return;
    view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_top));
  }

  public static void snackbar(int messageResId, View parent) {
    if (parent == null) return;
    Snackbar.make(parent, messageResId, Snackbar.LENGTH_SHORT).show();
  }

  public static void snackbar(String message, View parent) {
    if (parent == null) return;
    Snackbar.make(parent, message, Snackbar.LENGTH_SHORT).show();
  }

  public static void toast(int messageResId, Context context) {
    Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
  }

  public static void toast(String message, Context context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public static void toast(int messageResId, Fragment fragment) {
    toast(messageResId, fragment.getActivity());
  }

  public static void toast(String message, Fragment fragment) {
    toast(message, fragment.getActivity());
  }

}
