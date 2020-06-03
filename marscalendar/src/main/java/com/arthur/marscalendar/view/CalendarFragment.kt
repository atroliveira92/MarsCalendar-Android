package com.arthur.marscalendar.view


import com.arthur.marscalendar.util.OnSnapPositionChangeListener
import com.arthur.marscalendar.util.SnapOnScrollListener
import com.arthur.marscalendar.view.daypicker.DayPickerAdapter
import com.arthur.marscalendar.view.daypicker.OnDayPickerListener
import com.arthur.marscalendar.view.daypicker.WeeksAdapter
import com.arthur.marscalendar.view.horizontal_picker.HorizontalPickerAdapter
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.arthur.marscalendar.R
import com.arthur.marscalendar.model.*
import com.arthur.marscalendar.util.attachSnapHelperWithListener
import com.arthur.marscalendar.util.scrollToLinearSnapPosition
import kotlinx.android.synthetic.main.calendar_view.*

public class CalendarFragment: Fragment(), OnDayPickerListener {

    private lateinit var monthAdapter: HorizontalPickerAdapter
    private lateinit var yearAdapter: HorizontalPickerAdapter
    private lateinit var daysAdapter: DayPickerAdapter

    private var selectedDate: Date? = null
    private var disableDates: ArrayList<Date>? = null

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0

    private var listener:OnCalendarListener? = null

    companion object {
        const val NUMBER_OF_ROWS = 7
        const val START_YEAR = "start_year"
        const val END_YEAR = "end_year"
        const val DATE = "date"
        const val DISABLED_DATES = "disabled_dates"

        fun newInstance(startYear: Int, endYear: Int, date: Date?, disableDates: ArrayList<Date>?): CalendarFragment {
            val fragment = CalendarFragment()
            val args = Bundle()
            args.putInt(START_YEAR, startYear)
            args.putInt(END_YEAR, endYear)
            args.putParcelable(DATE, date)
            args.putParcelableArrayList(DISABLED_DATES, disableDates?: ArrayList<Date>() as ArrayList<out Parcelable>)
            fragment.arguments = args
            return fragment
        }
    }

    fun setListener(listener: OnCalendarListener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.calendar_view, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                init(view.width)
            }
        })
    }

    private fun init(width: Int) {
        val margin = 8 / resources.displayMetrics.density

        val cardWidth = (width - margin).div(3)

        val startYear = arguments?.getInt(START_YEAR) ?: Year.getMinYear()
        val endYear = arguments?.getInt(END_YEAR) ?: Year.getCurrentYear()

        disableDates = arguments?.getParcelableArrayList(DISABLED_DATES)
        selectedDate = arguments?.getParcelable(DATE)
        selectedYear = selectedDate?.year ?: startYear
        selectedMonth = selectedDate?.month ?: Month.JANUARY

        setupYear(cardWidth.toInt(), startYear, endYear)

        setupMonth(cardWidth.toInt())

        setupDay(width)

        setupWeek(width)
    }

    private fun setupMonth(cardWidth: Int) {
        mRecyclerViewMonth.setPadding(cardWidth, 0, cardWidth, 0)

        val snapHelperMonth = LinearSnapHelper()
        snapHelperMonth.attachToRecyclerView(mRecyclerViewMonth)

        mRecyclerViewMonth.setHasFixedSize(true)
        monthAdapter = HorizontalPickerAdapter(Month.loadMonths(), cardWidth)
        mRecyclerViewMonth.adapter = monthAdapter
        mRecyclerViewMonth.attachSnapHelperWithListener(snapHelperMonth, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    selectedMonth = monthAdapter.getValue(position)
                    val array = Month.loadDaysOfMonth(selectedYear, selectedMonth, selectedDate, disableDates)
                    daysAdapter.update(array)
                }
            })

        mRecyclerViewMonth.scrollToLinearSnapPosition(monthAdapter.getPosition(selectedMonth), snapHelperMonth)
    }

    private fun setupYear(cardWidth: Int, startYear: Int, endYear: Int) {
        mRecyclerViewYear.setPadding(cardWidth, 0, cardWidth, 0)

        val snapHelperYear = LinearSnapHelper()
        snapHelperYear.attachToRecyclerView(mRecyclerViewYear)

        mRecyclerViewYear.setHasFixedSize(true)
        yearAdapter = HorizontalPickerAdapter(Year.loadYears(startYear, endYear), cardWidth)
        mRecyclerViewYear.adapter = yearAdapter
        mRecyclerViewYear.attachSnapHelperWithListener(snapHelperYear, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE, object :
            OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                selectedYear = yearAdapter.getValue(position)
                val array = Month.loadDaysOfMonth(selectedYear, selectedMonth, selectedDate, disableDates)
                daysAdapter.update(array)
            }
        })
        mRecyclerViewYear.scrollToLinearSnapPosition(yearAdapter.getPosition(selectedYear), snapHelperYear)
    }

    private fun setupDay(screenWidth: Int) {
        val array = Month.loadDaysOfMonth(selectedYear, selectedMonth, selectedDate, disableDates)

        val size = screenWidth / NUMBER_OF_ROWS

        mRecyclerViewDays.setHasFixedSize(false)
        daysAdapter = DayPickerAdapter(array, size, this)
        mRecyclerViewDays.layoutManager = GridLayoutManager(activity, 7)
        mRecyclerViewDays.adapter = daysAdapter
    }

    private fun setupWeek(screenWidth: Int) {
        val size =screenWidth / NUMBER_OF_ROWS

        mRecyclerViewWeek.setHasFixedSize(false)
        mRecyclerViewWeek.adapter = WeeksAdapter(Week.loadWeeks(), size)
    }

    override fun onClickDay(day: Day) {
        selectedDate = Date(selectedYear, selectedMonth, day.value)

        listener?.onSelectedDate(selectedDate!!)
    }
}