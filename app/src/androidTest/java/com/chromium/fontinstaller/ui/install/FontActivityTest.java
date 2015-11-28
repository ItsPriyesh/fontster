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

package com.chromium.fontinstaller.ui.install;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.chromium.fontinstaller.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FontActivityTest {

    private static final String FONT_NAME = "Aleo";
    private static final String TEST_INPUT = "This is a test";

    @Rule
    public ActivityTestRule<FontActivity> mActivityRule =
            new ActivityTestRule<>(FontActivity.class, true, false);

    @Before
    public void startActivity() {
        final Context context = InstrumentationRegistry.getTargetContext();
        final Intent launchIntent = FontActivity.getLaunchIntent(context, FONT_NAME);
        mActivityRule.launchActivity(launchIntent);
    }

    @Test
    public void testInitialViewStates() {
        onView(withId(R.id.font_name)).check(matches(withText(FONT_NAME)));
        onView(withText(R.string.font_activity_tab_regular)).check(matches(isSelected()));
        onView(allOf(withId(R.id.preview_text), isDisplayed())).check(matches(withText(R.string.alphabet_upper)));
    }

    @Test
    public void testToggleCase() {
        onView(withId(R.id.toggle_case)).perform(click());
        onView(allOf(withId(R.id.preview_text), isDisplayed())).check(matches(withText(R.string.alphabet_lower)));
        onView(withId(R.id.toggle_case)).perform(click());
        onView(allOf(withId(R.id.preview_text), isDisplayed())).check(matches(withText(R.string.alphabet_upper)));
    }

    @Test
    public void testTryFontDialog() {
        onView(withId(R.id.try_font)).perform(click());
        onView(withId(R.id.input)).perform(typeText(TEST_INPUT));
        onView(withText(R.string.done)).perform(click());
        onView(allOf(withId(R.id.preview_text), isDisplayed())).check(matches(withText(TEST_INPUT)));
    }
}