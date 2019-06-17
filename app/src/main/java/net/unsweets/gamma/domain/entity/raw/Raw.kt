package net.unsweets.gamma.domain.entity.raw

object Raw {
    interface IRaw {
        val type: String
        val value: RawValue
    }

    interface RawValue
}
