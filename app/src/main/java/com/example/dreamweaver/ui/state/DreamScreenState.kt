package com.example.dreamweaver.ui.state

import com.example.dreamweaver.data.model.Dream

enum class DreamSort { NEWEST, OLDEST }

enum class DreamFilter { ALL, LUCID_ONLY }

/**
 * Single immutable state object exposed by [com.example.dreamweaver.ui.DreamViewModel].
 * The Compose UI observes this and never talks to the repository directly.
 */
data class DreamScreenState(
    val dreams: List<Dream> = emptyList(),
    val dailyInsight: String? = null,
    val isLoadingInsight: Boolean = false,
    val sort: DreamSort = DreamSort.NEWEST,
    val filter: DreamFilter = DreamFilter.ALL
) {
    val displayedDreams: List<Dream>
        get() {
            val filtered = when (filter) {
                DreamFilter.ALL -> dreams
                DreamFilter.LUCID_ONLY -> dreams.filter { it.isLucid }
            }
            return when (sort) {
                DreamSort.NEWEST -> filtered.sortedByDescending { it.timestamp }
                DreamSort.OLDEST -> filtered.sortedBy { it.timestamp }
            }
        }
}
