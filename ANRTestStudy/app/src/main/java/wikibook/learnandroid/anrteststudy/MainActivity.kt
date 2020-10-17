// ANR 상황 테스트 용 액티비티 클래스
/*
package wikibook.learnandroid.anrteststudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.lang.Math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anr_test_activity)

        // 결과를 출력할 TextView 객체 초기화
        val result = findViewById<TextView>(R.id.result)

        findViewById<Button>(R.id.btn).setOnClickListener {
            Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
        }

        /*
        findViewById<Button>(R.id.anr).setOnClickListener {
            for(i in 1 .. Int.MAX_VALUE) {
                Log.d("mytag", sqrt(Random.nextDouble()).toString())
            }
        }
        */

        /*
        findViewById<Button>(R.id.anr).setOnClickListener {
            // 시간이 아주 오래 걸리는 작업을 스레드로 위임하여 처리
            Thread(Runnable {
                for(i in 1 .. 60) {
                    Log.d("mytag", sqrt(Random.nextDouble()).toString())
                    Thread.sleep(1000)
                }
            }).start()
        }
        */

        /*
        findViewById<Button>(R.id.anr).setOnClickListener {
            Thread(Runnable {
                var sum = 0.0
                for(i in 1 .. 60) {
                    sum += sqrt(Random.nextDouble())
                    Thread.sleep(100)
                }
                runOnUiThread {
                    // 액티비티에서 제공하는 runOnUiThread 메서드를 호출하는 과정에서 작성한 코드들이 모두 메인 스레드에서 실행되게 함
                    result.text = sum.toString()
                }
            }).start()
        }
        */

        val progressStatus = findViewById<TextView>(R.id.progress_status)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val progress = findViewById<TextView>(R.id.progress)

        findViewById<Button>(R.id.anr).setOnClickListener {
            // (1)
            progressStatus.text = "작업을 수행 중"
            progressBar.visibility = View.VISIBLE

            Thread(Runnable {
                var sum = 0.0
                var count = 0
                for(i in 1 until 60) {
                    sum += sqrt(Random.nextDouble())
                    Thread.sleep(100)
                    // (2)
                    runOnUiThread {
                        progress.text = "%.1f".format(((count + 1) / 60.toDouble()) * 100) + "% 완료"
                    }
                    count++
                }
                // (3)
                runOnUiThread {
                    result.text = sum.toString()
                    progressStatus.text = "작업 수행 완료"
                    progressBar.visibility = View.GONE
                }
            }).start()
        }
    }
}
*/

// MyAsyncTask 클래스 테스트 용 액티비티 클래스
package wikibook.learnandroid.anrteststudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.async_task_test_activity)

        // (1)
        MyAsyncTask(this).execute("Hello", "Android", "AsyncTask")
    }
}
