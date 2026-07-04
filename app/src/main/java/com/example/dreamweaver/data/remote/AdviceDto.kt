package com.example.dreamweaver.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Response shape from the free, key-less Advice Slip API
 * (https://api.adviceslip.com), reused here as a daily "dream insight".
 */
data class AdviceResponseDto(
    @SerializedName("slip") val slip: AdviceSlipDto
)

data class AdviceSlipDto(
    @SerializedName("id") val id: Int,
    @SerializedName("advice") val advice: String
)
