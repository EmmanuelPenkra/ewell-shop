package com.ewellshop.sheets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.ewellshop.R
import com.ewellshop.helpers.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.sheet_inventory.view.*

class InventorySheet(private val item: Item, private val imageClicked: ((String) -> Unit),
                     private val changeState: ((InventorySheet) -> Unit)) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.sheet_inventory, container, false)
        v.cancelIV.setOnClickListener { dismiss() }

        item.run {
            Actions.showImage(image, v.imageIV)
            v.imageIV.setOnClickListener {
                imageClicked(image)
            }

            v.nameTV.text = name
            v.priceTV.text = "${price.currency()} JMD"

            when (state){
                XBusinessInventory.AVAILABLE -> {
                    v.conditionB.text = "Sold Out"
                    v.conditionB.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                }
                XBusinessInventory.UNAVAILABLE -> {
                    v.conditionB.text = "Available"
                    v.conditionB.setBackgroundColor(resources.getColor(R.color.purple_200))
                }
                XBusinessInventory.REMOVED -> {
                    // SHOULDN'T GET HERE
                }
            }
        }

        v.conditionB.setOnClickListener {
            changeState.invoke(this)
        }

        v.editB.setOnClickListener {
            dismiss()
            startActivity(Intent(context, com.ewellshop.Item::class.java).apply {
                putExtra("item", item)
                putExtra("isUpdate", true)
            })
        }
        return v
    }
}