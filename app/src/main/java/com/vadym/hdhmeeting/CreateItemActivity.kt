package com.vadym.hdhmeeting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast

class CreateItemActivity: BaseActivity() {
    lateinit var db: SqliteDatabase
    lateinit var listLinks: List<ItemLinkEntity>

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.item_link_create)
        val onSaveButtonClick = findViewById<Button>(R.id.saveButton)
        val customLinkTitle = findViewById<EditText>(R.id.customLinkTitle)
        val customLink = findViewById<EditText>(R.id.customLink)
        val timePicker = findViewById<TimePicker>(R.id.time_picker)
        timePicker.setIs24HourView(true)
        db = SqliteDatabase.getInstance(this)
        listLinks = db.listLinks()

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
                db.addLink(
                    ItemLinkEntity(
                        linkTitle = customLinkTitle.text.toString(),
                        linkUrl = customLink.text.toString(),
                        days = getSelectedDays(daysCheckBoxes),
                        time = saveSelectedTime(timePicker),
                        position = listLinks.lastIndex + 1
                    )
                )
                startActivity(Intent(this, MainActivity::class.java))
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
        EMPTY_DAY("Select min one day, when this link must be open"),
        EMPTY_TIME("Set the time")
    }
}