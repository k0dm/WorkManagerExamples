package com.bugbender.workmanagerexamples.presentation

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bugbender.workmanagerexamples.App
import com.bugbender.workmanagerexamples.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModel = (application as App).stopwatchViewModel
        val timeTextView = findViewById<TextView>(R.id.timeTextView)
        val actionButton = findViewById<Button>(R.id.actionButton)
        val resetButton = findViewById<Button>(R.id.resetButton)

        viewModel.stopwatchLiveData.observe(this) { state ->
            Log.d("k0dm", state.toString())
            state.show(timeTextView, actionButton, resetButton)
        }

        actionButton.setOnClickListener {
            viewModel.actionClick()
        }

        resetButton.setOnClickListener {
            viewModel.reset()
        }

        requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
        Log.d("k0dm", "bundle is null: ${savedInstanceState==null}")
    }

    private fun requestPermissions(vararg permissions: String) {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            result.entries.forEach {
                Log.d("MainActivity", "${it.key} = ${it.value}")
            }
        }
        requestPermissionLauncher.launch(permissions.asList().toTypedArray())
    }
}