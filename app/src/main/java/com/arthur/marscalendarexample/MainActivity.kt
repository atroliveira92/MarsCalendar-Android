package com.arthur.marscalendarexample

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.arthur.marscalendar.model.Date
import com.arthur.marscalendar.view.CalendarFragment
import com.arthur.marscalendar.view.OnCalendarListener

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnCalendarListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarFragment = CalendarFragment.newInstance(2012, 2020, Date(2020, 7, 13), null)
        calendarFragment.setListener(this)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.mFrameLayoutHolder, calendarFragment, "calendar")
            .commit()
    }

    override fun onSelectedDate(date: Date) {
        Toast.makeText(this, "year: ${date.year} month: ${date.month} day: ${date.day}", Toast.LENGTH_LONG).show()
    }
}
