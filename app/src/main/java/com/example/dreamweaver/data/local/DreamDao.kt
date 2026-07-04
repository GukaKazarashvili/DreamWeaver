package com.example.dreamweaver.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {

    @Query("SELECT * FROM dreams ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<DreamEntity>>

    @Insert
    suspend fun insert(entity: DreamEntity)

    @Delete
    suspend fun delete(entity: DreamEntity)
}
