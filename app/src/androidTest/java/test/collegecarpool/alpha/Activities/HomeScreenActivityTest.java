package test.collegecarpool.alpha.Activities;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.collegecarpool.alpha.LoginAndRegistrationActivities.SignupActivity;
import test.collegecarpool.alpha.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeScreenActivityTest {

    @Rule
    public ActivityTestRule<HomeScreenActivity> myActivityRule = new ActivityTestRule<>(HomeScreenActivity.class);

    @Test
    public void initPlanJourney() throws Exception {
        onView(withId(R.id.plan_journey)).perform(click());
    }
}