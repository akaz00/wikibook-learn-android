package wikibook.learnandroid.pomodoro

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

// (1)
class PomodoroTimeSelectFragment : DialogFragment() {
    lateinit var timeSelectView : View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // (2)
        val builder = AlertDialog.Builder(context!!)

        // (3)
        timeSelectView = LayoutInflater.from(context!!).inflate(R.layout.pomodoro_time_select_dialog, null)
        val timeSelect = timeSelectView.findViewById<LinearLayout>(R.id.time_select)

        // (4)
        val listener = View.OnClickListener {
            val sec = it.tag.toString().toLong()
            startPomodoro(sec)
        }

        val times = activity?.getSharedPreferences(SettingFragment.SETTING_PREF_FILENAME, Context.MODE_PRIVATE)?.getString("preset_times", "5,10,15,20,25,30")

        // (5)
        times?.split(",")?.forEach {
            val time = it.trim()
            // (6)
            val btn = Button(activity)
            btn.setText("${time}분")
            // (7)
            btn.tag = "${time.toInt() * 60}"
            // (8)
            btn.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // (9)
            btn.setOnClickListener(listener)
            timeSelect.addView(btn)
        }

        // (10)
        builder.setView(timeSelectView)
            .setPositiveButton("시작"){ _, _ ->
                // (11)
                var time = timeSelectView.findViewById<EditText>(R.id.manual_time_select).text.toString().toLong()
                startPomodoro(time)
            }
            .setNegativeButton("취소"){ _, _ ->
                // (12)
                dismiss()
            }

        // (13)
        return builder.create()
    }

    // (14)
    private fun startPomodoro(delay : Long) {
        if(!(delay <= 0)) {
            activity?.let {
                val i = Intent(it, PomodoroService::class.java)
                i.putExtra("delayTimeInSec", delay.toInt())
                i.putExtra("startTime", System.currentTimeMillis())

                // 프리퍼런스를 통해 받아온 알람 방식과 볼륨값을 인텐트에 추가
                i.putExtra("notifyMethod", it.getSharedPreferences(SettingFragment.SETTING_PREF_FILENAME, Context.MODE_PRIVATE)?.getString("notify_method", "vibration"))
                i.putExtra("volume", it.getSharedPreferences(SettingFragment.SETTING_PREF_FILENAME, Context.MODE_PRIVATE)?.getInt("volume", 50))

                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    it.startForegroundService(i)
                } else {
                    it.startService(i)
                }

                dismiss()
            }
        }
    }
}
