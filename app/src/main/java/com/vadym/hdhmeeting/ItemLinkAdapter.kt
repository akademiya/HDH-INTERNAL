package com.vadym.hdhmeeting

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class ItemLinkAdapter (
    private val itemLinkList: List<ItemLinkEntity>,
    private val database: SqliteDatabase,
    private val onMoveItemTouch: (viewHolder: VH) -> Unit
) : RecyclerView.Adapter<ItemLinkAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context).inflate(R.layout.item_link_card, parent, false)
    )

    override fun getItemCount(): Int {
        return itemLinkList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.apply {
            val sharedPreferences = itemView.context.getSharedPreferences("AppPreferences", MODE_PRIVATE)
            val editedSharedPref = itemView.context.getSharedPreferences("Edit", MODE_PRIVATE)
            val isNotification = sharedPreferences.getBoolean("notificationSwitchState", false)
            var isEdited = editedSharedPref.getBoolean("isEdited", false)
            val currentItem = itemLinkList[position]
            currLinkTitle.text = currentItem.linkTitle
            currDays.text = if (currentItem.days!!.size > 1) currentItem.days?.joinToString(", ") else currentItem.days?.joinToString("")
            currTime.text = currentItem.time ?: "06:00"
            linkNotification.isChecked = currentItem.notification == true

            if (isEdited && isNotification && linkNotification.isChecked) {
                scheduleLinkOpening(itemView.context, currentItem)
                isEdited = false
            }

            linkNotification.setOnCheckedChangeListener { _, isChecked ->
                currentItem.notification = isChecked
                database.updateNotificationStatus(currentItem.linkID, isChecked)

                if (isNotification) {
                    if (isChecked) {
                        scheduleLinkOpening(itemView.context, currentItem)
                    } else {
                        cancelScheduledNotification(itemView.context, currentItem)
                    }
                }

            }


            itemView.setOnClickListener {
                openUrlByHandle(it.context, currentItem.linkUrl.toString())
            }

            itemView.setOnLongClickListener {
                editItemFrame.visibility = View.VISIBLE
                isEdited = true
                true
            }

            ivMoveItem?.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    onMoveItemTouch(holder)
                }
                return@setOnTouchListener false
            }

            goBack.setOnClickListener {
                editItemFrame.visibility = View.GONE
            }

            deleteItem.setOnClickListener {
                database.deleteLink(currentItem.linkID)
                (itemView.context as Activity).recreate()
            }

            editItem.setOnClickListener {
                itemView.context.startActivity(Intent(itemView.context, CreateItemActivity::class.java).apply {
                    putExtra("editItem", currentItem)
                })
            }

        }
    }

    private fun cancelScheduledNotification(context: Context, item: ItemLinkEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        item.days?.forEach { day ->
            val dayOfWeek = getDayOfWeek(day)
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                item.linkID + dayOfWeek, // Unique requestCode
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }



    @SuppressLint("UnspecifiedImmutableFlag", "ScheduleExactAlarm")
    private fun scheduleLinkOpening(context: Context, item: ItemLinkEntity) {
        if (!AlarmPermissionUtil.canScheduleExactAlarms(context)) {
            AlarmPermissionUtil.requestExactAlarmPermission(context)
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        item.days?.forEach { day ->
            val dayOfWeek = getDayOfWeek(day)
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
                val timeParts = item.time?.split(":")
                if (timeParts != null && timeParts.size == 2) {
                    set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    set(Calendar.MINUTE, timeParts[1].toInt())
                } else {
                    set(Calendar.HOUR_OF_DAY, 6) // Default to 06:00
                    set(Calendar.MINUTE, 0)
                }
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("linkItem", item)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                item.linkID + dayOfWeek, // Unique requestCode for each alarm
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }


    private fun getDayOfWeek(day: String): Int {
        return when (day.lowercase()) {
            "sunday" -> Calendar.SUNDAY
            "monday" -> Calendar.MONDAY
            "tuesday" -> Calendar.TUESDAY
            "wednesday" -> Calendar.WEDNESDAY
            "thursday" -> Calendar.THURSDAY
            "friday" -> Calendar.FRIDAY
            "saturday" -> Calendar.SATURDAY
            else -> throw IllegalArgumentException("Invalid day: $day")
        }
    }


    private fun openUrlByHandle(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Toast.makeText(context, "No application found to open this link", Toast.LENGTH_SHORT).show()
        }
    }




    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val currLinkTitle = view.findViewById<TextView>(R.id.titleOfItem)
        val currDays = view.findViewById<TextView>(R.id.days)
        val currTime = view.findViewById<TextView>(R.id.time)
        val linkNotification = view.findViewById<SwitchCompat>(R.id.link_notification)

        val deleteItem = view.findViewById<ImageView>(R.id.delete_item)
        val editItem = view.findViewById<ImageView>(R.id.edit_item)
        val editItemFrame = view.findViewById<FrameLayout>(R.id.edit_card_frame)
        val goBack = view.findViewById<ImageView>(R.id.go_back)
        val ivMoveItem = view.findViewById<ImageView>(R.id.iv_move_item)

    }
}