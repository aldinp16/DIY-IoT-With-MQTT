package dev.aldi.diyiotwithmqtt

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dev.aldi.diyiotwithmqtt.control.AddControlButtonActivity

class AddControlDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arrayOf("Switch", "Button", "Slider", "Form")
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Control List")
                .setItems(items,
                    DialogInterface.OnClickListener { _, which ->
                        when (items[which]) {
                            "Button" -> startActivity(Intent(activity, AddControlButtonActivity::class.java))
                        }
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}