package wikibook.learnandroid.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class PomodoroActivity : AppCompatActivity() {
    // (1)
    lateinit var remainTime : TextView
    lateinit var receiver : BroadcastReceiver
    // 뷰 객체 속성을 추가
    lateinit var remainProgress : ProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pomodoro_activity)

        // 뷰 객체 초기화
        remainProgress = findViewById<ProgressView>(R.id.remain_progress)

        // (1)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // (2)
        toolbar.findViewById<ImageButton>(R.id.to_setting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        // (3)
        window.statusBarColor = Color.rgb(255, 0, 0)

        // (1)
        remainTime = findViewById<TextView>(R.id.remain_time)

        findViewById<Button>(R.id.pomodoro_timer_start).setOnClickListener {
            // (3)
            val cancelIntent = Intent(PomodoroService.ACTION_ALARM_CANCEL)
            sendBroadcast(cancelIntent)

            val dialog = PomodoroTimeSelectFragment()
            dialog.show(supportFragmentManager, "pomodoro_time_select_dialog")

            // (3)
            remainTime.setTextColor(getColor(R.color.colorPrimary))
        }

        findViewById<Button>(R.id.pomodoro_timer_cancel).setOnClickListener {
            val cancelIntent = Intent(PomodoroService.ACTION_ALARM_CANCEL)
            sendBroadcast(cancelIntent)

            // (2)
            remainTime.text = "-"

            // (3)
            remainTime.setTextColor(getColor(R.color.colorPrimary))
        }

        // 취소 버튼의 클릭 리스너 설정 코드 뒤에 설정 버튼의 클릭 리스너 코드를 추가
        findViewById<Button>(R.id.pomodoro_setting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action

                if (action == PomodoroService.ACTION_REMAIN_TIME_NOTIFY) {
                    // (4)
                    val remainInSec = intent.getLongExtra("count", 0) / 1000

                    // (5)
                    remainTime.text = "${remainInSec / 60}:${String.format("%02d", remainInSec % 60)}"

                    // 인텐트 객체에서 전달받은 알람 시간 정보를 추출
                    val delayInSec = intent.getIntExtra("delay", 0)
                    // 알람 시간과 남은 시간의 비율을 계산하고 100을 곱해서 0부터 100까지 범위의 최종 진행도 계산
                    remainProgress.progress = (remainInSec / delayInSec.toDouble()) * 100

                    // (6)
                    if(remainInSec <= 10) {
                        remainTime.setTextColor(getColor(R.color.colorAccent))
                    }
                }
            }
        }

        // (7)
        val filter = IntentFilter()
        filter.addAction(PomodoroService.ACTION_REMAIN_TIME_NOTIFY)
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()

        // (8)
        unregisterReceiver(receiver)
    }
}
