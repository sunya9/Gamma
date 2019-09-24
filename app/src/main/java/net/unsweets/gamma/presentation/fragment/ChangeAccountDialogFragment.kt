package net.unsweets.gamma.presentation.fragment


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerDialogFragment
import kotlinx.android.synthetic.main.account_list.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.domain.usecases.GetAccountListUseCase
import net.unsweets.gamma.presentation.adapter.AccountListAdapter
import net.unsweets.gamma.util.ErrorCollections
import javax.inject.Inject

class ChangeAccountDialogFragment : DaggerDialogFragment() {
    private val currentUserId: String by lazy {
        arguments?.getString(BundleKey.CurrentUserId.name) ?: throw ErrorCollections.AccountNotFound
    }

    private enum class BundleKey { CurrentUserId }

    interface Callback {
        fun changeAccount(account: Account)
    }

    private var listener: Callback? = null

    @Inject
    lateinit var getAccountListUseCase: GetAccountListUseCase

    private val accounts
        get() = getAccountListUseCase.run(Unit).accounts.filterNot { it.id == currentUserId }

    private val accountListListener = object : AccountListAdapter.Listener {
        override fun onAccountClick(account: Account) {
            listener?.changeAccount(account)
            dismiss()
        }

        override fun onAddAccount() {
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(
            R.layout.fragment_change_account_dialog,
            view?.findViewById(android.R.id.content)
        )
        view.accountList.adapter = AccountListAdapter(
            accounts,
            accountListListener,
            false
        )

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.accounts)
            .setView(view)
            .create()
    }

    companion object {
        fun newInstance(currentUserId: String) = ChangeAccountDialogFragment().apply {
            arguments = Bundle().apply {
                putString(BundleKey.CurrentUserId.name, currentUserId)
            }
        }
    }

}
