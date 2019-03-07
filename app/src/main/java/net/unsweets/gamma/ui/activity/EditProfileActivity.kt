package net.unsweets.gamma.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.unsweets.gamma.R
import net.unsweets.gamma.model.entity.User

class EditProfileActivity : AppCompatActivity() {
    private enum class IntentKey { User }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<net.unsweets.gamma.databinding.ActivityEditProfileBinding>(
            this,
            R.layout.activity_edit_profile
        )

        val timezoneAdapter =
            ArrayAdapter.createFromResource(this, R.array.timezones, android.R.layout.simple_spinner_dropdown_item)
                .also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
        binding.viewTimezoneSpinner.adapter = timezoneAdapter

        val localeAdapter =
            ArrayAdapter.createFromResource(this, R.array.locales, android.R.layout.simple_spinner_dropdown_item).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        binding.viewLocaleSpinner.adapter = localeAdapter
        setTitle(R.string.edit_profile)

        val user = intent.getParcelableExtra<User>(IntentKey.User.name)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item?.let {
            when (it.itemId) {
                android.R.id.home -> discard()
                R.id.menuSave -> save()
            }
            true
        } ?: super.onOptionsItemSelected(item)
    }

    private fun discard() {
        // TODO: show confirmation dialog
        finish()
    }

    private fun save() {
        // TODO: show progress bar and save
        finish()
    }

    class EditProfileViewModel : ViewModel() {
        val name = MutableLiveData<String>()
        val description = MutableLiveData<String>()
        val timezone = MutableLiveData<String>()
        val locale = MutableLiveData<String>()
        var user = MutableLiveData<User>()
            set(value) {
                val user = value.value ?: return
                name.value = user.name
                description.value = user.content.text
                timezone.value = user.timezone
                locale.value = user.locale
                field = value
            }
    }

    companion object {
        fun newIntent(context: Context, user: User) = Intent(context, EditProfileActivity::class.java).apply {
            putExtra(IntentKey.User.name, user)
        }
    }
}
