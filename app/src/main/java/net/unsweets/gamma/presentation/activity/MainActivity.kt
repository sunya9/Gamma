package net.unsweets.gamma.presentation.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.ViewAnimationUtils
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.ActivityMainBinding
import net.unsweets.gamma.databinding.NavigationDrawerHeaderBinding
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.usecases.GetAuthenticatedUserUseCase
import net.unsweets.gamma.presentation.fragment.ComposePostFragment
import net.unsweets.gamma.presentation.fragment.HomeFragment
import net.unsweets.gamma.presentation.fragment.PostItemFragment
import net.unsweets.gamma.presentation.fragment.ProfileFragment
import net.unsweets.gamma.presentation.util.DrawerContentFragment
import net.unsweets.gamma.presentation.util.FragmentHelper.addFragment
import net.unsweets.gamma.presentation.viewmodel.MainActivityViewModel
import javax.inject.Inject


class MainActivity : BaseActivity(), BaseActivity.HaveDrawer {

    private val drawerToggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(
            this, drawerLayout, bottomAppBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
    }
    @Inject
    lateinit var getAuthenticatedUseCase: GetAuthenticatedUserUseCase

    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(this, MainActivityViewModel.Factory(getAuthenticatedUseCase))
            .get(MainActivityViewModel::class.java)
    }
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
            is Event.OpenMyProfile -> openMyProfile(it.user)
            is Event.ComposePost -> openComposePostDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.event.observe(this, eventObserver)

        setupNavigationView()
        setupFragment(savedInstanceState == null)
        setupNavigation()
        bottomAppBar.inflateMenu(R.menu.main)
    }

    private fun setupFragment(firstStart: Boolean) {
        if (firstStart) {
            val homeFragment = HomeFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentPlaceholder, homeFragment)
                .commit()
        }
        supportFragmentManager.addOnBackStackChangedListener(::syncMenu)
    }


    private fun setupNavigationView() {
        val header = navigationView.getHeaderView(0)
        DataBindingUtil.bind<NavigationDrawerHeaderBinding>(header)
        DataBindingUtil.getBinding<NavigationDrawerHeaderBinding>(header)?.let { binding ->
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
        }
        navigationView.setNavigationItemSelectedListener(::onOptionsItemSelected)
    }

    private fun syncMenu() {
        uncheckMenuItem(navigationView.menu)
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragmentPlaceholder) as? DrawerContentFragment ?: return
        navigationView.menu.findItem(fragment.menuItemId).isChecked = true
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

    enum class DialogKey { ComposePost }

    private fun openComposePostDialog() {
        val pos = IntArray(2)
        fab.getLocationOnScreen(pos)
        val cx = pos[0] + fab.width / 2
        val cy = pos[1] + fab.height / 2
        val fragment = ComposePostFragment.newInstance(cx, cy)
        fragment.dialog?.setOnShowListener {
            val decorView = window.decorView
            val targetRadius = Math.hypot(decorView.width.toDouble(), decorView.height.toDouble()).toFloat()
            val startRadius = 0F
            val anim = ViewAnimationUtils.createCircularReveal(decorView, cx, cy, startRadius, targetRadius)
            anim.interpolator = AccelerateInterpolator()
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }
            })
            val duration = resources.getInteger(android.R.integer.config_mediumAnimTime)
            anim.duration = duration.toLong()
            anim.start()

        }
        fragment.show(supportFragmentManager, DialogKey.ComposePost.name)
    }

    private fun setupNavigation() {
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    private fun openMyProfile(user: User) {
        closeDrawer()

        Handler().postDelayed({
            val fragment = ProfileFragment.newInstance(user.id, user.content.avatarImage.link, user)
            addFragment(supportFragmentManager, fragment, user.id)
            uncheckMenuItem(navigationView.menu)
        }, 200)
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

    sealed class Event {
        object ComposePost : Event()
        data class OpenMyProfile(val user: User) : Event()
    }

}
