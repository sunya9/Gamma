package net.unsweets.gamma.domain.repository

import android.content.Context

class PreferenceRepository(val context: Context) : AbstractPreferenceRepository(context), IPreferenceRepository {

    override val name = "Store"
    enum class PrefKey(val key: String) {
        ID("id"),
        Token("token"),
        AccountListOrder("account_list_order")
    }


}