package wikibook.learnandroid.servicestudy

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.Toast

// 액티비티 측에서 사용할 브로드캐스트 리시버 클래스를 정의
class BroadcastReceiverForActivity : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // (7)
        if(intent?.action == "SEND_RANDOM_NUMBER") {
            val num = intent.getIntExtra("num", -1)
            Toast.makeText(context, num.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}

// 시스템에서 발생하는 이벤트를 수신할 브로드캐스트 리시버 클래스
class SystemBroadcastMessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> Log.d("mytag", "충전 연결")
            Intent.ACTION_POWER_DISCONNECTED -> Log.d("mytag", "충전 연결 해제")
            Intent.ACTION_HEADSET_PLUG -> Log.d("mytag", "헤드셋 연결 상태 변경")
            Intent.ACTION_SCREEN_ON -> Log.d("mytag", "화면 켜짐")
            Intent.ACTION_SCREEN_OFF -> Log.d("mytag", "화면 꺼짐")
        }
    }
}

// (1)
class MainActivity : AppCompatActivity(), ServiceConnection {
    // (2)
    var myBinder: MyBoundService.MyBinder? = null

    // (5)
    lateinit var broadcastReceiver : BroadcastReceiver
    // (1)
    lateinit var systemBroadcastReceiver : BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // (3)
        val boundServiceIntent = Intent(this, MyBoundService::class.java)
        // (4)
        bindService(boundServiceIntent, this, Context.BIND_AUTO_CREATE)

        findViewById<Button>(R.id.random_num_from_bound_service).setOnClickListener {
            // (5)
            val randomNumber = myBinder?.service?.getRandomNum()
            Toast.makeText(this, randomNumber.toString(), Toast.LENGTH_SHORT).show()
        }

        // 버튼 리스너 관련 코드 및 브로드캐스트 리시버 관련 코드 추가
        findViewById<Button>(R.id.start_service).setOnClickListener {
            Toast.makeText(this, "서비스 시작", Toast.LENGTH_SHORT).show()

            // (1)
            val serviceIntent = Intent(this, MyStartedService::class.java)
            serviceIntent.putExtra("from", 1000)
            serviceIntent.putExtra("until", 2000)

            // (2)
            startService(serviceIntent)
        }

        findViewById<Button>(R.id.stop_service).setOnClickListener {
            Toast.makeText(this, "서비스 정지", Toast.LENGTH_SHORT).show()

            // (3)
            val serviceIntent = Intent(this, MyStartedService::class.java)
            stopService(serviceIntent)
        }

        findViewById<Button>(R.id.random_num_from_started_service).setOnClickListener {
            Log.d("mytag", "send broadcast")

            // (4)
            val broadcastIntent = Intent("GENERATE_RANDOM_NUMBER")
            sendBroadcast(broadcastIntent)
        }

        // (5)
        broadcastReceiver = BroadcastReceiverForActivity()

        val filter = IntentFilter()
        filter.addAction("SEND_RANDOM_NUMBER")

        registerReceiver(broadcastReceiver, filter)

        // 시스템 메시지를 전달받을 리시버 객체 생성 및 인텐트 필터 선언
        systemBroadcastReceiver = SystemBroadcastMessageReceiver()
        val systemBroadcastFilter = IntentFilter()

        // (2)
        systemBroadcastFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        systemBroadcastFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)

        systemBroadcastFilter.addAction(Intent.ACTION_HEADSET_PLUG)

        systemBroadcastFilter.addAction(Intent.ACTION_SCREEN_ON)
        systemBroadcastFilter.addAction(Intent.ACTION_SCREEN_OFF)

        registerReceiver(systemBroadcastReceiver, systemBroadcastFilter)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("mytag", "unbindService")
        unbindService(this)

        // (6)
        unregisterReceiver(broadcastReceiver)

        // (3)
        unregisterReceiver(systemBroadcastReceiver)
    }

    // (7)
    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        Log.d("mytag", "바운드 서비스 : onServiceConnected")

        myBinder = binder as? MyBoundService.MyBinder
    }

    // (8)
    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d("mytag", "바운드 서비스 : onServiceDisconnected")

        myBinder = null
    }
}
