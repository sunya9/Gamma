package net.unsweets.gamma.di

import dagger.Subcomponent

@Subcomponent(modules = [ServiceModule::class])
interface ServiceComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ServiceComponent
    }
}