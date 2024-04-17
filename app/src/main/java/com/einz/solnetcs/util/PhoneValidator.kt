package com.einz.solnetcs.util

import android.content.Context
import android.widget.Toast

fun phoneValidator(context: Context, phoneNumber: String): String {
    // Regex pattern to match Indonesian phone numbers with more flexibility
    val regexPattern = """^(?:\+62|62|0)(\d{9,12})$""".toRegex()

    // Check if the phone number matches the expected pattern
    val matchResult = regexPattern.find(phoneNumber)

    if(matchResult == null) {
        Toast.makeText(context, "Nomor telpon tidak valid!", Toast.LENGTH_SHORT).show()
        return ""
    }
    else{
        return "+62${matchResult.groupValues[1]}"
    }
}
