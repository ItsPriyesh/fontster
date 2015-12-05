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

package com.chromium.fontinstaller.ui.main;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ListView;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

  private static final String TEST_SEARCH_INPUT = "Ca";

  @Rule public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

  @Test public void testToolbarTitles() throws Exception {
    assertToolbarTitleEquals(R.string.app_name);

    openDrawer();
    onView(withText(R.string.drawer_item_backup_restore)).perform(click());
    assertToolbarTitleEquals(R.string.drawer_item_backup_restore);

    openDrawer();
    onView(withText(R.string.drawer_item_font_list)).perform(click());
    assertToolbarTitleEquals(R.string.app_name);
  }

  @Test public void testNavigationDrawerItems() throws Exception {
    openDrawer();
    final ViewInteraction fontListItem = onView(withText(R.string.drawer_item_font_list));
    fontListItem.check(matches(isChecked()));

    final ViewInteraction backupItem = onView(allOf(
        withText(R.string.drawer_item_backup_restore),
        withId(R.id.design_menu_item_text)));

    backupItem.perform(click());
    openDrawer();
    backupItem.check(matches(isChecked()));
  }

  @Test public void testSearchBar() throws Exception {
    onView(withId(R.id.action_search)).perform(click());
    onView(withId(R.id.searchTextView)).perform(typeText(TEST_SEARCH_INPUT));

    final Set<String> expectedSearchResults = getFontNamesStartingWith(TEST_SEARCH_INPUT);
    for (String result : expectedSearchResults) {
      onData(hasToString(result))
          .inAdapterView(withClassName(equalTo(ListView.class.getName())))
          .check(matches(isDisplayed()));
    }
  }

  private static Set<String> getFontNamesStartingWith(String startingString) {
    final Resources resources = InstrumentationRegistry.getTargetContext().getResources();
    final String[] fontList = resources.getStringArray(R.array.font_list);
    final Set<String> output = new HashSet<>();
    for (String fontName : fontList) {
      if (fontName.startsWith(startingString)) output.add(fontName);
      else if (!output.isEmpty()) break;
    }
    return output;
  }

  private static void openDrawer() {
    onView(withContentDescription(R.string.drawer_open)).perform(click());
  }

  private static ViewInteraction assertToolbarTitleEquals(int resourceId) {
    return onView(allOf(isAssignableFrom(TextView.class),
        withParent(isAssignableFrom(Toolbar.class))))
        .check(matches(withText(resourceId)));
  }

  public static Matcher<Object> withFontName(final String fontName) {
    return new BoundedMatcher<Object, FontPackage>(FontPackage.class) {
      @Override public boolean matchesSafely(FontPackage fontPackage) {
        return fontPackage.getName().matches(fontName);
      }

      @Override public void describeTo(Description description) {
        description.appendText("with font name: ");
      }
    };
  }
}
