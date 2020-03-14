package net.unsweets.gamma.presentation.activity

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.unsweets.gamma.domain.model.io.GetCurrentAccountOutputData
import net.unsweets.gamma.domain.usecases.GetCurrentAccountUseCase
import net.unsweets.gamma.sample.Accounts
import net.unsweets.gamma.testutil.OverrideModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
  @get:Rule
  val intentsTestRule = IntentsTestRule(SettingsActivity::class.java, true, false)

  @Before
  fun setup() {
    OverrideModules {
      it.fakeUseCaseModule.getCurrentAccountUseCase =
        Mockito.mock(GetCurrentAccountUseCase::class.java).also { useCase ->
          Mockito.`when`(useCase.run(Unit))
            .thenReturn(GetCurrentAccountOutputData(Accounts.account))
        }
    }
  }

  private fun verifyText(text: String) {
    Espresso.onView(ViewMatchers.withId(androidx.preference.R.id.recycler_view))
      .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(text))))
  }

  @Test
  fun verifyListItemsOnRoot() {
    intentsTestRule.launchActivity(Intent())
    verifyText("@screenName")
    verifyText("Account")
    verifyText("General")
    verifyText("Behavior & Appearances")
    verifyText("Stream")
    verifyText("License")
    verifyText("Gamma version")
  }
}