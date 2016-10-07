package com.hisetu.testrobot;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.hisetu.testrobot.TestHelper.childOf;
import static com.hisetu.testrobot.TestHelper.childOfChildOf;
import static com.hisetu.testrobot.TestHelper.withRecyclerView;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by lucas on 2016/10/7.
 */

public class TestRobot {

    protected ActivityTestRule activityRule;

    private ElapsedTimeIdlingResource idlingResource;

    public TestRobot(ActivityTestRule activityRule) {
        this.activityRule = activityRule;
    }

    public static ViewAssertion hasItemsCount(final int count) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException e) {
                if (!(view instanceof RecyclerView)) {
                    throw e;
                }
                RecyclerView rv = (RecyclerView) view;
                assertThat(rv.getAdapter().getItemCount(), is(count));
            }
        };
    }

    public TestRobot shouldShow(@IdRes int viewId) {
        onView(withId(viewId)).check(matches(isDisplayed()));
        return this;
    }

    public TestRobot shouldHide(@IdRes int viewId) {
        onView(withId(viewId)).check(matches(not(isDisplayed())));
        return this;
    }

    public TestRobot recycleViewItemChildTextShould(@IdRes int recycleViewId, int position,
            @IdRes int viewId, String text) {
        onView(withRecyclerView(recycleViewId).atPositionOnView(position, viewId))
                .check(matches(withText(text)));
        return this;
    }

    public TestRobot recycleViewItemCountShould(@IdRes int recycleViewId, int count) {
        onView(withId(recycleViewId))
                .check(hasItemsCount(count));
        return this;
    }

    public TestRobot clickRecycleChildViewItem(@IdRes int recycleViewId, int position,
            @IdRes int viewId) {
        onView(withId(recycleViewId)).perform(scrollToPosition(position));
        onView(withRecyclerView(recycleViewId).atPositionOnView(position, viewId))
                .perform(click());
        return this;
    }

    public TestRobot clickRecycleChildViewItem(@IdRes int recycleViewId, int position,
            @IdRes int parentViewId, @IdRes int viewId) {
        onView(withId(recycleViewId)).perform(scrollToPosition(position));
        onView(withRecyclerView(recycleViewId)
                .atPositionOnView(position, parentViewId, viewId))
                .perform(click());
        return this;
    }

    public TestRobot clickRecycleChildViewItem(@IdRes int recycleViewId, int position) {
        onView(withId(recycleViewId)).perform(scrollToPosition(position));
        onView(withRecyclerView(recycleViewId)
                .atPositionOnView(position, -1))
                .perform(click());
        return this;
    }

    public TestRobot toastShouldShow(@StringRes int stringRes) throws InterruptedException {
        onView(withText(stringRes))
                .inRoot(withDecorView(Matchers.not(is(getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        return this;
    }

    public TestRobot toastShouldShow(String text) throws InterruptedException {
        onView(withText(text))
                .inRoot(withDecorView(Matchers.not(is(getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        return this;
    }

    public TestRobot clickView(@IdRes int viewId) {
        onView(withId(viewId))
                .perform(click());
        return this;
    }

    public TestRobot scrollToClickView(@IdRes int viewId) {
        onView(withId(viewId))
                .perform(scrollTo(), click());
        return this;
    }

    public TestRobot clickView(String text) {
        onView(withText(text))
                .perform(click());
        return this;
    }

    public TestRobot scrollToClickView(String text) {
        onView(withText(text))
                .perform(scrollTo(), click());
        return this;
    }

    public TestRobot clickChildView(@IdRes int viewId, int position, @IdRes int childViewId) {
        onView(childOfChildOf(withId(viewId), position, withId(childViewId)))
                .perform(click());
        return this;
    }

    public TestRobot clickChildViewWithText(@IdRes int viewId, int position,
            @StringRes int textRes) {
        onView(childOfChildOf(withId(viewId), position, withText(getString(textRes))))
                .perform(click());
        return this;
    }

    public TestRobot clickChildViewWithText(@IdRes int viewId, int position, String text) {
        onView(childOfChildOf(withId(viewId), position, withText(text)))
                .perform(click());
        return this;
    }

    protected Activity getActivity() {
        return activityRule.getActivity();
    }

    public TestRobot sleep(int timeInMilliSec) {
        IdlingPolicies.setMasterPolicyTimeout(timeInMilliSec * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(timeInMilliSec * 2, TimeUnit.MILLISECONDS);

        idlingResource = new ElapsedTimeIdlingResource(timeInMilliSec);
        IdlingResource idlingResource = this.idlingResource;
        registerIdlingResources(idlingResource);
        return this;
    }

    public TestRobot wakeup() {
        if (idlingResource != null) {
            unregisterIdlingResources(idlingResource);
        }
        return this;
    }

    public TestRobot loadScreen() {
        loadScreen(new Intent());
        return this;
    }

    public TestRobot loadScreen(Intent intent) {
        activityRule.launchActivity(intent);
        return this;
    }

    public TestRobot titleShould(@StringRes int stringRes) {
        shouldWithText(R.id.title, getActivity().getString(stringRes));
        return this;
    }

    public TestRobot titleShould(String string) {
        shouldWithText(R.id.title, string);
        return this;
    }

    public TestRobot spinnerSelectedItemTextShould(@IdRes int spinnerId, String text) {
        onView(withId(spinnerId))
                .check(matches(withSpinnerText(containsString(text))));
        return this;
    }

    public TestRobot spinnerItemTextShould(@IdRes int spinnerId, int position, String text) {
        onData(Matchers.is(instanceOf(String.class)))
                .inAdapterView(withId(spinnerId))
                .atPosition(position)
                .check(matches(withText(text)));
        return this;
    }

    public TestRobot spinnerSelectItemTo(@IdRes int spinnerId, String text) {
        clickView(spinnerId);
        onData(allOf(Matchers.is(instanceOf(String.class)), Matchers.is(text)))
                .perform(click());
        return this;
    }

    public TestRobot recycleViewItemChildViewShouldHide(@IdRes int recycleViewId, int position,
            @IdRes int itemChildViewId) {
        onView(withRecyclerView(recycleViewId).atPositionOnView(position, itemChildViewId))
                .check(matches(not(isDisplayed())));
        return this;
    }

    public TestRobot recycleViewItemChildViewShouldShow(@IdRes int recycleViewId, int position,
            @IdRes int itemChildViewId) {
        onView(withRecyclerView(recycleViewId).atPositionOnView(position, itemChildViewId))
                .check(matches(isDisplayed()));
        return this;
    }

    public void noImplementError() {
        assertTrue("No implement", false);
    }

    public void screenShouldClosed() {
        assertThat(getActivity().isFinishing(), Matchers.is(true));
    }

    public String getString(@StringRes int textRes) {
        return getActivity().getString(textRes);
    }

    public TestRobot shouldWithText(@IdRes int viewId, String text) {
        onView(withId(viewId))
                .check(matches(withText(text)));
        return this;
    }

    public TestRobot shouldWithText(String text) {
        onView(withText(text))
                .check(matches(withText(text)));
        return this;
    }

    public TestRobot clickTextLink(@IdRes int viewId, String linkText) {
        onView(withId(viewId))
                .perform(new OpenClickableSpanAction(is(linkText)));
        return this;
    }

    public TestRobot shouldSelected(@IdRes int viewId) {
        onView(withId(viewId))
                .check(matches(isSelected()));
        return this;
    }


    public TestRobot shouldNotSelected(@IdRes int viewId) {
        onView(withId(viewId))
                .check(matches(not(isSelected())));
        return this;
    }

    public TestRobot viewChildTextShould(@IdRes int viewId, int position, String text) {
        onView(childOf(withId(viewId), position))
                .check(matches(withText(text)));
        return this;
    }

    public TestRobot viewChildTextShould(@IdRes int viewId, int position, @IdRes int childViewId,
            String text) {
        onView(childOfChildOf(withId(viewId), position, withId(childViewId)))
                .check(matches(withText(text)));
        return this;
    }

    public TestRobot fieldShouldShowError(String field, String errorMessage) {
        onView(AllOf.allOf(hasSibling(withText(field)), CoreMatchers.instanceOf(EditText.class)))
                .check(matches(TestHelper.hasErrorText(is(errorMessage))));
        return this;
    }

    public TestRobot shouldNotShowError(String fieldName) {
        onView(AllOf
                .allOf(hasSibling(withText(fieldName)), CoreMatchers.instanceOf(EditText.class)))
                .check(matches(TestHelper.hasNoErrorText()));
        return this;
    }

    public TestRobot shouldNotShowError(int viewId) {
        onView(withId(viewId))
                .check(matches(TestHelper.hasNoErrorText()));
        return this;
    }

    public TestRobot typeText(String fieldName, String text) {
        try {
            onView(AllOf.allOf(hasSibling(withText(fieldName)),
                    CoreMatchers.instanceOf(EditText.class)))
                    .perform(clearText(), ViewActions.typeText(text));
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("replaceText")) {
                throw e;
            }
            onView(AllOf.allOf(hasSibling(withText(fieldName)),
                    CoreMatchers.instanceOf(EditText.class)))
                    .perform(clearText(), replaceText(text));
        } finally {
            Espresso.closeSoftKeyboard();
        }
        return this;
    }

    public TestRobot scrolltoTypeText(String fieldName, String text) {
        try {
            onView(AllOf.allOf(hasSibling(withText(fieldName)),
                    CoreMatchers.instanceOf(EditText.class)))
                    .perform(scrollTo(), clearText(), ViewActions.typeText(text));
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("replaceText")) {
                throw e;
            }
            onView(AllOf.allOf(hasSibling(withText(fieldName)),
                    CoreMatchers.instanceOf(EditText.class)))
                    .perform(scrollTo(), clearText(), replaceText(text));
        } finally {
            Espresso.closeSoftKeyboard();
        }
        return this;
    }

    public TestRobot typeText(int viewId, String text) {
        try {
            onView(withId(viewId))
                    .perform(clearText(), ViewActions.typeText(text));
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("replaceText")) {
                throw e;
            }
            onView(withId(viewId))
                    .perform(clearText(), replaceText(text));
        } finally {
            Espresso.closeSoftKeyboard();
        }
        return this;
    }

    public TestRobot scrolltoTypeText(int viewId, String text) {
        try {
            onView(withId(viewId))
                    .perform(scrollTo(), clearText(), ViewActions.typeText(text));
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("replaceText")) {
                throw e;
            }
            onView(withId(viewId))
                    .perform(scrollTo(), clearText(), replaceText(text));
        } finally {
            Espresso.closeSoftKeyboard();
        }
        return this;
    }

    public TestRobot shouldShowError(@IdRes int viewId, String errorMessage) {
        onView(withId(viewId))
                .check(matches(TestHelper.hasErrorText(is(errorMessage))));
        return this;
    }
}