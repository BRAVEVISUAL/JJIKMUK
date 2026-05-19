package com.coworker.jjikmuk.feature.home

import androidx.lifecycle.ViewModel
import com.coworker.jjikmuk.R
import com.coworker.jjikmuk.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val initialProfiles = listOf(
        UserProfile(
            id = "me",
            name = "나",
            imageResId = R.drawable.ic_launcher_foreground,
            isSelected = true
        ),
        UserProfile(
            id = "coworker",
            name = "코워커",
            imageResId = R.drawable.ic_launcher_foreground,
            isSelected = false
        ),
        UserProfile(
            id = "family_1",
            name = "가족 1",
            imageResId = R.drawable.ic_launcher_foreground,
            isSelected = false
        )
    )

    private val _uiState = MutableStateFlow(
        HomeUiState(
            profiles = initialProfiles,
            selectedProfiles = initialProfiles.filter { profile -> profile.isSelected }
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState

    fun updateInputMessage(message: String) {
        _uiState.update { state ->
            state.copy(inputMessage = message)
        }
    }

    fun toggleProfile(profileId: String) {
        _uiState.update { state ->
            val updatedProfiles = state.profiles.map { profile ->
                if (profile.id == profileId) {
                    profile.copy(isSelected = !profile.isSelected)
                } else {
                    profile
                }
            }

            state.copy(
                profiles = updatedProfiles,
                selectedProfiles = updatedProfiles.filter { profile -> profile.isSelected }
            )
        }
    }

    fun getCurrentMessage(): String {
        return uiState.value.inputMessage.trim()
    }

    fun clearInputMessage() {
        _uiState.update { state ->
            state.copy(inputMessage = "")
        }
    }
}