package budasuyasa.android.simplecrud;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.isA;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class LoginInstrumentedTest {
    @Before
    public void init() {
       ActivityScenario.launch(LoginActivity.class);
    }

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLogin() {
        onView(withId(R.id.username2)).perform(replaceText("test@test.fr"), closeSoftKeyboard());
        onView(withId(R.id.password2)).perform(replaceText("passwordtest"), closeSoftKeyboard());
        onView(withId(R.id.login2)).perform(click());
    }
}