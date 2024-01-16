package com.einz.solnetcs.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ErrorDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(activity)
        // Retrieve the error message from the fragment's arguments
        val message = arguments?.getString("message") ?: "Unknown error"

        // Set the message and the button to dismiss the dialog
        builder.setMessage(message)
            .setPositiveButton("OK") { dialog, id ->
                // User acknowledged the error message
            }

        // Create the AlertDialog object and return it
        return builder.create()
    }
}
