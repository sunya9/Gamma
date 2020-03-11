package net.unsweets.gamma.testutil

import androidx.test.core.app.ApplicationProvider
import net.unsweets.gamma.GammaApplication
import net.unsweets.gamma.di.DaggerTestAppComponent
import net.unsweets.gamma.di.FakeAppModule
import net.unsweets.gamma.di.FakeUseCaseModule

data class OverrideModules(private val callback: (modules: Modules) -> Unit) {
  data class Modules(
    val fakeAppModule: FakeAppModule,
    val fakeUseCaseModule: FakeUseCaseModule
  )

  private val app by lazy {
    ApplicationProvider.getApplicationContext<GammaApplication>()
  }

  init {
    overrideModules()
  }

  private fun overrideModules() {
    val fakeUseCaseModule = FakeUseCaseModule()
    val fakeAppModule = FakeAppModule(app)
    val modules = Modules(fakeAppModule, fakeUseCaseModule)
    callback(modules)
    val component = DaggerTestAppComponent.builder()
      .fakeUseCaseModule(modules.fakeUseCaseModule)
      .fakeAppModule(modules.fakeAppModule)
      .build()
    component.inject(app)
    app.updateAppComponent(component)
  }
}
