package net.unsweets.gamma.mock

import android.content.SharedPreferences
import net.unsweets.gamma.domain.model.preference.ShapeOfAvatar
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.presentation.util.ThemeColorUtil

open class PreferenceRepositoryMock : IPreferenceRepository {
  override fun onRegisterChangePreference(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onUnregisterChangePreference(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun load() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun reload() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override val themeColor: ThemeColorUtil.ThemeColor
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val darkMode: ThemeColorUtil.DarkMode
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val darkModeStr: String
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val avatarSwipe: Boolean
    get() = true
  override val loadingSize: Int
    get() = 20
  override val thresholdOfAutoPager: Int
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val unifiedStream: Boolean
    get() = false
  override val shapeOfAvatar: ShapeOfAvatar
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
  override val cache: Boolean
    get() = true
  override val cacheSize: Int
    get() = 100
  override val includeDirectedPosts: Boolean
    get() = false
}