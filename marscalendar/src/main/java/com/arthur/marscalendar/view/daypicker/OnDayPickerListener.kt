package com.arthur.marscalendar.view.daypicker

import com.arthur.marscalendar.model.Day

interface OnDayPickerListener {
    fun onClickDay(day: Day)
}