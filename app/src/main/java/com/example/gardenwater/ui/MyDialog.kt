package com.example.gardenwater.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.gardenwater.R

class MyDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Ordinary title)))")
                .setMessage("Get deal with the button color!")
                .setIcon(R.drawable.cloudy)
                .setPositiveButton("Ok, gonna solve this problem soon") {
                        dialog, id ->  dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}