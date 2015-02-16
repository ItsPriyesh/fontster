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

import android.content.Context;

/**
 * Created by priyeshpatel on 15-02-10.
 */
public class ViewUtils {
    public int dpToPixels(int dp, Context context) {
        final float SCALE = context.getResources().getDisplayMetrics().density;
        return (int) (dp * SCALE + 0.5f);
    }
}