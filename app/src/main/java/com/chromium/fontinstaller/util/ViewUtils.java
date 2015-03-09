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
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;

import com.chromium.fontinstaller.R;

/**
 * Created by priyeshpatel on 15-02-10.
 */
public class ViewUtils {

    public int dpToPixels(int dp, Context context) {
        final float SCALE = context.getResources().getDisplayMetrics().density;
        return (int) (dp * SCALE + 0.5f);
    }

    public static void animCenterRevealIn(View view) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        view.setVisibility(View.VISIBLE);
        anim.setDuration(400);
        anim.start();
    }

    public static void animCenterGrowIn(View view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.grow_from_center));
        view.setVisibility(View.VISIBLE);
    }

    public static void animateShrinkToCenter(View view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shrink_to_center));
        view.setVisibility(View.INVISIBLE);
    }

    public static void animSlideBottomIn(View view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom));
        view.setVisibility(View.VISIBLE);
    }

    public static void animSlideUp(View view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_top));
    }
}
