package com.ewellshop.popups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.ewellshop.R
import com.ewellshop.helpers.Actions
import kotlinx.android.synthetic.main.popup_image.view.*
import kotlinx.android.synthetic.main.popup_image.view.cancelIV
import kotlinx.android.synthetic.main.sheet_inventory.view.*

class ImagePopup(private val url: String, private val changeClicked: (() -> Unit)? = null):
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.popup_image, container, false)
        v.cancelIV.setOnClickListener { dismiss() }

        if (url.isNotEmpty())
            Actions.showImage(url, v.profileIV)

        changeClicked?.let { cc ->
            v.changeBtn.isVisible = true
            v.changeBtn.setOnClickListener {
                cc.invoke()
            }
        }

        return v
    }
}