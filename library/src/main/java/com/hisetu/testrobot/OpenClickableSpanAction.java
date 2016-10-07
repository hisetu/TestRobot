package com.hisetu.testrobot;

import org.hamcrest.Matcher;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.HumanReadables;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

public class OpenClickableSpanAction implements ViewAction {

    private final Matcher<String> spanTextMatcher;

    public OpenClickableSpanAction(Matcher<String> spanTextMatcher) {
        this.spanTextMatcher = spanTextMatcher;
    }

    @Override
    public Matcher<View> getConstraints() {
        return allOf(isDisplayed(), isAssignableFrom(TextView.class));
    }

    @Override
    public String getDescription() {
        return String.format("open ClickableSpan with text %s", spanTextMatcher);
    }

    @Override
    public void perform(UiController uiController, View view) {
        TextView textView = (TextView) view;
        String allText = textView.getText().toString();
        Spanned spanned = (Spanned) textView.getText();

        ClickableSpan[] spannedSpans = spanned
                .getSpans(0, allText.length(), ClickableSpan.class);
        for (ClickableSpan clickableSpan : spannedSpans) {
            int spanStart = spanned.getSpanStart(clickableSpan);
            int spanEnd = spanned.getSpanEnd(clickableSpan);
            if (spanTextMatcher.matches(allText.substring(spanStart, spanEnd))) {
                clickableSpan.onClick(view);
                return;
            }
        }
        throw new PerformException.Builder()
                .withActionDescription(this.getDescription())
                .withViewDescription(HumanReadables.describe(view))
                .withCause(new RuntimeException(String.format(
                        "ClickableSpan with text '%s'not found", spanTextMatcher)))
                .build();
    }
}
