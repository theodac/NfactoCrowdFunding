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
public class RegisterInstrumentedTest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> registerActivityActivityScenarioRule =
            new ActivityScenarioRule<>(RegisterActivity.class);

    @Test
    public void testRegister() {
        onView(withId(R.id.username)).perform(replaceText("test@test.fr"), closeSoftKeyboard());
        onView(withId(R.id.pseudo)).perform(replaceText("pseudotest"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(replaceText("passwordtest"), closeSoftKeyboard());
        onView(withId(R.id.birthday)).perform(replaceText("01/01/0001"), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.txt_result)).check(matches(withText("OK")));
    }
}