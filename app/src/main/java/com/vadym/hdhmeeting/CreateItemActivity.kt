package com.vadym.hdhmeeting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast

class CreateItemActivity: BaseActivity() {
    private lateinit var db: SqliteDatabase
    private lateinit var listLinks: List<ItemLinkEntity>
    private lateinit var customLinkTitle: EditText
    private lateinit var customLink: EditText
    private var isEditItem: Boolean = false

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.item_link_create)
        val onSaveButtonClick = findViewById<Button>(R.id.saveButton)
        customLinkTitle = findViewById(R.id.customLinkTitle)
        customLink = findViewById(R.id.customLink)
        val timePicker = findViewById<TimePicker>(R.id.time_picker)
        timePicker.setIs24HourView(true)
        db = SqliteDatabase.getInstance(this)
        listLinks = db.listLinks()

        val editItem = intent?.getSerializableExtra("editItem") as? ItemLinkEntity
        editItem?.let {
            editItem(it)
            isEditItem = true
        }

        val daysCheckBoxes = listOf<CheckBox>(
            findViewById(R.id.sunday),
            findViewById(R.id.monday),
            findViewById(R.id.tuesday),
            findViewById(R.id.wednesday),
            findViewById(R.id.thursday),
            findViewById(R.id.friday),
            findViewById(R.id.saturday)
        )


        onSaveButtonClick.setOnClickListener {
            val isValid = validateRows(customLinkTitle.text.toString(), customLink.text.toString(), getSelectedDays(daysCheckBoxes), saveSelectedTime(timePicker))
            if (isValid) {
                saveDataToEntity(
                    linkTitle = customLinkTitle.text.toString(),
                    linkUrl = customLink.text.toString(),
                    days = getSelectedDays(daysCheckBoxes),
                    time = saveSelectedTime(timePicker)
                )
                if (!isEditItem) {
                    db.addLink(
                        ItemLinkEntity(
                            linkTitle = customLinkTitle.text.toString(),
                            linkUrl = customLink.text.toString(),
                            days = getSelectedDays(daysCheckBoxes),
                            time = saveSelectedTime(timePicker),
                            position = listLinks.lastIndex + 1
                        )
                    )
                } else {
                    db.updateLink(ItemLinkEntity(
                        linkID = editItem!!.linkID,
                        linkTitle = customLinkTitle.text.toString(),
                        linkUrl = customLink.text.toString(),
                        days = getSelectedDays(daysCheckBoxes),
                        time = saveSelectedTime(timePicker),
                        position = editItem.position
                    ))
                }
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }

    private fun editItem(item: ItemLinkEntity) {
        val days: List<String>? = item.days

        customLinkTitle.setText(item.linkTitle, TextView.BufferType.EDITABLE)
        customLink.setText(item.linkUrl, TextView.BufferType.EDITABLE)

        val daysCheckBoxes = listOf<CheckBox>(
            findViewById(R.id.sunday),
            findViewById(R.id.monday),
            findViewById(R.id.tuesday),
            findViewById(R.id.wednesday),
            findViewById(R.id.thursday),
            findViewById(R.id.friday),
            findViewById(R.id.saturday)
        )

        days?.forEach { day ->
            when (day) {
                DayOfWeek.SUNDAY.day -> daysCheckBoxes[0].isChecked = true
                DayOfWeek.MONDAY.day -> daysCheckBoxes[1].isChecked = true
                DayOfWeek.TUESDAY.day -> daysCheckBoxes[2].isChecked = true
                DayOfWeek.WEDNESDAY.day -> daysCheckBoxes[3].isChecked = true
                DayOfWeek.THURSDAY.day -> daysCheckBoxes[4].isChecked = true
                DayOfWeek.FRIDAY.day -> daysCheckBoxes[5].isChecked = true
                DayOfWeek.SATURDAY.day -> daysCheckBoxes[6].isChecked = true
            }
        }

    }

    private fun getSelectedDays(checkBoxes: List<CheckBox>): List<String> {
        return checkBoxes.filter { it.isChecked }.mapNotNull { checkBox ->
            when(checkBox.id) {
                R.id.sunday -> DayOfWeek.SUNDAY.day
                R.id.monday -> DayOfWeek.MONDAY.day
                R.id.tuesday -> DayOfWeek.TUESDAY.day
                R.id.wednesday -> DayOfWeek.WEDNESDAY.day
                R.id.thursday -> DayOfWeek.THURSDAY.day
                R.id.friday -> DayOfWeek.FRIDAY.day
                R.id.saturday -> DayOfWeek.SATURDAY.day
                else -> null
            }
        }
    }

    private fun saveSelectedTime(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        return "$hour:${minute.toString().padStart(2, '0')}"
    }

    private fun validateRows(linkTitle: String, linkUrl: String, days: List<String>, time: String): Boolean {
        fun showErrorMessage(error: Error) {
            Toast.makeText(this, error.textError, Toast.LENGTH_SHORT).show()
        }

        return when {
            linkTitle.isEmpty() -> {
                showErrorMessage(Error.EMPTY_TITLE)
                false
            }
            linkUrl.isEmpty() -> {
                showErrorMessage(Error.EMPTY_URL)
                false
            }
            days.isEmpty() -> {
                showErrorMessage(Error.EMPTY_DAY)
                false
            }
            time.isEmpty() -> {
                showErrorMessage(Error.EMPTY_TIME)
                false
            }
            else -> true
        }
    }

    private fun saveDataToEntity(linkTitle: String, linkUrl: String, days: List<String>, time: String) {
        ItemLinkEntity(
            linkTitle = linkTitle,
            linkUrl = linkUrl,
            days = days,
            time = time
        )
    }

    enum class DayOfWeek(val day: String) {
        SUNDAY("sunday"),
        MONDAY("monday"),
        TUESDAY("tuesday"),
        WEDNESDAY("wednesday"),
        THURSDAY("thursday"),
        FRIDAY("friday"),
        SATURDAY("saturday")
    }

    enum class Error(val textError: String) {
        EMPTY_TITLE("Enter the Title of link"),
        EMPTY_URL("Enter the valid URL"),
        EMPTY_DAY("Select minimum one day, when this link must be open"),
        EMPTY_TIME("Set the time")
    }
}