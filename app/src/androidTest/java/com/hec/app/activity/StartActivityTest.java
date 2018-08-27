package com.hec.app.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.hec.app.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StartActivityTest {

//    @Rule
//    public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(StartActivity.class);

    @Test
    public void startActivityTest() {

        ViewInteraction appCompatAutoCompleteTextView = onView(
                allOf(withId(R.id.txtUserName),
                        withParent(withId(R.id.relativeLayout)),
                        isDisplayed()));
        appCompatAutoCompleteTextView.perform(click());

        ViewInteraction appCompatAutoCompleteTextView2 = onView(
                allOf(withId(R.id.txtUserName),
                        withParent(withId(R.id.relativeLayout)),
                        isDisplayed()));
        appCompatAutoCompleteTextView2.perform(replaceText("tomtom"), closeSoftKeyboard());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.txtPwd), isDisplayed()));
        appCompatEditText.perform(replaceText("a123456"), closeSoftKeyboard());

        ViewInteraction appCompatCheckBox = onView(
                allOf(withId(R.id.rmb), withText("记住密码"), isDisplayed()));
        appCompatCheckBox.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.btnUserLogin), isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction linearLayout = onView(
                childAtPosition(
                        withId(R.id.gridviewFavorite),
                        1));
        linearLayout.perform(scrollTo(), click());

        ViewInteraction circleButton = onView(
                allOf(withText("3"), isDisplayed()));
        circleButton.perform(click());

        ViewInteraction circleButton2 = onView(
                allOf(withText("4"), isDisplayed()));
        circleButton2.perform(click());

        ViewInteraction circleButton3 = onView(
                allOf(withText("1"), isDisplayed()));
        circleButton3.perform(click());

        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.ll_ok),
                        withParent(allOf(withId(R.id.bottom_ll_info),
                                withParent(withId(R.id.lotteryContainer)))),
                        isDisplayed()));
        linearLayout2.perform(click());

        ViewInteraction linearLayout3 = onView(
                allOf(withId(R.id.ll_ok), isDisplayed()));
        linearLayout3.perform(click());

        ViewInteraction linearLayout4 = onView(
                allOf(withId(R.id.ll_ok), isDisplayed()));
        linearLayout4.perform(click());

        ViewInteraction linearLayout5 = onView(
                allOf(withId(R.id.ll_return), isDisplayed()));
        linearLayout5.perform(click());

        ViewInteraction imageButton = onView(
                allOf(withClassName(is("android.widget.ImageButton")),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        imageButton.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
