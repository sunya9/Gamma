package net.unsweets.gamma.presentation.activity

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.io.GetAccountListOutputData
import net.unsweets.gamma.domain.model.io.SetupTokenOutputData
import net.unsweets.gamma.domain.usecases.GetAccountListUseCase
import net.unsweets.gamma.domain.usecases.SetupTokenUseCase
import net.unsweets.gamma.testutil.OverrideModules
import net.unsweets.gamma.testutil.Util
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class EntryActivityTest {
  @get:Rule
  val intentsTestRule = ActivityTestRule(EntryActivity::class.java, false, false)
  private val intent = Intent().also {
    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }

  private fun startActivity(): EntryActivity {
    return intentsTestRule.launchActivity(intent)
  }


  @Test
  fun launchMainActivityWhenSomeAccountsExists() {
    OverrideModules {
      it.fakeUseCaseModule.setupTokenUseCase = Mockito.mock(SetupTokenUseCase::class.java).also {
        runBlocking { Mockito.`when`(it.run(Unit)).thenReturn(SetupTokenOutputData(true)) }
      }
      it.fakeUseCaseModule.getAccountListUseCase =
        Mockito.mock(GetAccountListUseCase::class.java).also {
          runBlocking { Mockito.`when`(it.run(Unit)) }.thenReturn(GetAccountListOutputData(emptyList()))
        }

    }
    startActivity()
    Assert.assertThat(
      Util.getActivityInstance()!!.javaClass.name,
      `is`(MainActivity::class.java.name)
    )
  }

  @Test
  fun launchLoginActivityWhenHasAccountsDoesNotExists() {
    OverrideModules {
      it.fakeUseCaseModule.setupTokenUseCase = Mockito.mock(SetupTokenUseCase::class.java).also {
        runBlocking { Mockito.`when`(it.run(Unit)).thenReturn(SetupTokenOutputData(false)) }
      }
    }
    startActivity()
    Assert.assertThat(
      Util.getActivityInstance()!!.javaClass.name,
      `is`(LoginActivity::class.java.name)
    )
  }
}