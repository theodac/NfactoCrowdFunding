package budasuyasa.android.simplecrud;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class RegisterInstrumentedTest {
    @Rule
    public val rule = ActivityTestRule(MainActivity::class.java)

    @Test
     void useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("budasuyasa.android.simplecrud", appContext.packageName);

        onView(withId(R.id.edit_login)).perform(replaceText("lionel"), closeSoftKeyboard());
        onView(withId(R.id.edit_password)).perform(replaceText("monmauvaismotdepasse"), closeSoftKeyboard());
        onView(withId(R.id.btn_go)).perform(click());
        onView(withId(R.id.txt_result)).check(matches(withText("KO")));
        onView(withId(R.id.edit_password)).perform(replaceText("bonpassword"), closeSoftKeyboard());
        onView(withId(R.id.btn_go)).perform(click());
        onView(withId(R.id.txt_result)).check(matches(withText("OK")));
    }
}
