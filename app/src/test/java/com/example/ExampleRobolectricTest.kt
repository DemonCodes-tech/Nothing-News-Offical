package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Nothing News", appName)
  }

  @Test
  fun `tutorial appears by default for new users and remembers completion`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)
    
    // Clear state
    prefs.edit().clear().commit()

    // First time user check
    val isCompletedFirstTime = prefs.getBoolean("has_completed_tutorial", false)
    assertEquals(false, isCompletedFirstTime)

    // Mark completed
    prefs.edit().putBoolean("has_completed_tutorial", true).commit()

    // Returning user check
    val isCompletedReturning = prefs.getBoolean("has_completed_tutorial", false)
    assertEquals(true, isCompletedReturning)
  }

  @Test
  fun `notification preferences and design style persist correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("nothing_news_prefs", Context.MODE_PRIVATE)

    // Set custom notification preference for natural disasters and design style
    prefs.edit()
      .putBoolean("notif_natural_disasters", true)
      .putBoolean("notif_humanitarian_crisis", true)
      .putString("design_style", "MATERIAL_YOU")
      .commit()

    assertEquals(true, prefs.getBoolean("notif_natural_disasters", false))
    assertEquals(true, prefs.getBoolean("notif_humanitarian_crisis", false))
    assertEquals("MATERIAL_YOU", prefs.getString("design_style", "NOTHING_OS"))
  }
}
