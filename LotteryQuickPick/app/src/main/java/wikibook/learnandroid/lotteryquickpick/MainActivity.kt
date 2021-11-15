package wikibook.learnandroid.lotteryquickpick

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var numbers: TextView

    fun genereateLotteryNumber() : String {
        var numbers = mutableListOf<Int>()
        for(i in 1..6) {
            numbers.add(Random.nextInt(1, 46))
        }
        return numbers.joinToString("-")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numbers = findViewById<TextView>(R.id.numbers)
        numbers.text = genereateLotteryNumber()

        val generateButton = findViewById<TextView>(R.id.generate)
        generateButton.setOnClickListener {
            numbers.text = genereateLotteryNumber()
        }

        val saveButton = findViewById<TextView>(R.id.save)
        saveButton.setOnClickListener {
            val numbersList = getNumbersFromPref(this)
            numbersList.add(numbers.text.toString())
            saveNumbersToPref(this, numbersList)
        }

        val listButton = findViewById<TextView>(R.id.list)
        listButton.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        val checkResultButton = findViewById<TextView>(R.id.check_result)
        checkResultButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.dhlottery.co.kr/gameResult.do?method=byWin&wiselog=M_A_1_8"))
            startActivity(intent)
        }
    }
}
