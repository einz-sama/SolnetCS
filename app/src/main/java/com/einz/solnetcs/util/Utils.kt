package com.einz.solnetcs.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.einz.solnetcs.data.model.FirebaseTimestamp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private const val DATETIME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    DATETIME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())


fun checkUsername(username: String): Boolean {
    return username.length >= 1
}

fun checkEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun checkPassword(password: String): Boolean {
    return password.length >= 8
}

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun formatDate(dateString: String): String {
    val input = ZonedDateTime.parse(dateString)
    val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    val output = dateTimeFormatter.format(input)
    return output
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun formatFirebaseTimestamp(firebaseTimestamp: FirebaseTimestamp?): String {
    firebaseTimestamp?.let {
        val date = it.toTimestamp().toDate() // Convert to Date object
        val format = SimpleDateFormat("MMMM dd HH:mm", Locale.getDefault())
        return format.format(date)
    }
    return "Unknown Date" // Return a default or error string if timestamp is null
}

fun formatPhoneNumber(phoneNumber: String): String {
    return when {
        phoneNumber.startsWith("08") -> phoneNumber.replaceFirst("08", "+628")
        phoneNumber.startsWith("8") -> "+62$phoneNumber"
        phoneNumber.startsWith("+628") -> phoneNumber
        else -> phoneNumber
    }
}