package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM saved_articles ORDER BY savedAtTimestamp DESC")
    fun getAllSaved(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM saved_articles WHERE id = :id")
    suspend fun getSavedById(id: String): ArticleEntity?

    @Query("SELECT id FROM saved_articles")
    fun getAllSavedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: ArticleEntity)

    @Query("UPDATE saved_articles SET readingProgress = :progress WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Float)

    @Query("DELETE FROM saved_articles WHERE id = :id")
    suspend fun deleteById(id: String)
}
