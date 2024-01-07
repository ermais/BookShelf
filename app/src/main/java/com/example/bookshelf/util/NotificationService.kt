package com.example.bookshelf.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bookshelf.R
import com.example.bookshelf.ui.downloads.DownloadsFragment
import com.example.bookshelf.ui.main.MainActivity

class NotificationService {

    companion object {

        val NOTIFICATION_ID = 789
        val CHANNEL_ID = "download_book_channel"

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val POST_NOTIFICATION = android.Manifest.permission.POST_NOTIFICATIONS

        fun builder(
            context: Context,
            max: Int,
            value: Int,
            title: String
        ): NotificationCompat.Builder {
            val intent = Intent(context, DownloadsFragment::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setProgress(max, value, false)
                .setSmallIcon(R.drawable.ic_download)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.download_book_channel_name)
                val descriptionChannel =
                    context.getString(R.string.download_book_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    name,
                    importance
                ).apply {
                    description = descriptionChannel
                }
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE)
                            as NotificationManager

                notificationManager.createNotificationChannel(channel)
            }
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun notify(context: Context, builder: NotificationCompat.Builder) {
            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context, android.Manifest.permission.POST_NOTIFICATIONS
                    ) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission(context as MainActivity, POST_NOTIFICATION, 899)
                    return@with
                }
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}