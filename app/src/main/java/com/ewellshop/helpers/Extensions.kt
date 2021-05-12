package com.ewellshop.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

operator fun JSONArray.iterator(): Iterator<JSONObject>
        = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

fun Date.daySuffix(): String {
    return when (SimpleDateFormat("d").format(this).toInt()){
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
}

fun String.formatNumber(mask: String): String {
    val numbers = this.numbers()
    var result = ""
    var index = 0

    for (ch in mask){
        if (index >= numbers.length) break
        if (ch == 'X'){
            result += numbers[index]
            index++
        }else {
            result += ch
        }
    }

    return result
}

fun String.numbers (): String {
    return this.filter { it.isDigit() }
}

fun String.formatEnum() = this.replace("_", " ")

fun String.capitalizeWords() = this.toLowerCase(Locale.ROOT).split(" ").joinToString(" ") { it.capitalize() }

fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun Int.ordinal(): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when (this % 100) {
        11, 12, 13 -> this.toString() + "th"
        else -> this.toString() + suffixes[this % 10]
    }
}

fun Context.openURL(url: String) {
    val uri: Uri = Uri.parse(url) // missing 'http://' will cause crashed
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}

fun Int.isTrue(): Boolean {
    return this > 0
}

inline fun <T> MutableList<T>.mapInPlace(mutator: (T) -> T) {
    val iterate = this.listIterator()
    while (iterate.hasNext()) {
        val oldValue = iterate.next()
        val newValue = mutator(oldValue)
        if (newValue !== oldValue) {
            iterate.set(newValue)
        }
    }
}