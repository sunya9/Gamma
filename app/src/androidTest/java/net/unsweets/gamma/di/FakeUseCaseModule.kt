package net.unsweets.gamma.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.CachedList
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.io.GetCachedPostListInputData
import net.unsweets.gamma.domain.model.io.GetCachedPostListOutputData
import net.unsweets.gamma.domain.usecases.*
import org.mockito.Mockito

@Module
class FakeUseCaseModule {
  lateinit var setupTokenUseCase: SetupTokenUseCase
  lateinit var getAccountListUseCase: GetAccountListUseCase
  lateinit var verifyTokenUseCase: VerifyTokenUseCase

  @Provides
  fun provideSetupTokenUseCase() = setupTokenUseCase

  @Provides
  fun provideTokenUseCase(): VerifyTokenUseCase = verifyTokenUseCase

  @Provides
  fun provideGetPostUseCase(): GetPostUseCase =
    Mockito.mock(GetPostUseCase::class.java)

  @Provides
  fun provideGetInteractionUseCase(): GetInteractionUseCase =
    Mockito.mock(GetInteractionUseCase::class.java)

  @Provides
  fun provideGetAuthenticatedUserUseCase(): GetAuthenticatedUserUseCase =
    Mockito.mock(GetAuthenticatedUserUseCase::class.java)

  @Provides
  fun provideGetFilesUseCase(): GetFilesUseCase = Mockito.mock(GetFilesUseCase::class.java)

  @Provides
  fun provideGetProfileUseCase(): GetProfileUseCase = Mockito.mock(GetProfileUseCase::class.java)

  @Provides
  fun provideGetUsersUseCase(): GetUsersUseCase = Mockito.mock(GetUsersUseCase::class.java)

  @Provides
  fun providePostUseCase(): PostUseCase = Mockito.mock(PostUseCase::class.java)

  @Provides
  fun provideStarUseCase(): StarUseCase = Mockito.mock(StarUseCase::class.java)

  @Provides
  fun provideRepostUseCase(): RepostUseCase = Mockito.mock(RepostUseCase::class.java)

  @Provides
  fun provideGetCurrentAccountUseCase(): GetCurrentAccountUseCase =
    Mockito.mock(GetCurrentAccountUseCase::class.java)

  @Provides
  fun provideUpdateProfileUseCase(): UpdateProfileUseCase =
    Mockito.mock(UpdateProfileUseCase::class.java)

  @Provides
  fun provideFollowUseCase(): UpdateRelationshipUseCase =
    Mockito.mock(UpdateRelationshipUseCase::class.java)

  @Provides
  fun provideGetAccountListUseCase(): GetAccountListUseCase = getAccountListUseCase

  @Provides
  fun provideUpdateDefaultAccountUseCase(): UpdateDefaultAccountUseCase =
    Mockito.mock(UpdateDefaultAccountUseCase::class.java)

  @Provides
  fun provideLogoutUseCase(): LogoutUseCase = Mockito.mock(LogoutUseCase::class.java)

  @Provides
  fun provideUploadFileUseCase(): UploadFileUseCase = Mockito.mock(UploadFileUseCase::class.java)

  @Provides
  fun provideDeletePostUseCase(): DeletePostUseCase = Mockito.mock(DeletePostUseCase::class.java)

  @Provides
  fun provideUpdateUserImageUseCase(): UpdateUserImageUseCase =
    Mockito.mock(UpdateUserImageUseCase::class.java)

  @Provides
  fun provideGetCachedPostListUseCase(): GetCachedPostListUseCase =
    Mockito.mock(GetCachedPostListUseCase::class.java).also {
      runBlocking {
        Mockito.`when`(it.run(GetCachedPostListInputData(StreamType.Home)))
      }.thenReturn(
        GetCachedPostListOutputData(CachedList(emptyList()))
      )
    }

  @Provides
  fun provideGetCachedUserListUseCase(): GetCachedUserListUseCase =
    Mockito.mock(GetCachedUserListUseCase::class.java)

  @Provides
  fun provideGetCachedInteractionListUseCase(): GetCachedInteractionListUseCase =
    Mockito.mock(GetCachedInteractionListUseCase::class.java)

  @Provides
  fun provideCachePostUseCase(): CachePostUseCase =
    Mockito.mock(CachePostUseCase::class.java)

  @Provides
  fun provideCacheUserUseCase(): CacheUserUseCase =
    Mockito.mock(CacheUserUseCase::class.java)

  @Provides
  fun provideCacheInteractionUseCase(): CacheInteractionUseCase =
    Mockito.mock(CacheInteractionUseCase::class.java)

  @Provides
  fun provideCreatePollUseCase(): CreatePollUseCase = Mockito.mock(CreatePollUseCase::class.java)

  @Provides
  fun provideGetPollUseCase(): GetPollUseCase = Mockito.mock(GetPollUseCase::class.java)

  @Provides
  fun provideVoteUseCase(): VoteUseCase = Mockito.mock(VoteUseCase::class.java)
}