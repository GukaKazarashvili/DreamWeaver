package com.example.dreamweaver.data.remote

import retrofit2.http.GET

interface AdviceApiService {

    @GET("advice")
    suspend fun getRandomAdvice(): AdviceResponseDto
}
