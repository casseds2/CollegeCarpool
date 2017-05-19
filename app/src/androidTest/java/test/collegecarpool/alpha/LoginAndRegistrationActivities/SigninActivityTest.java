package test.collegecarpool.alpha.LoginAndRegistrationActivities;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.collegecarpool.alpha.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SigninActivityTest {

    @Rule
    public ActivityTestRule<SigninActivity> myActivityRule = new ActivityTestRule<>(SigninActivity.class);

    @Test
    public void signin() throws Exception {

        String email = "stephen.cassedy2@mail.dcu.ie";
        String password = "hurler1";

        onView(withId(R.id.email))
                .perform(clearText())
                .perform(typeText(email), closeSoftKeyboard());

        onView(withId(R.id.password))
                .perform(clearText())
                .perform(typeText(password), closeSoftKeyboard());

        onView(withId(R.id.btn_login)).perform(click());
    }
}