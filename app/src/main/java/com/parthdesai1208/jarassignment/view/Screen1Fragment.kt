package com.parthdesai1208.jarassignment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.autoroutesante.belocum.liveadapter.LiveAdapter
import com.google.android.material.snackbar.Snackbar
import com.parthdesai1208.jarassignment.BR
import com.parthdesai1208.jarassignment.R
import com.parthdesai1208.jarassignment.databinding.FragmentScreen1Binding
import com.parthdesai1208.jarassignment.databinding.ItemFragmentScreen1Binding
import com.parthdesai1208.jarassignment.orOne
import com.parthdesai1208.jarassignment.orZero
import com.parthdesai1208.jarassignment.viewModel.Screen1FragmentViewModel


class Screen1Fragment : Fragment() {

    lateinit var binding: FragmentScreen1Binding
    private var shapeList: ArrayList<String> = ArrayList()
    private var lm: GridLayoutManager? = null
    private var positionToUndoList: ArrayList<Int> = ArrayList()

    private val viewModel by lazy { viewModels<Screen1FragmentViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScreen1Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()

        positionToUndoList.clear()
        shapeList.clear()
        for (i in 1..80) {
            shapeList.add("null")
        }
        viewModel.value.setShapeLiveList(shapeList)

        binding.btnAddCircle.setOnClickListener {
            circleRectangleOperation("circle")
        }

        binding.btnAddRectangle.setOnClickListener {
            circleRectangleOperation("rectangle")
        }
        binding.btnUndo.setOnClickListener {
            if (positionToUndoList.size > 0) {
                viewModel.value.updateShapeLiveList(
                    "null",
                    positionToUndoList[positionToUndoList.size - 1]
                )
                positionToUndoList.removeAt(positionToUndoList.size - 1)
            }
        }
        binding.imgBackArrow.setOnClickListener { requireActivity().onBackPressed() }

    }

    private fun circleRectangleOperation(shape: String) {
        if(lm?.findLastCompletelyVisibleItemPosition().orOne() + 1 == positionToUndoList.size){
            var returnSnackBar : Snackbar? = null
            returnSnackBar = view?.let { Snackbar.make(it, "oops,Room is full!!" , Snackbar.LENGTH_LONG) }
            returnSnackBar?.anchorView = binding.btnAddCircle
            returnSnackBar?.context?.let { ContextCompat.getColor(it, R.color.error) }?.let { returnSnackBar.view.setBackgroundColor(it) }
            returnSnackBar?.show()
            return
        }
        var randomPosition: Int
        do {
            randomPosition = (lm?.findFirstCompletelyVisibleItemPosition()
                .orZero()..lm?.findLastCompletelyVisibleItemPosition().orOne()).random()
        } while (positionToUndoList.contains(randomPosition))
        positionToUndoList.add(randomPosition)
        viewModel.value.updateShapeLiveList(shape, randomPosition)
    }

    private fun setAdapter() {
        LiveAdapter(viewModel.value.shapeLiveList, BR.item)
            .map<String, ItemFragmentScreen1Binding>(R.layout.item_fragment_screen1) {
                onBind {
                    it.adapterPosition
                    val bind = it.binding
                    val item = bind.item

                    item?.let { it1 ->
                        when (it1) {
                            "null" -> bind.imgShape.setImageDrawable(null)
                            "circle" -> bind.imgShape.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.circle
                                )
                            )
                            "rectangle" -> bind.imgShape.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.rectangle
                                )
                            )
                            else -> bind.imgShape.setImageDrawable(null)
                        }
                    }
                }
            }.into(binding.rcvScreen1)
        lm = binding.rcvScreen1.layoutManager as GridLayoutManager
    }


}