package net.unsweets.gamma.model.raw

object Raw {
    interface IRaw {
        val type: String
        val value: RawValue
    }

    interface RawValue
}
