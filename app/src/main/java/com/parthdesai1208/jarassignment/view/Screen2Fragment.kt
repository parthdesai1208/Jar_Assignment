package com.parthdesai1208.jarassignment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.autoroutesante.belocum.liveadapter.LiveAdapter
import com.google.android.material.snackbar.Snackbar
import com.parthdesai1208.jarassignment.*
import com.parthdesai1208.jarassignment.ProgressUtils.hideDialog
import com.parthdesai1208.jarassignment.ProgressUtils.showProgressDialog
import com.parthdesai1208.jarassignment.databinding.FragmentScreen2Binding
import com.parthdesai1208.jarassignment.databinding.ItemFragmentScreen2Binding
import com.parthdesai1208.jarassignment.model.UnSplashModel
import com.parthdesai1208.jarassignment.model.remote.resources.Resource
import com.parthdesai1208.jarassignment.viewModel.Screen2FragmentViewModel

class Screen2Fragment : Fragment() {
    private lateinit var binding: FragmentScreen2Binding
    private val viewModel by lazy { viewModels<Screen2FragmentViewModel>() }
    private var page = 1
    private var lm: StaggeredGridLayoutManager? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScreen2Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()

        if (requireContext().isInternetAvailable(binding.root, binding.root)) {
            callAPI()
        }

        binding.imgBackArrow.setOnClickListener { requireActivity().onBackPressed() }

        binding.rcvScreen2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPositions =
                    lm?.findLastVisibleItemPositions(null)
                lastVisibleItemPositions?.let {
                    if (getLastVisibleItem(it) == viewModel.value.imageLiveList.value?.size.orOne() - 1) {
                        page += 1
                        callAPI()
                    }
                }
            }
        })
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (position in lastVisibleItemPositions) {
            if (position > maxSize) {
                maxSize = position
            }
        }
        return maxSize
    }

    private fun callAPI() {
        viewModel.value.getImageCall(page).observe(viewLifecycleOwner, {
            when (it.state) {
                Resource.State.LOADING_MORE, Resource.State.LOADING -> showProgressDialog(
                    requireContext()
                )
                Resource.State.ERROR, Resource.State.NONE -> {
                    hideDialog()
                    val returnSnackBar: Snackbar? = view?.let { it1 ->
                        Snackbar.make(
                            it1,
                            it.exception?.localizedMessage.orEmpty(),
                            Snackbar.LENGTH_LONG
                        )
                    }
                    returnSnackBar?.context?.let { it1 ->
                        ContextCompat.getColor(
                            it1,
                            R.color.error
                        )
                    }
                        .let { it1 -> it1?.let { it2 -> returnSnackBar?.view?.setBackgroundColor(it2) } }
                    returnSnackBar?.show()
                }
                Resource.State.SUCCESS -> {
                    hideDialog()
                    it?.data?.let { it1 -> viewModel.value.setImageLiveList(it1) }
                }
            }
        })
    }

    private fun setAdapter() {
        LiveAdapter(viewModel.value.imageLiveList, BR.item)
            .map<UnSplashModel, ItemFragmentScreen2Binding>(R.layout.item_fragment_screen2) {
                onBind {

                    val bind = it.binding
                    val item = bind.item

                    item?.let { it1 ->
                        bind.imgUnSplash.loadImage(it1.urls.full, R.drawable.ic_jar_logo)
                    }
                }
            }.into(binding.rcvScreen2)
        lm = binding.rcvScreen2.layoutManager as StaggeredGridLayoutManager

    }
}