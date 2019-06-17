package net.unsweets.gamma.presentation.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DaggerActivity
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.HasSupportFragmentInjector
import net.unsweets.gamma.GammaApplication
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {
    protected var isConfigurationChanges: Boolean = false

    private enum class StateKey { ConfigurationChanges }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isConfigurationChanges = savedInstanceState?.getBoolean(StateKey.ConfigurationChanges.name, false) ?: false
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(StateKey.ConfigurationChanges.name, isFinishing)
    }

    interface HaveDrawer {
        fun openDrawer()
        fun closeDrawer()
    }
}
