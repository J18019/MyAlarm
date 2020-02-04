package com.example.myalarmclock

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() , TimeAlertDialog.Listener,
    TimeAlertDialog.DatePickerFragment.OnDateSlectedListener, TimeAlertDialog.TimePickerFragment.OnTimeSlectedListener {

    override fun onSelected(year: Int, month: Int, date: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, date)
        dateText.text = android.text.format.DateFormat.format("yyyy/MM/dd", c)
    }

    override fun onSelected(hourOfDay: Int, minute: Int){
        timeText.text = "%1$02d:%2$02d".format(hourOfDay, minute)
    }

    /*override fun onPositiveClick(){
        finish()
    }*/

    override fun getUp() {
        finish()
        //Toast.makeText(this, "起きる時間がクリックされました", Toast.LENGTH_SHORT)
            //.show()
    }

    override fun snooze() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, 5)
        setAlarmManager(calendar)
        finish()
        //Toast.makeText(this, "あと5分がクリックされました", Toast.LENGTH_SHORT)
            //.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.getBooleanExtra("onReceive", false) == true){
            when{
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 ->{
                    setShowWhenLocked(true)
                    setTurnScreenOn(true)
                    val keyguardManager =
                        getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    keyguardManager.requestDismissKeyguard(this, null)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->{
                    window.addFlags(
                        FLAG_TURN_SCREEN_ON or FLAG_SHOW_WHEN_LOCKED
                    )
                    val keyguardManager =
                        getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    keyguardManager.requestDismissKeyguard(this, null)
                }
                else ->
                    window.addFlags(
                        FLAG_TURN_SCREEN_ON or
                                FLAG_SHOW_WHEN_LOCKED or FLAG_DISMISS_KEYGUARD
                    )
            }
            val dialog = TimeAlertDialog()
            dialog.show(supportFragmentManager, "alert_dialog")
        }

        setContentView(R.layout.activity_main)

        setAlarm.setOnClickListener{
            val date = "${dateText.text} ${timeText.text}".toDate()
            when {
                date != null -> {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    setAlarmManager(calendar)
                    Toast.makeText(
                        this, "アラームをセットしました",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        this,"日付の形式が正しくありません",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            /*val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.SECOND, 5)
            setAlarmManager(calendar)*/
        }

        cancelAlarm.setOnClickListener {
            cancelAlarmManager()
        }

        dateText.setOnClickListener {
            val dialog = TimeAlertDialog.DatePickerFragment()
            dialog.show(supportFragmentManager, "date_dialog")
        }

        timeText.setOnClickListener {
            val dialog = TimeAlertDialog.TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
        }

    }

    private fun setAlarmManager(calendar: Calendar){
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmBroadcastReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->{
                val info = AlarmManager.AlarmClockInfo(
                    calendar.timeInMillis, null)
                am.setAlarmClock(info, pending)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ->{
                am.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
            }
            else ->{
                am.set(AlarmManager.RTC_WAKEUP,calendar.timeInMillis, pending)
            }
        }
    }
    private fun cancelAlarmManager(){
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        am.cancel(pending)
    }

    private fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"):Date?{
        return try{
            SimpleDateFormat(pattern).parse(this)
        } catch (e: IllegalArgumentException){
            return null
        } catch (e: ParseException){
            return null
        }
    }
}
