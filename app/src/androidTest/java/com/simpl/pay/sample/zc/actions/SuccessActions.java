package com.simpl.pay.sample.zc.actions;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.simpl.pay.sample.zc.R;
import com.simpl.pay.sample.zc.utils.TestUtils;

import org.hamcrest.Matcher;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;

public class SuccessActions {
    private String token = "";

    public SuccessActions fetchToken() {
        TestUtils.wait(1000);
        token = getText(withId(R.id.tvToken));
        onView(withId(R.id.tvToken)).check(matches(isDisplayed()));
        Log.d("Success Actions", "Token: " + token);
        return this;
    }

    public void validateToken() {
        assertTrue(token.length() > 0);
    }

    public void clickOk() {
        TestUtils.wait(5000);
        onView(withId(R.id.bOkay)).perform(click());
    }

    private String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView) view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    public String getToken() {
        return token;
    }
}
