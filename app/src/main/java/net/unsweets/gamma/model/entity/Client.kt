package net.unsweets.gamma.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Client(
    var name: String,
    var link: String,
    @PrimaryKey var id: String
) : Parcelable