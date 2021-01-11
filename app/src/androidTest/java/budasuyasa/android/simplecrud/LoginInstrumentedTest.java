package budasuyasa.android.simplecrud;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.replaceText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLogin() {
        onView(withId(R.id.username2)).perform(replaceText("test@test.fr"), closeSoftKeyboard());
        onView(withId(R.id.password2)).perform(replaceText("passwordtest"), closeSoftKeyboard());
        onView(withId(R.id.login2)).perform(click());
        onView(withId(R.id.txt_result2)).check(matches(withText("OK")));
    }
}