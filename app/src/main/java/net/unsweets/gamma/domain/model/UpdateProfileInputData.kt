package net.unsweets.gamma.domain.model

data class UpdateProfileInputData(
    val name: String,
    val description: String,
    val timezone: String,
    val locale: String
)
