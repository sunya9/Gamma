package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.account_list_footer_item.view.*
import kotlinx.android.synthetic.main.account_list_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.presentation.util.GlideApp

class AccountListAdapter(
    private val accounts: List<Account>,
    private val listener: Listener,
    private val showAddAccountButton: Boolean = true
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ItemViewType { Body, Footer }

    interface Listener {
        fun onAccountClick(account: Account)
        fun onAddAccount()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (ItemViewType.values()[viewType]) {
            ItemViewType.Body -> ItemViewHolder(inflater.inflate(R.layout.account_list_item, parent, false))
            ItemViewType.Footer -> FooterViewHolder(inflater.inflate(R.layout.account_list_footer_item, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val type = if (!showAddAccountButton || itemCount - 1 > position) ItemViewType.Body else ItemViewType.Footer
        return type.ordinal
    }

    override fun getItemCount(): Int = if (showAddAccountButton) accounts.size + 1 else accounts.size // item + footer

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ordinal = getItemViewType(position)
        when (ItemViewType.values()[ordinal]) {
            ItemViewType.Body -> {
                (holder as? ItemViewHolder)?.also {
                    val account = accounts[position]
                    it.bindTo(account)
                    it.itemView.setOnClickListener { listener.onAccountClick(account) }
                }
            }
            ItemViewType.Footer -> (holder as? FooterViewHolder)?.also {
                it.addAccountButton.setOnClickListener { listener.onAddAccount() }
            }
        }
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarView: ImageView = itemView.accountListItemAvatarImageView
        private val usernameView: TextView = itemView.accountListItemScreenNameTextView
        private val nameView: TextView = itemView.accountListItemNameTextView
        fun bindTo(account: Account) {
            GlideApp.with(itemView.context).load(account.getAvatarUrl()).into(avatarView)
            usernameView.text = account.usernameWithAt
            nameView.text = account.name
        }
    }

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addAccountButton: MaterialButton = itemView.addAccountButton
    }
}