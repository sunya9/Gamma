package net.unsweets.gamma.presentation.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.account_list.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.broadcast.PostReceiver
import net.unsweets.gamma.databinding.ActivityMainBinding
import net.unsweets.gamma.databinding.NavigationDrawerHeaderBinding
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.domain.model.io.UpdateDefaultAccountInputData
import net.unsweets.gamma.domain.usecases.GetAccountListUseCase
import net.unsweets.gamma.domain.usecases.GetAuthenticatedUserUseCase
import net.unsweets.gamma.domain.usecases.GetCurrentAccountUseCase
import net.unsweets.gamma.domain.usecases.UpdateDefaultAccountUseCase
import net.unsweets.gamma.presentation.adapter.AccountListAdapter
import net.unsweets.gamma.presentation.fragment.*
import net.unsweets.gamma.presentation.util.FragmentHelper.addFragment
import net.unsweets.gamma.presentation.util.LoginUtil
import net.unsweets.gamma.presentation.util.Util
import net.unsweets.gamma.presentation.viewmodel.MainActivityViewModel
import net.unsweets.gamma.service.PostService
import javax.inject.Inject

class MainActivity : BaseActivity(), BaseActivity.HaveDrawer, PostReceiver.Callback, AccountListAdapter.Listener {
    override fun onDeletePostReceive(post: Post) {
        showActionResultSnackbar(post, Action.Delete)
    }

    override fun onAccountClick(account: Account) {
        if (currentAccount == account) return closeDrawer()
        updateDefaultAccountUseCase.run(UpdateDefaultAccountInputData(account.id))
        val restartIntent = intent
        finish()
        overridePendingTransition(R.anim.scale_up, R.anim.scale_down)
        startActivity(restartIntent)
    }

    override fun onAddAccount() {
        closeDrawer()
        val newIntent = LoginUtil.getLoginIntent(this)
        startActivity(newIntent)
    }

    override fun onRepostReceive(post: Post) {
        showActionResultSnackbar(post, Action.Repost)
    }

    override fun onStarReceive(post: Post) {
        showActionResultSnackbar(post, Action.Star)
    }

    private enum class Action { Star, Repost, Delete }

    @Inject
    lateinit var getAccountListUseCase: GetAccountListUseCase
    @Inject
    lateinit var updateDefaultAccountUseCase: UpdateDefaultAccountUseCase
    @Inject
    lateinit var getCurrentAccountUseCase: GetCurrentAccountUseCase

    private val currentAccount: Account? by lazy {
        getCurrentAccountUseCase.run(Unit).account
    }
    private val accounts
        get() = getAccountListUseCase.run(Unit).accounts.filterNot { it == currentAccount }

    private fun showActionResultSnackbar(post: Post, action: Action) {
        val actionNameRes = when (action) {
            Action.Star -> if (post.mainPost.youBookmarked == true) R.string.stars else R.string.unstar
            Action.Repost -> if (post.mainPost.youReposted == true) R.string.repost else R.string.delete_repost
            Action.Delete -> R.string.delete
        }
        val actionName = getString(actionNameRes)
        val username = post.mainPost.user?.username ?: return
        val content = post.content?.text ?: return
        val message =
            getString(R.string.action_result_snackbar_template, actionName, username, content)
        showSnackBar(message)
    }

    override fun onPostReceive(post: Post) {
        val text = post.content?.text ?: return
        showSnackBar(getString(R.string.posted, text))
    }

    private fun showSnackBar(text: String) {
        val view = findViewById<View>(android.R.id.content) ?: return
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).apply {
            setAnchorView(R.id.fab)
        }.show()
    }

    private lateinit var binding: ActivityMainBinding

    private val accountListView by lazy {
        val view = layoutInflater.inflate(R.layout.account_list, binding.navigationView, false)
        view.accountList.also { accountList ->
            accountList.adapter = AccountListAdapter(accounts, this)
        }
    }


    private val showAccountMenuObserver = Observer<Boolean> {
        val menu = binding.navigationView.menu
        val indicatorView: ImageView? = binding.navigationView.switchAccountIndicatorImageView
        indicatorView?.let { imageView ->
            val res =
                if (it) R.drawable.ic_arrow_drop_down_to_up else R.drawable.ic_arrow_drop_up_to_down
            val avd = AnimatedVectorDrawableCompat.create(this, res) ?: return@let
            imageView.setImageDrawable(avd)
            avd.start()
        }
        menu.clear()
        when (it) {
            true -> {
                binding.navigationView.addHeaderView(accountListView)
            }
            false -> {
                binding.navigationView.removeHeaderView(accountListView)
                menuInflater.inflate(R.menu.navigation_drawer, menu)
                syncMenu()
            }
        }
    }
    private val drawerToggle: ActionBarDrawerToggle by lazy {
        object : ActionBarDrawerToggle(
            this, drawerLayout, bottomAppBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                viewModel.showAccountMenu.value = false
            }
        }
    }
    @Inject
    lateinit var getAuthenticatedUseCase: GetAuthenticatedUserUseCase

    private val receiverManager by lazy {
        LocalBroadcastManager.getInstance(applicationContext)
    }

    private val postReceiver by lazy {
        PostReceiver(this)
    }

    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(
            this,
            MainActivityViewModel.Factory(getAuthenticatedUseCase)
        )[MainActivityViewModel::class.java]
    }
    private val fragmentMap: HashMap<Int, () -> PostItemFragment> = hashMapOf(
        R.id.conversations to { PostItemFragment.getConversationInstance() },
        R.id.missedConversations to { PostItemFragment.getMissedConversationInstance() },
        R.id.newcomers to { PostItemFragment.getNewcomersInstance() },
        R.id.photos to { PostItemFragment.getPhotoInstance() },
        R.id.trending to { PostItemFragment.getTrendingInstance() },
        R.id.global to { PostItemFragment.getGlobalInstance() }
    )
    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.OpenMyProfile -> openMyProfile(it.user)
            is Event.ComposePost -> openComposePostDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.event.observe(this, eventObserver)
        viewModel.showAccountMenu.observe(this, showAccountMenuObserver)

        setupNavigationView()
        setupFragment(savedInstanceState == null)
        setupNavigation()
        setupBottomAppBar()
    }

    private fun setupBottomAppBar() {
        bottomAppBar.inflateMenu(R.menu.main)
        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuSearch -> showSearchFragment()
            }
            true
        }
    }

    private fun showSearchFragment() {
        val existFragment =
            addFragment(supportFragmentManager, SearchFragment.newInstance(), SearchFragment::class.java.simpleName)
        (existFragment as? SearchFragment)?.focusToEditText()
    }


    override fun onStart() {
        super.onStart()
        receiverManager.registerReceiver(postReceiver, PostService.getIntentFilter())
    }

    override fun onStop() {
        super.onStop()
        receiverManager.unregisterReceiver(postReceiver)
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
            supportFragmentManager.findFragmentById(R.id.fragmentPlaceholder) as? Util.DrawerContentFragment ?: return
        navigationView.menu.findItem(fragment.menuItemId)?.isChecked = true
    }

    private fun uncheckMenuItem(menu: Menu) {
        val size = menu.size()
        for (i in 0 until size) {
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
        val fragment = ComposePostDialogFragment.newInstance(cx, cy)
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
        val tag = menuId.toString()
        val cache = supportFragmentManager.findFragmentByTag(tag)
        val fragment = cache ?: fragmentMap[menuId]?.let { it() } ?: return
        addFragment(supportFragmentManager, fragment, tag)
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
