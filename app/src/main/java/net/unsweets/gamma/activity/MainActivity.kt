package net.unsweets.gamma.activity

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.ActivityMainBinding
import net.unsweets.gamma.databinding.NavigationDrawerHeaderBinding
import net.unsweets.gamma.fragment.HomeFragment
import net.unsweets.gamma.fragment.ProfileFragment
import net.unsweets.gamma.model.User
import net.unsweets.gamma.util.FragmentHelper.addFragment
import net.unsweets.gamma.util.PrefManager
import net.unsweets.gamma.util.Store
import net.unsweets.gamma.util.then


class MainActivity : BaseActivity() {

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainActivity.MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        viewModel.event.observe(this, Observer {
            when (it) {
                Event.COMPOSE_POST -> showComposePostActivity()
                Event.OPEN_MY_PROFILE -> openMyProfile()
                else -> Unit
            }
        })

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.bottomAppBar.setNavigationOnClickListener { showBottomMenu() }
        binding.navigationView.also { nav ->
            val header = nav.getHeaderView(0)
            DataBindingUtil.bind<NavigationDrawerHeaderBinding>(header)
            DataBindingUtil.getBinding<NavigationDrawerHeaderBinding>(header)?.let { binding ->
                binding.setLifecycleOwner(this)
                binding.viewModel = viewModel
            }
            nav.setNavigationItemSelectedListener { onOptionsItemSelected(it) }
        }

        val homeFragment = HomeFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPlaceholder, homeFragment)
            .commit()

        mDrawerToggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.bottomAppBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(mDrawerToggle)
        getCurrentUserData()
    }

    private fun openMyProfile() {
        val id = PrefManager(this).getDefaultAccountID() ?: return
        val fragment = ProfileFragment.newInstance(id)
        addFragment(supportFragmentManager, fragment, id)
        closeDrawer()
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun getCurrentUserData() {
        pnut.token().then { viewModel.loginUser.postValue(it.data.user) }
    }

    private fun showBottomMenu() {
    }

    private fun showComposePostActivity() {
        val fab = binding.fab
        val pos = IntArray(2)
        fab.getLocationOnScreen(pos)
        val cx = pos[0] + fab.width / 2
        val cy = pos[1] + fab.height / 2
        val intent = ComposePostActivity.newIntent(this, cx, cy)
        val options = ActivityOptions.makeSceneTransitionAnimation(this, fab, "transition")
        startActivity(intent, options.toBundle())
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle.onConfigurationChanged(newConfig)
    }

    private fun goToHome() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun goToExplore(pos: Int) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (mDrawerToggle.onOptionsItemSelected(item))
            true
        else
            item?.let {
                when (it.itemId) {
                    R.id.home -> goToHome()
                    R.id.conversations -> goToExplore(0)
                    R.id.missedConversations -> goToExplore(1)
                    R.id.newcomers -> goToExplore(2)
                    R.id.photos -> goToExplore(2)
                    R.id.trending -> goToExplore(3)
                    R.id.global -> goToExplore(5)
                    R.id.file -> goToFiles()
                    R.id.settings -> goToSettings()
                }
                closeDrawer()
                return true
            } ?: super.onOptionsItemSelected(item)
    }

    private fun goToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun goToFiles() {
        val intent = Intent(this, FilesActivity::class.java)
        startActivity(intent)
    }

    enum class Event {
        COMPOSE_POST,
        OPEN_MY_PROFILE
    }

    class MainActivityViewModel : Store<Event>() {
        val loginUser = MutableLiveData<User>()
        fun composePost() {
            sendEvent(Event.COMPOSE_POST)
        }

        fun openMyProfile() {
            sendEvent(Event.OPEN_MY_PROFILE)
        }
    }

}
