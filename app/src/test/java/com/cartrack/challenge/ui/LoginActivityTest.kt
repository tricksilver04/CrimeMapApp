package com.cartrack.challenge.ui

import com.cartrack.challenge.R
import com.cartrack.challenge.dagger.hilt.AppModule
import dagger.hilt.android.testing.*
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.launchActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject


@HiltAndroidTest
@UninstallModules(AppModule::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class,sdk=[28])
class LoginActivityTest() {

    @get:Rule
    var rule = HiltAndroidRule(this)


    @Inject
    lateinit var db: AppDatabase


    @Before
    fun setup() {
        rule.inject()
    }

    @Test fun activityShouldNotBeNull() {
        launchActivity<LoginActivity>().onActivity { activity ->
            assertNotNull(activity)
        }
    }

    @Test fun activityFieldsShouldNotBeNull() {
        launchActivity<LoginActivity>().onActivity { activity ->
            assertNotNull(activity.findViewById(R.id.editTextUsername))
            assertNotNull(activity.findViewById(R.id.editTextTextPassword))
            assertNotNull(activity.findViewById(R.id.editTextCountries))
        }
    }

}



