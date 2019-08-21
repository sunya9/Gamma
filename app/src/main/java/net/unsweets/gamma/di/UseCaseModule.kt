package net.unsweets.gamma.di

import dagger.Module
import dagger.Provides
import net.unsweets.gamma.domain.repository.IAccountRepository
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.domain.usecases.*

@Module
class UseCaseModule {
    @Provides
    fun provideTokenUseCase(
        accountRepository: IAccountRepository,
        pnutRepository: IPnutRepository
    ): VerifyTokenUseCase = VerifyTokenUseCase(accountRepository, pnutRepository)

    @Provides
    fun provideGetPostUseCase(pnutRepository: IPnutRepository): GetPostUseCase = GetPostUseCase(pnutRepository)

    @Provides
    fun provideGetInteractionUseCase(pnutRepository: IPnutRepository): GetInteractionUseCase =
        GetInteractionUseCase(pnutRepository)

    @Provides
    fun provideSetUpTokenUseCase(
        pnutRepository: IPnutRepository,
        accountRepository: IAccountRepository,
        preferenceRepository: IPreferenceRepository
    ): SetupTokenUseCase = SetupTokenUseCase(pnutRepository, accountRepository, preferenceRepository)

    @Provides
    fun provideGetAuthenticatedUserUseCase(
        pnutRepository: IPnutRepository
    ): GetAuthenticatedUserUseCase = GetAuthenticatedUserUseCase(pnutRepository)

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
        pnutRepository: IPnutRepository,
        accountRepository: IAccountRepository
    ): PostUseCase = PostUseCase(pnutRepository, accountRepository)

    @Provides
    fun provideStarUseCase(
        pnutRepository: IPnutRepository
    ): StarUseCase = StarUseCase(pnutRepository)

    @Provides
    fun provideRepostUseCase(
        pnutRepository: IPnutRepository
    ): RepostUseCase = RepostUseCase(pnutRepository)

    @Provides
    fun provideGetCurrentAccountUseCase(
        accountRepository: IAccountRepository
    ): GetCurrentAccountUseCase = GetCurrentAccountUseCase(accountRepository)

    @Provides
    fun provideUpdateProfileUseCase(
        pnutRepository: IPnutRepository
    ): UpdateProfileUseCase = UpdateProfileUseCase(pnutRepository)

    @Provides
    fun provideFollowUseCase(
        pnutRepository: IPnutRepository
    ): FollowUseCase = FollowUseCase(pnutRepository)

    @Provides
    fun provideGetAccountListUseCase(
        accountRepository: IAccountRepository
    ): GetAccountListUseCase = GetAccountListUseCase(accountRepository)

    @Provides
    fun provideUpdateDefaultAccountUseCase(
        accountRepository: IAccountRepository,
        pnutRepository: IPnutRepository
    ): UpdateDefaultAccountUseCase = UpdateDefaultAccountUseCase(accountRepository, pnutRepository)

    @Provides
    fun provideLogoutUseCase(
        accountRepository: IAccountRepository,
        pnutRepository: IPnutRepository
    ): LogoutUseCase = LogoutUseCase(accountRepository, pnutRepository)

    @Provides
    fun provideUploadFileUseCase(
        pnutRepository: IPnutRepository
    ): UploadFileUseCase = UploadFileUseCase(pnutRepository)

    @Provides
    fun provideDeletePostUseCase(
        pnutRepository: IPnutRepository
    ): DeletePostUseCase = DeletePostUseCase(pnutRepository)

    @Provides
    fun provideUpdateUserImageUseCase(
        pnutRepository: IPnutRepository
    ): UpdateUserImageUseCase = UpdateUserImageUseCase(pnutRepository)
}