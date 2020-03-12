package net.unsweets.gamma.testutil

import android.app.Activity
import android.app.Instrumentation
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.ComponentNameMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers
import kotlin.reflect.KClass

object IntentUtil {
  fun stubIntentResponse(activityClass: KClass<out Activity>) {
    val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
    Intents.intending(IntentMatchers.hasComponent(ComponentNameMatchers.hasClassName(activityClass.java.name)))
      .respondWith(result)
  }

  fun assertIntent(activityClass: KClass<out Activity>, operation: () -> Unit) {
    Intents.init()
    stubIntentResponse(activityClass)
    operation()
    assertIntent(activityClass)
    Intents.release()
  }

  fun assertIntent(activityClass: KClass<out Activity>) {
    Intents.intended(
      IntentMatchers.hasComponent(ComponentNameMatchers.hasClassName(activityClass.java.name))
    ) { _, intents ->
      intents.first()
    }
  }
}