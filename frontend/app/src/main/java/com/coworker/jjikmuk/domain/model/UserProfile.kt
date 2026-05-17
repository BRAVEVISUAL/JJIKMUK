package com.coworker.jjikmuk.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val imageResId: Int,
    val isSelected: Boolean = false
)