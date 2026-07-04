package com.example.dreamweaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.dreamweaver.data.local.DreamDatabase
import com.example.dreamweaver.data.remote.RetrofitInstance
import com.example.dreamweaver.data.repository.DreamRepository
import com.example.dreamweaver.ui.DreamViewModel
import com.example.dreamweaver.ui.DreamViewModelFactory
import com.example.dreamweaver.ui.screens.DreamListScreen
import com.example.dreamweaver.ui.theme.DreamWeaverTheme

class MainActivity : ComponentActivity() {

    private val repository: DreamRepository by lazy {
        val dao = DreamDatabase.getInstance(applicationContext).dreamDao()
        DreamRepository(dao = dao, api = RetrofitInstance.api)
    }

    private val viewModel: DreamViewModel by viewModels {
        DreamViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DreamWeaverTheme {
                DreamListScreen(viewModel = viewModel)
            }
        }
    }
}
