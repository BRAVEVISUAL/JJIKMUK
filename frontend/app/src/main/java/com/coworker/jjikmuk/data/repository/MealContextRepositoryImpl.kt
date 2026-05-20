package com.coworker.jjikmuk.data.repository

import com.coworker.jjikmuk.domain.model.MealContext
import com.coworker.jjikmuk.domain.repository.MealContextRepository
import com.coworker.jjikmuk.domain.repository.UserProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MealContextRepositoryImpl @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : MealContextRepository {

    private val profiles = userProfileRepository.getProfiles()
    private val defaultSelectedProfileIds = profiles
        .firstOrNull()
        ?.let { profile -> setOf(profile.id) }
        .orEmpty()

    private val _mealContext = MutableStateFlow(
        createMealContext(defaultSelectedProfileIds)
    )

    override val mealContext: StateFlow<MealContext> = _mealContext.asStateFlow()

    override fun setSelectedProfileIds(profileIds: Set<String>) {
        val nextIds = profileIds.ifEmpty { defaultSelectedProfileIds }
        _mealContext.value = createMealContext(nextIds)
    }

    private fun createMealContext(profileIds: Set<String>): MealContext {
        val selectedProfiles = profiles.filter { profile ->
            profile.id in profileIds
        }

        return MealContext(
            selectedProfileIds = selectedProfiles.map { profile -> profile.id }.toSet(),
            selectedProfiles = selectedProfiles,
            allergyNames = selectedProfiles
                .flatMap { profile -> profile.allergies }
                .distinct()
        )
    }
}
