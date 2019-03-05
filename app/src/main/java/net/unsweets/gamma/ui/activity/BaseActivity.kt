package net.unsweets.gamma.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import net.unsweets.gamma.GammaApplication

abstract class BaseActivity : AppCompatActivity() {
    protected var isConfigurationChanges: Boolean = false

    private enum class StateKey { ConfigurationChanges }

    private val gammaApplication
        get() = this.application as GammaApplication
    val prefManager
        get() = gammaApplication.prefManager


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
