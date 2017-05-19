package test.collegecarpool.alpha.LoginAndRegistrationActivities;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.UserProfile;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignupActivityTest {

    private String firstName, secondName, email, password;

    @Rule
    public ActivityTestRule<SignupActivity> myActivityRule = new ActivityTestRule<>(SignupActivity.class);

    @Before
    public void initString(){
        email = "test@mail.dcu.ie";
        firstName = "Test";
        secondName = "Test";
        password = "test123";
    }

    @Test
    public void createUser() throws Exception {
        onView(withId(R.id.first_name))
                .perform(typeText(firstName), closeSoftKeyboard());

        onView(withId(R.id.second_name))
                .perform(typeText(secondName), closeSoftKeyboard());

        onView(withId(R.id.email))
                .perform(typeText(email), closeSoftKeyboard());

        onView(withId(R.id.password))
                .perform(typeText(password), closeSoftKeyboard());

        onView(withId(R.id.btn_Signup)).perform(click());
    }
}