package wikibook.learnandroid.todayquote

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuoteListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quote_list_activity)

        val pref = getSharedPreferences("quotes", Context.MODE_PRIVATE)

        // (1)
        val quotes = Quote.getQuotesFromPreference(pref)

        // (2)
        val layoutManager = LinearLayoutManager(this)

        // (3)
        val adapter = QuoteAdapter(quotes)

        // (4)
        val recyclerView = findViewById<RecyclerView>(R.id.quote_list)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}
