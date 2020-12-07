package wikibook.learnandroid.pomodoro

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class PomodoroService : Service() {
    // (1)
    companion object {
        val ALARM_CHANNEL_NAME = "뽀모도로 알람"
        val ACTION_ALARM_CANCEL = "wikibook.learnandroid.pomodoro.ACTION_ALARM_CANCEL"
        val ACTION_ALARM = "wikibook.learnandroid.pomodoro.ACTION_ALARM"

        // (1)
        val ACTION_REMAIN_TIME_NOTIFY = "wikibook.learnandroid.pomodoro.ACTION_SEND_COUNT"
    }

    // (2)
    lateinit var timer: Timer

    // (2)
    var delayTimeInSec: Int = 0
    var startTime: Long = 0
    var endTime: Long = 0

    // (3)
    lateinit var vibrator: Vibrator
    lateinit var receiver: BroadcastReceiver
    lateinit var alarmBroadcastIntent: PendingIntent

    // (1)
    lateinit var builder : NotificationCompat.Builder

    // (2)
    val dateFormatter = SimpleDateFormat("h:mm:ss")

    // 효과음을 출력하기 위해 사용할 SoundPool 객체를 추가
    lateinit var soundPool : SoundPool
    var soundId = 0

    // 배경음악을 출력하기 위해 사용할 MediaPlayer 객체를 추가
    lateinit var mediaPlayer : MediaPlayer

    // 볼륨 정보 저장용 속성을 추가
    var volume : Int = 0

    // (4)
    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // (5)
        delayTimeInSec = intent.getIntExtra("delayTimeInSec", 0)
        startTime = intent.getLongExtra("startTime", 0)
        endTime = startTime + (delayTimeInSec * 1000)

        // (1)
        val notifyMethod = intent.getStringExtra("notifyMethod")

        // 인텐트를 통해 전달받은 볼륨값을 이용해 volume 속성을 초기화
        volume = intent.getIntExtra("volume", 50)

        // (6)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationTimeInMS: Long = 1000 * 3

        // (2)
        soundPool = SoundPool.Builder().build()
        soundId = soundPool.load(this, R.raw.beep, 1)

        // (3)
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_music)
        // 미디어 플레이어를 생성한 후 볼륨을 설정
        mediaPlayer.setVolume((volume * 0.01).toFloat(), (volume * 0.01).toFloat())

        // (7)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // (8)
        alarmBroadcastIntent =
            PendingIntent.getBroadcast(this, 0, Intent(ACTION_ALARM), PendingIntent.FLAG_ONE_SHOT)

        // (9)
        val delay = 1000 * delayTimeInSec
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delay,
            alarmBroadcastIntent
        )

        // (10)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when (action) {
                    // (11)
                    ACTION_ALARM -> {
                        when(notifyMethod) {
                            // (1)
                            "vibration" -> {
                                // Log.d("mytag", "Vibrating...")
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(vibrationTimeInMS, VibrationEffect.DEFAULT_AMPLITUDE))
                                } else {
                                    vibrator.vibrate(vibrationTimeInMS)
                                }
                                stopSelf()
                            }
                            // (2)
                            "beep" -> {
                                soundPool.play(soundId, (volume * 0.01).toFloat(), (volume * 0.01).toFloat(), 1, 0, 1f)
                                stopSelf()
                            }
                            // (3)
                            "music" -> {
                                mediaPlayer.start()
                                cancelRemainTimeNotifyTimer()
                                // (4)
                                mediaPlayer.setOnCompletionListener {
                                    stopSelf()
                                }
                            }
                        }
                    }

                    // (12)
                    ACTION_ALARM_CANCEL -> stopSelf()
                    // 화면 상태가 변경될 때 타이머의 작동 여부를 조정할 분기 코드
                    Intent.ACTION_SCREEN_ON -> startRemainTimeNotifyTimer()
                    Intent.ACTION_SCREEN_OFF -> cancelRemainTimeNotifyTimer()
                }
            }
        }

        // (13)
        val filter = IntentFilter()
        filter.addAction(ACTION_ALARM)
        filter.addAction(ACTION_ALARM_CANCEL)

        // 단말기의 화면 상태 변경과 관련된 시스템 브로드캐스트 메시지 수신
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)

        // (14)
        registerReceiver(receiver, filter)

        // (15)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // (16)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                ALARM_CHANNEL_NAME,
                "뽀모도로 상태 알림 채널",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // (1)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(this, ALARM_CHANNEL_NAME)
        } else {
            builder = NotificationCompat.Builder(this)
        }

        // (2)
        val activityStartIntent = Intent(this, PomodoroActivity::class.java)
        val activityStartPendingIntent = PendingIntent.getActivity(this, 1, activityStartIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = builder
            // (3)
            .setContentTitle("${dateFormatter.format(Date(startTime))}부터 ${dateFormatter.format(Date(endTime))}까지")
            .setContentText("시작됨")
            .setSmallIcon(R.drawable.ic_tomato)
            .setOnlyAlertOnce(true)
            // (4)
            .setContentIntent(activityStartPendingIntent)
            .build()

        // (19)
        startForeground(1, notification)

        // (6)
        startRemainTimeNotifyTimer()

        // (20)
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        // 음악이 재생 중이라면 종료
        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        // 리소스 해제
        mediaPlayer.release()

        // (7)
        cancelRemainTimeNotifyTimer()

        // (21)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmBroadcastIntent)

        // (22)
        unregisterReceiver(receiver)
    }

    fun startRemainTimeNotifyTimer() {
        // (3)
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                // (4)
                val diff = ((endTime - System.currentTimeMillis()) / 1000) * 1000
                val i = Intent(ACTION_REMAIN_TIME_NOTIFY)
                i.putExtra("count", diff)
                // 알람 설정 시간 정보를 포함해서 전달하도록 수정
                i.putExtra("delay", delayTimeInSec)
                sendBroadcast(i)

                // 1초마다 알림 메시지의 내용을 갱신
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if(diff <= 0) {
                    // (1)
                    val notification = builder.setContentTitle("완료").setContentText("-").build()
                    notificationManager.notify(1, notification)
                    cancel()
                } else {
                    // (2)
                    val remainInSec = diff / 1000
                    val notification = builder.setContentText("남은 시간 : ${remainInSec / 60}:${String.format("%02d", remainInSec % 60)}").build()
                    notificationManager.notify(1, notification)
                }
            }
        }, 0, 1000)
    }

    // (5)
    fun cancelRemainTimeNotifyTimer() {
        timer?.cancel()
    }
}