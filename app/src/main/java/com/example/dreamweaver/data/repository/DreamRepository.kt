package com.example.dreamweaver.data.repository

import com.example.dreamweaver.data.local.DreamDao
import com.example.dreamweaver.data.local.DreamEntity
import com.example.dreamweaver.data.model.Dream
import com.example.dreamweaver.data.remote.AdviceApiService
import com.example.dreamweaver.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Single source of truth for dream data. Hides the fact that data comes from
 * two different places (Room local journal + Retrofit remote insight) from
 * the rest of the app (MVVM "Model" layer).
 */
class DreamRepository(
    private val dao: DreamDao,
    private val api: AdviceApiService
) {

    /** Live stream of all journal entries, straight from Room. */
    fun observeDreams(): Flow<List<Dream>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    suspend fun addDream(dream: Dream) = withContext(Dispatchers.IO) {
        dao.insert(dream.toEntity())
    }

    suspend fun deleteDream(dream: Dream) = withContext(Dispatchers.IO) {
        dao.delete(dream.toEntity())
    }

    /** Fetches a fresh "dream insight of the day" from the remote REST API. */
    suspend fun fetchDailyInsight(): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.getRandomAdvice()
            Resource.Success(response.slip.advice)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "ქსელური შეცდომა მოხდა")
        }
    }

    private fun DreamEntity.toDomain() = Dream(
        id = id,
        title = title,
        description = description,
        mood = mood,
        isLucid = isLucid,
        timestamp = timestamp
    )

    private fun Dream.toEntity() = DreamEntity(
        id = id,
        title = title,
        description = description,
        mood = mood,
        isLucid = isLucid,
        timestamp = timestamp
    )
}
