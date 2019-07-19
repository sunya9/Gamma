package net.unsweets.gamma.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_photo.*
import net.unsweets.gamma.R
import java.io.File


class EditPhotoActivity : BaseActivity() {

    private val uri: Uri by lazy {
        intent.getParcelableExtra<Uri>(IntentKey.Uri.name) ?: throw IllegalArgumentException("Must set Uri")
    }
    private val index by lazy {
        intent.getIntExtra(IntentKey.Index.name, -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)
        setSupportActionBar(toolbar)
        if (mode == Mode.Square) {
            cropImageView.setAspectRatio(1, 1)
            cropImageView.setFixedAspectRatio(true)
        }
        cropImageView.setImageUriAsync(uri)
        cropImageView.setOnCropImageCompleteListener { _, result -> cropped(result) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_photo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item?.itemId?.let {
            when (it) {
                R.id.menuCrop -> requestToCrop()
                R.id.menuRotateRight -> rotate()
                else -> super.onOptionsItemSelected(item)
            }
            true
        } ?: true
    }

    private fun rotate() {
        cropImageView.rotatedDegrees = cropImageView.rotatedDegrees + 90
    }

    private fun requestToCrop() {
        val ext = File(uri.path).extension
        cropImageView.saveCroppedImageAsync(
            Uri.fromFile(File(externalCacheDir, "${System.currentTimeMillis()}.$ext"))
        )
    }

    private fun cropped(result: CropImageView.CropResult) {
        val data = Intent().apply {
            putExtra(IntentKey.Index.name, index)
            putExtra(IntentKey.Uri.name, result.uri)
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private val mode by lazy {
        intent.getSerializableExtra(IntentKey.Mode.name) as? Mode ?: Mode.Free
    }

    private enum class IntentKey { Uri, Index, Mode }
    enum class Mode { Free, Square }
    data class EditPhotoResult(val uri: Uri, val index: Int)
    companion object {
        fun newIntentSquareMode(context: Context?, uri: Uri) = newIntent(context, uri).apply {
            putExtra(IntentKey.Mode.name, Mode.Square)
        }

        fun newIntent(context: Context?, uri: Uri, index: Int = -1) =
            Intent(context, EditPhotoActivity::class.java).apply {
            putExtra(IntentKey.Uri.name, uri)
            putExtra(IntentKey.Index.name, index)
        }
        fun parseIntent(intent: Intent): EditPhotoResult {
            val uri = intent.getParcelableExtra<Uri>(IntentKey.Uri.name)
            val index = intent.getIntExtra(IntentKey.Index.name, -1)
            return EditPhotoResult(uri, index)
        }
    }
}
