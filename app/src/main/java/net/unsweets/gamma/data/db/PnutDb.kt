package net.unsweets.gamma.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.unsweets.gamma.domain.entity.Post

@Database(entities = [Post::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PnutDb : RoomDatabase() {
    abstract fun getPostDao(): PostDao
}