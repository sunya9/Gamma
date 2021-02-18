package net.unsweets.gamma.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import net.unsweets.gamma.databinding.ActivityManageChannelMembersBinding
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.presentation.adapter.ChannelMembersAdapter
import net.unsweets.gamma.presentation.viewmodel.ManageChannelMembersViewModel
import javax.inject.Inject

class ManageChannelMembersActivity : BaseActivity() {
    private val eventObserver: Observer<ManageChannelMembersViewModel.Event> = Observer {
        when (it) {
            is ManageChannelMembersViewModel.Event.ManipulatedList -> adapter.manipulateList(it.manipulateList)
        }
    }
    private val channelId by lazy { intent.getStringExtra(IntentKey.ChannelId.name) }

    @Inject
    lateinit var pnutRepository: IPnutRepository

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ManageChannelMembersViewModel.Factory(channelId, pnutRepository)
        )[ManageChannelMembersViewModel::class.java]
    }
    private val adapter by lazy { ChannelMembersAdapter { viewModel.items } }

    private val binding by lazy {
        ActivityManageChannelMembersBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.event.observe(this, eventObserver)
        binding.manageChannelMembersToolbar.setNavigationOnClickListener { finishAfterTransition() }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.channelMembersRecyclerView.adapter = adapter

    }

    private enum class IntentKey {
        ChannelId
    }

    companion object {
        fun newIntent(context: Context, channelId: String) =
            Intent(context, ManageChannelMembersActivity::class.java).also {
                it.putExtra(IntentKey.ChannelId.name, channelId)
            }
    }
}