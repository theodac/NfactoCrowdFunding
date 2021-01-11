package budasuyasa.android.simplecrud;

import android.content.Intent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddProjectInstrumentedTest {
    @Before
    public void init() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void fullTest() {
        goToRegisterPage();
        registerUser();
    }

    @Test
    public void goToRegisterPage() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Login")).perform(click());
    }

    @Test
    public void registerUser() {
        Intent i = new Intent();

        onView(withId(R.id.username)).perform(replaceText("test2@test.fr"), closeSoftKeyboard());
        onView(withId(R.id.pseudo)).perform(replaceText("pseudo2test"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(replaceText("password2test"), closeSoftKeyboard());
        onView(withId(R.id.birthday)).perform(replaceText("01/01/0001"), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
    }

}