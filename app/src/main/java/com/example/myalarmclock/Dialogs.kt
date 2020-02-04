package com.example.myalarmclock

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.io.FileDescriptor
import java.io.PrintWriter
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.util.*

class TimeAlertDialog : DialogFragment(){
    interface Listener{
        fun getUp()
        fun snooze()
    }
    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context){
            is Listener -> listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("時間になりました！")
        builder.setPositiveButton("起きる") { dialog, which ->
            listener?.getUp()
        }
        builder.setPositiveButton("あと5分"){ dialog, which ->
            listener?.snooze()
        }
        return builder.create()
    }

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener{

        interface OnDateSlectedListener{
            fun onSelected(year: Int, month: Int, date: Int)
        }

        private var listener: OnDateSlectedListener? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            when (context){
                is OnDateSlectedListener -> listener = context
            }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val date = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireActivity(), this, year, month, date)
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            listener?.onSelected(year, month, dayOfMonth)
        }
    }

    class TimePickerFragment : DialogFragment(),TimePickerDialog.OnTimeSetListener{

        interface OnTimeSlectedListener{
            fun onSelected(hourOfDay: Int, minute: Int)
        }

        private var listener: OnTimeSlectedListener? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            when(context){
                is OnTimeSlectedListener -> listener = context
            }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            return TimePickerDialog(context, this, hour, minute, true)
        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            listener?.onSelected(hourOfDay, minute)
        }
    }
}
