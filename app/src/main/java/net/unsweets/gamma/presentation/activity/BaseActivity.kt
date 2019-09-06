package net.unsweets.gamma.presentation.activity

import android.os.Bundle
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import net.unsweets.gamma.GammaApplication
import net.unsweets.gamma.presentation.util.ThemeColorUtil

abstract class BaseActivity : DaggerAppCompatActivity() {
    private lateinit var darkThemeMode: String
    private var themeColorWhenCreated: ThemeColorUtil.ThemeColor? = null

    private enum class StateKey { ConfigurationChanges }

    override fun onCreate(savedInstanceState: Bundle?) {
        GammaApplication.getInstance(this).updateBaseTheme()
        darkThemeMode = ThemeColorUtil.currentDarkThemeMode(this)
        themeColorWhenCreated = ThemeColorUtil.applyTheme(this)
        super.onCreate(savedInstanceState)
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        val updateThemeColor = themeColorWhenCreated != ThemeColorUtil.getThemeColor(this)
        val currentDarkThemeMode = ThemeColorUtil.currentDarkThemeMode(this)
        if (updateThemeColor || darkThemeMode != currentDarkThemeMode) {
            recreate()
        }
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        themeColorWhenCreated = ThemeColorUtil.applyTheme(this)
    }

    override fun recreate() {
        super.recreate()
        (application as? GammaApplication)?.updateTheme()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
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
