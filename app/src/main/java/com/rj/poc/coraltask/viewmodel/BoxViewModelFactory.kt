package com.rj.poc.coraltask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rj.poc.coraltask.data.room.BoxDao

class BoxViewModelFactory(private val boxDao: BoxDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BoxViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BoxViewModel(boxDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



