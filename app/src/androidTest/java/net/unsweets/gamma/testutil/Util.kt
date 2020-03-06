package net.unsweets.gamma.testutil

import android.app.Activity
import androidx.test.espresso.core.internal.deps.guava.collect.Iterators
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import net.unsweets.gamma.GammaApplication

object Util {
  fun injectMock() {
    val gammaApp =
      InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GammaApplication
  }


  fun getActivityInstance(): Activity? {
    var currentActivity: Activity? = null
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
      val resumedActivities =
        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
      currentActivity = Iterators.getOnlyElement(resumedActivities.iterator())
    }
    return currentActivity
  }
}