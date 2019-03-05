package net.unsweets.gamma.model.entity.raw

object Raw {
    interface IRaw {
        val type: String
        val value: RawValue
    }

    interface RawValue
}
