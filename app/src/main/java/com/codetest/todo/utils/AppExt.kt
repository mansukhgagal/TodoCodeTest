package com.codetest.todo.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.Lifecycle
import kotlin.math.roundToInt

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Context.convertDpToPixel(dp: Float): Int {
    return (dp * (resources
        .displayMetrics.densityDpi  / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun AppCompatActivity.isAlive() : Boolean = lifecycle.currentState != Lifecycle.State.DESTROYED