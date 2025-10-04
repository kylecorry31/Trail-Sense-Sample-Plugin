package com.kylecorry.trail_sense.plugin.sample.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.kylecorry.andromeda.fragments.AndromedaActivity
import com.kylecorry.andromeda.fragments.ColorTheme
import com.kylecorry.trail_sense.plugin.sample.R

class MainActivity : AndromedaActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setColorTheme(ColorTheme.System, true)
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bindLayoutInsets()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        setIntent(intent)
    }


    private fun bindLayoutInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            windowInsets
        }
    }
}
