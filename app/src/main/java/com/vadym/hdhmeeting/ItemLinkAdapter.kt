package com.vadym.hdhmeeting

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class ItemLinkAdapter (
    private val itemLinkList: List<ItemLinkEntity>,
    private val database: SqliteDatabase
) : RecyclerView.Adapter<ItemLinkAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context).inflate(R.layout.item_link_card, parent, false)
    )

    override fun getItemCount(): Int {
        return itemLinkList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.apply {
            val currentItem = itemLinkList[position]
            currLinkTitle.text = currentItem.linkTitle
            currDays.text = if (currentItem.days!!.size > 1) currentItem.days?.joinToString(", ") else currentItem.days?.joinToString("")
            currTime.text = currentItem.time ?: "06:00"

            scheduleLinkOpening(itemView.context, currentItem)


            itemView.setOnClickListener {
                openUrlByHandle(it.context, currentItem.linkUrl.toString())
            }

            itemView.setOnLongClickListener {
                editItemFrame.visibility = View.VISIBLE
                true
            }

            goBack.setOnClickListener {
                editItemFrame.visibility = View.GONE
            }

            deleteItem.setOnClickListener {
                database.deleteLink(currentItem.linkID)
                (itemView.context as Activity).recreate()
            }

            editItem.setOnClickListener {  }

        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleLinkOpening(context: Context, item: ItemLinkEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, OpenUrlReceiver::class.java).apply {
            putExtra("url", item.linkUrl)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        item.days?.forEach { day ->
            val calendar = Calendar.getInstance().apply {
                val currentDay = get(Calendar.DAY_OF_WEEK)
                val targetDay = getDayOfWeekFromString(day)
                val daysUntilTarget = (targetDay - currentDay + 7) % 7
                add(Calendar.DAY_OF_YEAR, daysUntilTarget)

                val (hour, minute) = item.time?.split(":")?.map { it.toInt() } ?: listOf(0, 0)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun getDayOfWeekFromString(day: String): Int {
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

        val deleteItem = view.findViewById<ImageView>(R.id.delete_item)
        val editItem = view.findViewById<ImageView>(R.id.edit_item)
        val editItemFrame = view.findViewById<FrameLayout>(R.id.edit_card_frame)
        val goBack = view.findViewById<ImageView>(R.id.go_back)

    }
}