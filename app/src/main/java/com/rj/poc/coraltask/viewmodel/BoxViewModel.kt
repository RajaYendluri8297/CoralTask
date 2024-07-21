package com.rj.poc.coraltask.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.rj.poc.coraltask.data.room.Box
import com.rj.poc.coraltask.data.room.BoxDao

class BoxViewModel(private val boxDao: BoxDao) : ViewModel() {
    val boxes: LiveData<List<Box>> = liveData {
        val data = withContext(Dispatchers.IO) {
            boxDao.getAllBoxes()
        }
        emit(data)
    }

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    fun saveBoxes(boxes: List<Box>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                boxDao.clearBoxes()
                boxes.forEach { boxDao.insertBox(it) }
                _saveStatus.postValue(true)
            } catch (e: Exception) {
                _saveStatus.postValue(false)
            }
        }
    }

    fun clearBoxes() {
        viewModelScope.launch(Dispatchers.IO) {
            boxDao.clearBoxes()
        }
    }
}
