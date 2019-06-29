package net.unsweets.gamma.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import net.unsweets.gamma.data.db.dao.AccountDao
import net.unsweets.gamma.data.db.entities.Account

@Database(entities = [Account::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}