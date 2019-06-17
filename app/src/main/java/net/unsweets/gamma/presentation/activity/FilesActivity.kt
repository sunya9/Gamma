package net.unsweets.gamma.presentation.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_files.*
import net.unsweets.gamma.R

class FilesActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)
        setSupportActionBar(toolbar)
    }


}
