package com.codetest.todo.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.codetest.todo.R
import com.codetest.todo.ui.create.TodoModel
import com.codetest.todo.ui.create.TodoRepository
import com.codetest.todo.ui.main.MainActivity
import com.codetest.todo.utils.Constants
import com.codetest.todo.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.codetest.todo.utils.Constants.NOTIFICATION_CHANNEL_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TodoAlarmBroadcastReceiver : BaseBroadcastReceiver() {

    @Inject lateinit var todoRepository: TodoRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("Alarm received...!")
        val alarmId = intent.getIntExtra(Constants.KEY_ALARM_ID,-1)
        CoroutineScope(Dispatchers.IO).launch {
           val result = todoRepository.getTodoById(alarmId)
            withContext(Dispatchers.Main) {
                createNotification(context,result)
            }
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(context: Context, result:TodoModel?) {
        Timber.d("Alarm $result")
        result ?: return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).also {
                it.action = Constants.ACTION_ALARM
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_time)
            .setContentTitle(result.title)
            .setContentText(result.description)
            .setSubText(result.time)
            .setContentIntent(pendingIntent)

        notificationManager.notify(1,notificationBuilder.build())

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}