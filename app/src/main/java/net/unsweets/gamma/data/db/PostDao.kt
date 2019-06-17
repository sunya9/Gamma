package net.unsweets.gamma.data.db

import androidx.room.Dao
import androidx.room.Query
import net.unsweets.gamma.domain.entity.Post

@Dao
interface PostDao {
    @Query("select * from post order by id LIMIT :limit OFFSET :offset")
    fun findAll(limit: Int, offset: Int): List<Post>
}