package net.unsweets.gamma.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import net.unsweets.gamma.R

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setTitle(R.string.edit_profile)
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
}
