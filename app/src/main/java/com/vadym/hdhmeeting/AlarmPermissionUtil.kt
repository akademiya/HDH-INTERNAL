package com.vadym.hdhmeeting

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

object AlarmPermissionUtil {
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            /** For older versions, assume the app can schedule alarms since no permission is required */
            true
        }
    }

    fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlertDialog.Builder(context).apply {
                setTitle("Permission Required")
                setMessage("This app requires the SCHEDULE_EXACT_ALARM permission to schedule alarms accurately.")
                setPositiveButton("OK") { _, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }
    }
}