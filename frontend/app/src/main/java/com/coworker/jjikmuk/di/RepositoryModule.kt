package com.coworker.jjikmuk.di

import com.coworker.jjikmuk.data.repository.ChatRepositoryImpl
import com.coworker.jjikmuk.data.repository.ChatHistoryRepositoryImpl
import com.coworker.jjikmuk.data.repository.FakeUserProfileRepository
import com.coworker.jjikmuk.data.repository.FavoriteRepositoryImpl
import com.coworker.jjikmuk.data.repository.MealContextRepositoryImpl
import com.coworker.jjikmuk.data.repository.ProductRepositoryImpl
import com.coworker.jjikmuk.domain.repository.ChatHistoryRepository
import com.coworker.jjikmuk.domain.repository.ChatRepository
import com.coworker.jjikmuk.domain.repository.FavoriteRepository
import com.coworker.jjikmuk.domain.repository.MealContextRepository
import com.coworker.jjikmuk.domain.repository.ProductRepository
import com.coworker.jjikmuk.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindChatHistoryRepository(
        impl: ChatHistoryRepositoryImpl
    ): ChatHistoryRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: FakeUserProfileRepository
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindMealContextRepository(
        impl: MealContextRepositoryImpl
    ): MealContextRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl
    ): FavoriteRepository
}
