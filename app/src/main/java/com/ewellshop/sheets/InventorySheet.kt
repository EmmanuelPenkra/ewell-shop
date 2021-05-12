package com.ewellshop.sheets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.ewellshop.Item
import com.ewellshop.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.sheet_inventory.view.*

class InventorySheet(context: Context) : BottomSheetDialogFragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.sheet_inventory, container, false)
        v.cancelIV.setOnClickListener { dismiss() }
        v.editB.setOnClickListener {
            dismiss()
            startActivity(Intent(context, Item::class.java))
        }
        return v
    }
}