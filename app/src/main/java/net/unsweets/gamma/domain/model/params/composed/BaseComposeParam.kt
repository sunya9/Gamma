package net.unsweets.gamma.domain.model.params.composed

import android.util.Log
import net.unsweets.gamma.domain.model.params.single.BaseParam

abstract class BaseComposeParam: BaseParam {
    protected val queryList: MutableList<BaseParam> = mutableListOf()
    override fun toMap(): Map<String, String> {
        return queryList.fold(hashMapOf(), { res, current ->
            val currentMap = current.toMap()
            currentMap.entries.forEach { entry ->
                res[entry.key] = entry.value
            }
            Log.e("res", res.toString())
            res
        })
    }
}