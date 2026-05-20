package com.coworker.jjikmuk.feature.mypage.family

import androidx.lifecycle.ViewModel
import com.coworker.jjikmuk.domain.model.UserProfile
import com.coworker.jjikmuk.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FamilyAllergyViewModel @Inject constructor(
    userProfileRepository: UserProfileRepository
) : ViewModel() {

    val profiles: List<UserProfile> = userProfileRepository.getProfiles()
}
