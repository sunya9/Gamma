package net.unsweets.gamma.presentation.activity

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.io.SetupTokenOutputData
import net.unsweets.gamma.domain.usecases.SetupTokenUseCase
import net.unsweets.gamma.testutil.IntentUtil
import net.unsweets.gamma.testutil.OverrideModules
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class EntryActivityTest {
  @get:Rule
  val activityTestRule = ActivityTestRule(EntryActivity::class.java, true, false)

  private fun getFakeSetupTokenUseCase(result: Boolean) =
    Mockito.mock(SetupTokenUseCase::class.java).also {
      runBlocking { Mockito.`when`(it.run(Unit)).thenReturn(SetupTokenOutputData(result)) }
    }

  @Test
  fun launchMainActivityWhenSomeAccountsExists() {
    OverrideModules {
      it.fakeUseCaseModule.setupTokenUseCase = getFakeSetupTokenUseCase(true)
    }
    IntentUtil.assertIntent(MainActivity::class) {
      activityTestRule.launchActivity(Intent())
    }
    assertActivityIsFinished()
  }

  @Test
  fun launchLoginActivityWhenHasAccountsDoesNotExists() {
    OverrideModules {
      it.fakeUseCaseModule.setupTokenUseCase = getFakeSetupTokenUseCase(false)
    }
    IntentUtil.assertIntent(LoginActivity::class) {
      activityTestRule.launchActivity(Intent())
    }
    assertActivityIsFinished()
  }

  private fun assertActivityIsFinished() {
    Assert.assertThat(
      activityTestRule.activity.isFinishing,
      Matchers.`is`(true)
    )
  }
}