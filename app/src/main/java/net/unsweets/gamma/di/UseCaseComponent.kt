package net.unsweets.gamma.di

import dagger.Subcomponent

@Subcomponent(modules = [UseCaseModule::class])
interface UseCaseComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): UseCaseComponent
    }
}