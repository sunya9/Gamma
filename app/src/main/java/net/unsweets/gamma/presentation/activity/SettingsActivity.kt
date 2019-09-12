package net.unsweets.gamma.presentation.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.ListPreference
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_settings.*
import net.unsweets.gamma.BuildConfig
import net.unsweets.gamma.GammaApplication
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.usecases.GetCurrentAccountUseCase
import net.unsweets.gamma.domain.usecases.LogoutUseCase
import net.unsweets.gamma.presentation.fragment.ChoosePrimaryColorDialogFragment
import net.unsweets.gamma.presentation.util.ColorSummaryProvider
import net.unsweets.gamma.presentation.util.ThemeColorUtil
import net.unsweets.gamma.presentation.view.ThemeColorPreference
import javax.inject.Inject


class SettingsActivity : BaseActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference
    ): Boolean {
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_right
            )
            .replace(R.id.content, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

    @Inject
    lateinit var getCurrentAccountUseCase: GetCurrentAccountUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        val currentAccount = getCurrentAccountUseCase.run(Unit).account ?: return

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, SettingsFragment.newInstance(currentAccount.screenName))
                .commit()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // settings list fragment
    class SettingsFragment : BasePreferenceFragment() {
        val username by lazy {
            "@${arguments?.getString(BundleKey.Username.name, "")}"
        }
        override val rootKey: Int = R.string.pref_settings_root_key

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_headers, rootKey)
            findPreference<Preference>(getString(R.string.pref_account_header_title_key))?.let {
                it.title = username
            }
            findPreference<Preference>(getString(R.string.pref_license_key))?.let {
                it.intent = Intent(context, OssLicensesMenuActivity::class.java)
            }
            findPreference<Preference>((getString(R.string.pref_version_key)))?.let {
                it.summary = BuildConfig.VERSION_NAME
                it.intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                }
            }
        }

        private enum class BundleKey { Username }
        companion object {
            fun newInstance(username: String) = SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(BundleKey.Username.name, username)
                }
            }
        }
    }

    abstract class BasePreferenceFragment : PreferenceFragmentCompat(), HasSupportFragmentInjector {
        @Inject
        lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>


        override fun supportFragmentInjector(): AndroidInjector<Fragment> = childFragmentInjector

        override fun onCreate(savedInstanceState: Bundle?) {
            AndroidSupportInjection.inject(this)
            super.onCreate(savedInstanceState)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        fun findPreference(@StringRes id: Int): Preference? = findPreference(getString(id))

        override fun onResume() {
            super.onResume()
            activity?.title = findPreference(rootKey)?.title
        }

        abstract val rootKey: Int
    }

    class BehaviorAppearancePreferenceFragment : BasePreferenceFragment(),
        ChoosePrimaryColorDialogFragment.Callback {
        override fun updateColor(themeColor: ThemeColorUtil.ThemeColor?) {
            themeColorPreference?.themeColor = themeColor
            recreateActivity()
        }

        override val rootKey: Int = R.string.pref_behavior_appearance_key

        override fun setAsDefault() {
            themeColorPreference?.themeColor = null
            recreateActivity()
        }

        private fun recreateActivity() {
            Handler().post {
                activity?.recreate()
            }
        }

        private fun showDialog() {
            val fragment =
                ChoosePrimaryColorDialogFragment.newInstance(themeColorPreference?.themeColor)
            fragment.show(childFragmentManager, DialogKey.ChoosePrimaryColorDialog.name)
        }

        private enum class DialogKey { ChoosePrimaryColorDialog }

        private val themeColorPreference by lazy {
            findPreference(R.string.pref_change_primary_color_key) as? ThemeColorPreference
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(
                R.xml.pref_behavior_appearance,
                getString(R.string.pref_behavior_appearance_key)
            )
            themeColorPreference?.let {
                context?.let { ctx -> it.summaryProvider = ColorSummaryProvider(ctx) }
                it.setOnPreferenceClickListener {
                    showDialog()
                    true
                }
            }
            (findPreference(R.string.pref_dark_theme_key) as? DropDownPreference)?.let {
                it.setOnPreferenceChangeListener { preference: Preference, _: Any ->
                    if (preference !is DropDownPreference) return@setOnPreferenceChangeListener false
                    activity?.let { activity ->
                        GammaApplication.getInstance(activity).updateBaseTheme()
                    }
                    Handler().post {
                        activity?.recreate()
                    }
                    true
                }
            }
        }
    }

    class StreamPreferenceFragment : BasePreferenceFragment() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(
                R.xml.pref_stream,
                getString(R.string.pref_stream_key)
            )
        }

        override val rootKey: Int = R.string.pref_stream_key
    }

    class AccountPreferenceFragment : BasePreferenceFragment() {
        override val rootKey: Int = R.string.pref_account_key
        @Inject
        lateinit var logoutUseCase: LogoutUseCase

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            findPreference(R.string.pref_logout_account_key)?.apply {
                setOnPreferenceClickListener { logout() }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_account, rootKey)
        }

        private fun logout(): Boolean {
            val anotherAccountId = logoutUseCase.run(Unit).anotherAccountId
            activity?.finish()
            val intentClass = if (anotherAccountId != null) {
                activity?.overridePendingTransition(R.anim.scale_up, R.anim.scale_down)
                MainActivity::class
            } else {
                LoginActivity::class
            }
            val newIntent = Intent(activity, intentClass.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(newIntent)
            return true
        }
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener =
            Preference.OnPreferenceChangeListener { preference, value ->
                val stringValue = value.toString()

                if (preference is ListPreference) {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    val index = preference.findIndexOfValue(stringValue)

                    // Set the summary to reflect the new value.
                    preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null
                    )

//            } else if (preference is RingtonePreference) {
//                // For ringtone preferences, look up the correct display value
//                // using RingtoneManager.
//                if (TextUtils.isEmpty(stringValue)) {
//                    // Empty values correspond to 'silent' (no ringtone).
//                    preference.setSummary(R.string.pref_ringtone_silent)
//
//                } else {
//                    val ringtone = RingtoneManager.getRingtone(
//                        preference.getContext(), Uri.parse(stringValue)
//                    )
//
//                    if (ringtone == null) {
//                        // Clear the summary if there was a lookup showAsError.
//                        preference.setSummary(null)
//                    } else {
//                        // Set the summary to reflect the new ringtone display
//                        // name.
//                        val name = ringtone.getTitle(preference.getContext())
//                        preference.setSummary(name)
//                    }
//                }

                } else {
                    // For all other preferences, set the summary to the value's
                    // simple string representation.
                    preference.summary = stringValue
                }
                true
            }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_NORMAL &&
                    context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            )
        }
    }
}
