package com.parthdesai1208.jarassignment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Screen1FragmentViewModel : CoroutineScopedViewModel() {

    private var shapeMutableList = MutableLiveData<List<String>>()
    val shapeLiveList: LiveData<List<String>>
        get() = shapeMutableList

    fun setShapeLiveList(list: List<String>) {
        shapeMutableList.value = list
    }

    fun updateShapeLiveList(shape: String, positionToUpdate: Int) {
        val oldList = shapeMutableList.value
        val newList = ArrayList<String>()
        newList.addAll(oldList.orEmpty())
        newList[positionToUpdate] = shape
        shapeMutableList.value = newList
    }

}