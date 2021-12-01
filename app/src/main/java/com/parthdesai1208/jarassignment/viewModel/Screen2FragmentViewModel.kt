package com.parthdesai1208.jarassignment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.parthdesai1208.jarassignment.model.AppRepository
import com.parthdesai1208.jarassignment.model.UnSplashModel

class Screen2FragmentViewModel : CoroutineScopedViewModel() {

    private var imageMutableList = MutableLiveData<List<UnSplashModel>>()
    val imageLiveList: LiveData<List<UnSplashModel>>
        get() = imageMutableList

    fun setImageLiveList(list: List<UnSplashModel>) {
        val oldList = imageMutableList.value
        val newList = ArrayList<UnSplashModel>()
        newList.addAll(oldList.orEmpty())
        newList.addAll(list)
        imageMutableList.value = newList
    }

    fun getImageCall(page: Int) = with(AppRepository) {
        getImage(page)
    }
}