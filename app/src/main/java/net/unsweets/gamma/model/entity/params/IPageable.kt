package net.unsweets.gamma.model.entity.params

interface IPageable {
    val minId: String?
    val maxId: String?
    val count: Int?
    fun toMap(): HashMap<String, String> {
        val map = hashMapOf<String, String>()
        val min = minId
        val max = maxId
        val c = count
        if (min != null) map["before_id"] = min
        if (max != null) map["since_id"] = max
        if (c != null) map["count"] = c.toString()
        return map
    }
}