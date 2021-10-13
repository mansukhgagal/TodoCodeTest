package com.codetest.todo.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.codetest.todo.utils.Constants
import com.codetest.todo.utils.Constants.KEY_ALARM_ID
import com.codetest.todo.utils.Utility
import timber.log.Timber
import java.util.*


class AlarmWork(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun doWork(): Result {
        inputData.apply {
            val alarmId = getInt(KEY_ALARM_ID,-1)
            val alarmTime = getString(Constants.KEY_TIME)
            val alarmDate = getLong(Constants.KEY_DATE,0)
            val alarmType = getInt(Constants.KEY_TYPE,-1)

            alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val broadcastIntent = Intent(applicationContext, TodoAlarmBroadcastReceiver::class.java)
            broadcastIntent.putExtra(KEY_ALARM_ID, alarmId)
            alarmIntent = PendingIntent.getBroadcast(
                applicationContext,
                0,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Set the alarm to start at specific time.
            val calendar: Calendar = Calendar.getInstance().apply {
                Utility.getHoursAndMinute(alarmTime)?.let {
                    set(Calendar.HOUR_OF_DAY, it.first)
                    set(Calendar.MINUTE, it.second)
                }
            }

            when (alarmType) {
                Constants.TYPE_DAILY -> {
                    alarmMgr?.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        alarmIntent
                    )
                }
                Constants.TYPE_WEEKLY -> {
                    calendar.timeInMillis = alarmDate
                    alarmMgr?.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        (AlarmManager.INTERVAL_DAY * 7),
                        alarmIntent
                    )
                }
                else -> {
                    //alarm not allowed
                }
            }
            Timber.d("$alarmId Alarm set ${calendar.timeInMillis}")
        }
        return Result.success()
    }
}