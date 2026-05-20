package com.coworker.jjikmuk.domain.model

data class MealContext(
    val selectedProfileIds: Set<String> = emptySet(),
    val selectedProfiles: List<UserProfile> = emptyList(),
    val allergyNames: List<String> = emptyList()
)
