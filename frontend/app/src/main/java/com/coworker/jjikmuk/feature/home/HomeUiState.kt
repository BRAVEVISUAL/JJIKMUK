package com.coworker.jjikmuk.feature.home

import com.coworker.jjikmuk.domain.model.UserProfile

data class HomeUiState(
    val profiles: List<UserProfile> = emptyList(),
    val selectedProfiles: List<UserProfile> = emptyList(),
    val inputMessage: String = "",
    val errorMessage: String? = null
)