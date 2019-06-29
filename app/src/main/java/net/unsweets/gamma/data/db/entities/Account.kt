package net.unsweets.gamma.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
    @PrimaryKey
    val id: String,
    val token: String
)