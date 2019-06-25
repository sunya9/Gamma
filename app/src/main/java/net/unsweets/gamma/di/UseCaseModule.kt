package net.unsweets.gamma.di

import dagger.Module
import dagger.Provides
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.domain.usecases.*

@Module
class UseCaseModule {
    @Provides
    fun provideTokenUseCase(
        pnutRepository: IPnutRepository,
        preferenceRepository: IPreferenceRepository
    ): VerifyTokenUseCase = VerifyTokenUseCase(pnutRepository, preferenceRepository)

    @Provides
    fun provideGetPostUseCase(pnutRepository: IPnutRepository): GetPostUseCase = GetPostUseCase(pnutRepository)

    @Provides
    fun provideGetInteractionUseCase(pnutRepository: IPnutRepository): GetInteractionUseCase =
        GetInteractionUseCase(pnutRepository)

    @Provides
    fun provideSetUpTokenUseCase(
        pnutRepository: IPnutRepository,
        preferenceRepository: IPreferenceRepository
    ): SetupTokenUseCase = SetupTokenUseCase(pnutRepository, preferenceRepository)

    @Provides
    fun provideGetAuthenticatedUserUseCase(
        pnutRepository: IPnutRepository,
        preferenceRepository: IPreferenceRepository
    ): GetAuthenticatedUserUseCase = GetAuthenticatedUserUseCase(pnutRepository, preferenceRepository)

    @Provides
    fun provideGetFilesUseCase(
        pnutRepository: IPnutRepository
    ): GetFilesUseCase = GetFilesUseCase(pnutRepository)

    @Provides
    fun provideGetProfileUseCase(
        pnutRepository: IPnutRepository
    ): GetProfileUseCase = GetProfileUseCase(pnutRepository)

    @Provides
    fun provideGetUsersUseCase(
        pnutRepository: IPnutRepository
    ): GetUsersUseCase = GetUsersUseCase(pnutRepository)

    @Provides
    fun providePostUseCase(
        pnutRepository: IPnutRepository
    ): PostUseCase = PostUseCase(pnutRepository)

    @Provides
    fun provideStarUseCase(
        pnutRepository: IPnutRepository
    ): StarUseCase = StarUseCase(pnutRepository)

    @Provides
    fun provideRepostUseCase(
        pnutRepository: IPnutRepository
    ): RepostUseCase = RepostUseCase(pnutRepository)

    @Provides
    fun provideGetCurrentUserIdUseCase(
        preferenceRepository: IPreferenceRepository
    ): GetCurrentUserIdUseCase = GetCurrentUserIdUseCase(preferenceRepository)

    @Provides
    fun updateProfileUseCase(
        pnutRepository: IPnutRepository
    ): UpdateProfileUseCase = UpdateProfileUseCase(pnutRepository)
}