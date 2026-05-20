package com.coworker.jjikmuk.feature.mypage

import androidx.lifecycle.ViewModel
import com.coworker.jjikmuk.domain.model.ProfileRelation
import com.coworker.jjikmuk.domain.model.UserProfile
import com.coworker.jjikmuk.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val profiles = userProfileRepository.getProfiles()

    val myProfile: UserProfile = profiles.first { profile ->
        profile.relation == ProfileRelation.ME
    }

    val familyProfiles: List<UserProfile> = profiles
}
