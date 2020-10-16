package wikibook.learnandroid.todayquote

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class QuoteMainActivity : AppCompatActivity() {
    // (1)
    private lateinit var quotes: List<Quote>
    private lateinit var pref: SharedPreferences

    // 초기 명언 데이터를 저장하는 역할을 수행하는 initializeQuotes 메서드
    fun initializeQuotes() {
        // (3)
        val initialized = pref.getBoolean("initialized", false)
        if(!initialized) {
            // (4)
            Quote.saveToPreference(pref, 0, "괴로운 시련처럼 보이는 것이 뜻밖의 좋은 일일 때가 많다.", "오스카 와일드")
            Quote.saveToPreference(pref, 1, "성공한 사람이 되려고 노력하기보다 가치있는 사람이 되려고 노력하라.", "알버트 아인슈타인")
            Quote.saveToPreference(pref, 2, "추구할 수 있는 용기가 있다면 우리의 모든 꿈은 이뤄질 수 있다.", "월트 디즈니")
            Quote.saveToPreference(pref, 3, "실패에서부터 성공을 만들어 내라. 좌절과 실패는 성공으로 가는 가장 확실한 디딤돌이다.", "데일 카네기")
            Quote.saveToPreference(pref, 4, "창조적인 삶을 살려면 내가 틀릴지도 모른다는 공포를 버려야 한다.")

            val editor = pref.edit()
            // (5)
            editor.putBoolean("initialized", true)
            editor.apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // (2)
        setContentView(R.layout.quote_main_activity)

        // (2)
        pref = getSharedPreferences("quotes", Context.MODE_PRIVATE)
        initializeQuotes()

        // (3)
        val quoteText = findViewById<TextView>(R.id.quote_text)
        val quoteFrom = findViewById<TextView>(R.id.quote_from)

        // (1)
        val toQuoteListButton = findViewById<Button>(R.id.quote_list_btn)
        toQuoteListButton.setOnClickListener {
            // (2)
            val intent = Intent(this, QuoteListActivity::class.java)
            // (3)
            intent.putExtra("quote_size", quotes.size)
            // (4)
            startActivity(intent)
        }

        // (1)
        val toQuoteEditListButton = findViewById<Button>(R.id.quote_edit_btn)
        toQuoteEditListButton.setOnClickListener {
            val intent = Intent(this, QuoteEditActivity::class.java)
            startActivity(intent)
        }
        quotes = Quote.getQuotesFromPreference(pref)

        if(quotes.isNotEmpty()) {
            val randomIndex = Random().nextInt(quotes.size)
            val randomQuote = quotes[randomIndex]
            quoteText.text = randomQuote.text
            quoteFrom.text = randomQuote.from
        } else {
            quoteText.text = "저장된 명언이 없습니다."
            quoteFrom.text = ""
        }
    }
}
