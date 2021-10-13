package com.codetest.todo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.format.DateFormat
import com.codetest.todo.app.BaseApplication
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utility {
    @Suppress("DEPRECATION")
    fun isInternetAvailable(): Boolean {
        var result = false
        val cm = BaseApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }


    fun getFormattedDate(timestamp:Long?) :String {
        timestamp ?: return  ""
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp

        return DateFormat.format("dd/MM/yyyy", calendar).toString()
    }

    fun getDisplayFormattedTime(input:String) :String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        try {
            val date: Date? = inputFormat.parse(input)
            date ?: return ""
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = date.time
            return DateFormat.format("hh:mm aa", calendar).toString()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getFormattedTime(hour:Int,minute:Int) :String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        return DateFormat.format("HH:mm", calendar).toString()
    }

}