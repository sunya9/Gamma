package net.unsweets.gamma.data.db.dao

import androidx.room.*
import net.unsweets.gamma.data.db.entities.Account

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAccount(account: Account)

    @Query("select token from Account where id = :id")
    fun getToken(id: String): String

    @Delete
    fun deleteAccount(account: Account)
}