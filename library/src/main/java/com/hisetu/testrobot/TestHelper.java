package com.hisetu.testrobot;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import android.support.annotation.IdRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

public class TestHelper {

    public static Matcher<View> isSelected() {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return (view).isSelected();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is-selected=true");
            }
        };
    }

    public static RecyclerViewMatcher withRecyclerView(@IdRes int viewId) {
        return new RecyclerViewMatcher(viewId);
    }

    public static Matcher<View> childOf(final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with " + position + " child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {

                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }
                ViewGroup parent = (ViewGroup) view.getParent();
                return parentMatcher.matches(parent) && parent.getChildAt(position)
                        .equals(view);
            }
        };
    }

    public static Matcher<View> childOfChildOf(final Matcher<View> parentMatcher,
            final int position, final Matcher<View> childView) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with " + position + " child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {

                ViewParent parent = view.getParent();
                if (!(parent instanceof ViewGroup)) {
                    return false;
                }
                ViewParent grandfather = parent.getParent();
                if (!(grandfather instanceof ViewGroup)) {
                    return false;
                }

                return parentMatcher.matches(grandfather)
                        && ((ViewGroup) grandfather).getChildAt(position).equals(parent)
                        && childView.matches(view);
            }
        };
    }

    public static Matcher<View> hasNoErrorText() {
        return new BoundedMatcher<View, EditText>(EditText.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("without error");
            }

            @Override
            protected boolean matchesSafely(EditText view) {
                if (view instanceof TextInputEditText) {
                    if (view.getParent() instanceof TextInputLayout) {
                        TextInputLayout textInputLayout = (TextInputLayout) view.getParent();
                        return textInputLayout.getError() == null;
                    }
                }
                return view.getError() == null;
            }
        };
    }

    public static Matcher<View> hasErrorText(final Matcher<String> stringMatcher) {
        return new BoundedMatcher<View, EditText>(EditText.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("with error: ");
                stringMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(EditText view) {

                if (view instanceof TextInputEditText) {
                    if (view.getParent() instanceof TextInputLayout) {
                        TextInputLayout textInputLayout = (TextInputLayout) view.getParent();
                        return textInputLayout.getError() != null
                                && stringMatcher.matches(textInputLayout.getError());
                    }
                }
                return view.getError() != null && stringMatcher.matches(view.getError().toString());
            }
        };
    }
}
