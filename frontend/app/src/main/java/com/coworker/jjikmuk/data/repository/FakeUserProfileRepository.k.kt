package com.coworker.jjikmuk.data.repository

import com.coworker.jjikmuk.domain.model.UserProfile
import com.coworker.jjikmuk.domain.repository.UserProfileRepository
import javax.inject.Inject

class FakeUserProfileRepository @Inject constructor() : UserProfileRepository {

    override fun getProfiles(): List<UserProfile> {
        return listOf(
            UserProfile(
                id = "me",
                name = "나",
                isSelected = true
            ),
            UserProfile(
                id = "coworker",
                name = "코워커",
                isSelected = false
            ),
            UserProfile(
                id = "family_1",
                name = "가족 1",
                isSelected = false
            )
        )
    }
}