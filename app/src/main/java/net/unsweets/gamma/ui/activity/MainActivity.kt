package net.unsweets.gamma.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.NavigationDrawerHeaderBinding
import net.unsweets.gamma.ui.fragment.HomeFragment
import net.unsweets.gamma.ui.fragment.PostItemFragment
import net.unsweets.gamma.ui.fragment.ProfileFragment
import net.unsweets.gamma.ui.util.DrawerContentFragment
import net.unsweets.gamma.ui.util.FragmentHelper.addFragment
import net.unsweets.gamma.ui.viewmodel.MainActivityViewModel


class MainActivity : BaseActivity(), BaseActivity.HaveDrawer {
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var viewModel: MainActivityViewModel
    private val fragmentMap = hashMapOf(
        R.id.conversations to PostItemFragment.getConversationInstance(),
        R.id.missedConversations to PostItemFragment.getMissedConversationInstance(),
        R.id.newcomers to PostItemFragment.getNewcomersInstance(),
        R.id.photos to PostItemFragment.getPhotoInstance(),
        R.id.trending to PostItemFragment.getTrendingInstance(),
        R.id.global to PostItemFragment.getGlobalInstance()
    )

    private val eventObserver = Observer<Event> {
        when (it) {
            Event.OpenMyProfile -> openMyProfile()
            else -> Unit
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        if (!isConfigurationChanges) {
            viewModel.getLoginUser()
            viewModel.event.observe(this, eventObserver)
        }


        navigationView.also { nav ->
            val header = nav.getHeaderView(0)
            DataBindingUtil.bind<NavigationDrawerHeaderBinding>(header)
            DataBindingUtil.getBinding<NavigationDrawerHeaderBinding>(header)?.let { binding ->
                binding.lifecycleOwner = this
                binding.viewModel = viewModel
            }
            nav.setNavigationItemSelectedListener { onOptionsItemSelected(it) }
        }

        if (savedInstanceState == null) {
            val homeFragment = HomeFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentPlaceholder, homeFragment)
                .commit()
        }
        supportFragmentManager.addOnBackStackChangedListener { syncMenu() }
        setupNavigation()
    }

    private fun syncMenu() {
        uncheckMenuItem(navigationView.menu)
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragmentPlaceholder) as? DrawerContentFragment ?: return
        navigationView.menu.findItem(fragment.getMenuItemId()).isChecked = true
    }

    private fun uncheckMenuItem(menu: Menu) {
        val size = menu.size() - 1
        for (i in 0..size) {
            val item = menu.getItem(i)
            if (item.hasSubMenu()) {
                uncheckMenuItem(item.subMenu)
            } else {
                item.isChecked = false
            }
        }
    }

    private fun showComposePostActivity() {
        val pos = IntArray(2)
        fab.getLocationOnScreen(pos)
        val cx = pos[0] + fab.width / 2
        val cy = pos[1] + fab.height / 2
        val intent = ComposePostActivity.newIntent(this, cx, cy)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, fab, getString(R.string.fab_transition))
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    private fun setupNavigation() {
        drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, bottomAppBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        fab.setOnClickListener { showComposePostActivity() }
    }

    private fun openMyProfile() {
        closeDrawer()
        val id = prefManager.getDefaultAccountID() ?: return
        val user = viewModel.user.value ?: return
        val fragment = ProfileFragment.newInstance(id, user.content.avatarImage.link, user)
        addFragment(supportFragmentManager, fragment, id)
        uncheckMenuItem(navigationView.menu)
    }

    override fun closeDrawer() = drawerLayout.closeDrawer(GravityCompat.START)

    override fun openDrawer() = drawerLayout.openDrawer(GravityCompat.START)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerVisible(GravityCompat.START) -> closeDrawer()
            else -> super.onBackPressed()
        }
    }

    private fun goToHome() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (drawerToggle.onOptionsItemSelected(item))
            true
        else
            item?.let {
                when (it.itemId) {
                    R.id.home -> goToHome()
                    R.id.conversations,
                    R.id.missedConversations,
                    R.id.newcomers,
                    R.id.photos,
                    R.id.trending,
                    R.id.global -> showExploreStream(it.itemId)
                    R.id.file -> goToFiles()
                    R.id.settings -> goToSettings()
                }
                closeDrawer()
                return true
            } ?: super.onOptionsItemSelected(item)
    }

    private fun showExploreStream(menuId: Int) {
        val fragment = fragmentMap[menuId] ?: return
        addFragment(supportFragmentManager, fragment, menuId.toString())
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
        ComposePost,
        OpenMyProfile
    }

}
