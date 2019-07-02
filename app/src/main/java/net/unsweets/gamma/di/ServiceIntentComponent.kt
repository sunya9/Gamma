package net.unsweets.gamma.di

import dagger.Subcomponent

@Subcomponent(modules = [ServiceIntentModule::class])
interface ServiceIntentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ServiceIntentComponent
    }
}