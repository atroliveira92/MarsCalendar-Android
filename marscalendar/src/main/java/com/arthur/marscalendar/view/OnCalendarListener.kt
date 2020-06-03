package com.arthur.marscalendar.view

import com.arthur.marscalendar.model.Date

interface OnCalendarListener {

    fun onSelectedDate(date: Date)
}