package com.example.wavezcellular;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;


import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.wavezcellular.activities.ReportActivity;
import com.example.wavezcellular.activities.ShowActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ReportActivityTest {

    public final String DEFAULT_BEACH = "Bugrashov beach Tel Aviv";
    private Intent intent;
    private ActivityScenario<ReportActivity> activityScenario;
    @Before
    public void setup() {
        // Initialize Intents
        Intents.init();
        // Create a new intent
        intent = new Intent(ApplicationProvider.getApplicationContext(), ReportActivity.class);

        // Create a bundle and put the extra data
        Bundle bundle = new Bundle();
        bundle.putString("BEACH_NAME", DEFAULT_BEACH);

        intent.putExtras(bundle);
    }

    /**
     * Test that the activity starts without any issues
     */
    @Test
    public void testReportActivityStart() {
        activityScenario = ActivityScenario.launch(intent);
        // Add assertions to verify the initial state of the activity if needed
        // For example, you can check if certain views are displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.report_EditTXT_comment))
                .check(matches(isDisplayed()));
    }

    /**
     * Test the submission of a valid report from a user
     */
    @Test
    public void testUserReportSubmit() {
        // Launch the activity
        activityScenario = ActivityScenario.launch(intent);

        // Sign in with a test user
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("xowafec498@duscore.com","123456");
        try {
            Thread.sleep(2000); // Delay of 2 seconds (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Perform actions on the report activity
        activityScenario.onActivity(reportActivity -> {
            // Call the testAction method on the main application thread if needed
            //reportActivity.runOnUiThread(reportActivity::testAction);
        });

        // Wait for the report to be submitted
        try {
            Thread.sleep(2000); // Delay of 2 seconds (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Enter a valid report comment
        String comment = "TestReportSubmit";
        Espresso.onView(ViewMatchers.withId(R.id.report_EditTXT_comment))
                .perform(ViewActions.typeText(comment));

        // Simulate pressing the "Back" button
        Espresso.onView(ViewMatchers.isRoot())
                .perform(ViewActions.pressBack());
        // Wait for the report to be submitted
        try {
            Thread.sleep(2000); // Delay of 2 seconds (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Submit the report
        Espresso.onView(ViewMatchers.withId(R.id.report_BTN_submit))
                .perform(ViewActions.click());

        // Wait for the report to be submitted
        try {
            Thread.sleep(2000); // Delay of 2 seconds (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the report was submitted successfully
        Espresso.onView(ViewMatchers.withId(R.id.show_BTN_reports))
                .perform(ViewActions.click());

        // Scroll and search for the comment
        boolean commentFound = false;
        while (!commentFound) {
            try {
                Thread.sleep(1000); // Delay of 1 second (adjust as needed)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Scroll the view
            Espresso.onView(ViewMatchers.withId(R.id.userReports_RecyclerView_reports))
                    .perform(ViewActions.swipeUp());

            // Check if the comment is displayed
            try {
                Espresso.onView(ViewMatchers.withText(comment))
                        .check(matches(isDisplayed()));
                commentFound = true;
            } catch (Exception ignored) {
                // Comment not found, continue scrolling
            }
        }

        // Remove the user report
        removeUserReportByComment(comment);

        // Sign out
        FirebaseAuth.getInstance().signOut();

    }



    /**
     * Test navigating back from reportActivity
     */
    @Test
    public void testNavigatingBack() {
        FirebaseAuth.getInstance().signOut();

        activityScenario = ActivityScenario.launch(intent);

        activityScenario.onActivity(reportActivity -> {
            // Call the clearUser method on the main application thread if needed
            reportActivity.runOnUiThread(reportActivity::testAction);
        });

        // Perform the action that triggers the navigation back
        Espresso.onView(ViewMatchers.withId(R.id.report_BTN_back))
                .perform(ViewActions.click());

        // Introduce a delay to allow the application to process the navigation
        try {
            Thread.sleep(1000); // Delay of 1 second (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that a view in ShowActivity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.show_RB_review))
                .check(matches(isDisplayed()));

    }

    /**
     * Test navigating to profile from reportActivity
     */
    @Test
    public void testNavigatingProfile() {
        activityScenario = ActivityScenario.launch(intent);
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("xowafec498@duscore.com","123456");

        activityScenario.onActivity(reportActivity -> {
            // Call the clearUser method on the main application thread if needed
            reportActivity.runOnUiThread(reportActivity::testAction);
        });

        // Perform the action that triggers the navigation back
        Espresso.onView(ViewMatchers.withId(R.id.report_IMG_profile))
                .perform(ViewActions.click());

        // Introduce a delay to allow the application to process the navigation
        try {
            Thread.sleep(1000); // Delay of 1 second (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that a view in ShowActivity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.profile_IMG_picture))
                .check(matches(isDisplayed()));

    }

    public void removeUserReportByComment(String commentValue) {
        DatabaseReference reportsRef;
        reportsRef = FirebaseDatabase.getInstance().getReference().child("Beaches").child("Bugrashov beach Tel Aviv").child("Reports");
        Query query = reportsRef.orderByChild("comment").equalTo(commentValue);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    userSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the cancellation or throw an exception
            }
        });
    }

    @After
    public void closeTest() {
        activityScenario.close();
        Intents.release();
    }

}



