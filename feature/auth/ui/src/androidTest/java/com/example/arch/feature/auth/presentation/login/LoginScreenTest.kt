package com.example.arch.feature.auth.presentation.login

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.arch.core.ui.theme.AppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginScreenTest {

    @get:Rule(order = 0) val hiltRule    = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createComposeRule()

    @Before fun setUp() {
        hiltRule.inject()
        composeRule.setContent {
            AppTheme {
                LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {})
            }
        }
    }

    @Test
    fun screen_shows_signIn_button() {
        composeRule.onNodeWithText("Sign In").assertExists()
    }

    @Test
    fun signIn_button_disabled_when_fields_are_empty() {
        composeRule.onNodeWithText("Sign In").assertIsNotEnabled()
    }

    @Test
    fun signIn_button_enabled_when_fields_filled() {
        composeRule.onNodeWithText("Email").performTextInput("user@test.com")
        composeRule.onNodeWithText("Password").performTextInput("password1")
        composeRule.onNodeWithText("Sign In").assertIsEnabled()
    }

    @Test
    fun register_link_is_visible() {
        composeRule.onNodeWithText("Don't have an account? Register").assertExists()
    }

    @Test
    fun clicking_register_link_triggers_callback() {
        var navigated = false
        composeRule.setContent {
            AppTheme {
                LoginScreen(onLoginSuccess = {}, onNavigateToRegister = { navigated = true })
            }
        }
        composeRule.onNodeWithText("Don't have an account? Register").performClick()
        composeRule.waitForIdle()
        assert(navigated)
    }
}
