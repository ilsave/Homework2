package com.example.gardenwater.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gardenwater.repositories.Repository

class ViewModelFactory(val repository: Repository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelGarden(repository) as T
    }
}