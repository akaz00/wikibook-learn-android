package wikibook.learnandroid.anrtest

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.sqrt
import kotlin.random.Random

class ANRTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anr_test_activity)
        // 결과를 출력할 TextView 객체 초기화
        val result = findViewById<TextView>(R.id.result)
        val progressStatus = findViewById<TextView>(R.id.progress_status)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val progress = findViewById<TextView>(R.id.progress)

        findViewById<Button>(R.id.btn).setOnClickListener {
            Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
        }

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
