package wikibook.learnandroid.servicestudy

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import kotlin.random.Random

// 브로드캐스트 메시지를 수신하는 클래스
// (1)
class BroadcastReceiverForService(val service: MyStartedService) : BroadcastReceiver() {
    // (2)
    override fun onReceive(context: Context?, intent: Intent?) {
        // (3)
        if(intent?.action == "GENERATE_RANDOM_NUMBER") {
            // (4)
            val broadcastIntent = Intent("SEND_RANDOM_NUMBER")
            broadcastIntent.putExtra("num", service.getRandomNum(service.from ?: 0, service.until ?: 100))

            // (5)
            context?.sendBroadcast(broadcastIntent)
        }
    }
}


class MyStartedService : Service() {
    var from : Int? = null
    var until : Int? = null

    // (1)
    lateinit var broadcastReceiver : BroadcastReceiver

    // (2)
    override fun onBind(intent: Intent?): IBinder? = null

    // (3)
    override fun onCreate() {
        super.onCreate()
        Log.d("mytag", "service : onCreate")

        // (4)
        // BroadcastReceiverForService 클래스는 직접 정의할 클래스로 MyStartedService 클래스 코드 작성이 마무리 된 후 구현할 예정
        broadcastReceiver = BroadcastReceiverForService(this)

        // (5)
        val filter = IntentFilter()
        filter.addAction("GENERATE_RANDOM_NUMBER")

        // (6)
        registerReceiver(broadcastReceiver, filter)
    }

    // (7)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("mytag", "service : onStartCommand")

        // (8)
        from = intent?.getIntExtra("from", 0)
        until = intent?.getIntExtra("until", 100)

        // (9)
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("mytag", "service : onDestroy")

        // (10)
        unregisterReceiver(broadcastReceiver)
    }

    // (11)
    fun getRandomNum(from : Int, until : Int) = Random.nextInt(from, until)
}
