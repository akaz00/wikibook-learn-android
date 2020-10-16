package wikibook.learnandroid.todayquote

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// (1)
class QuoteAdapter(private val dataList: List<Quote>) : RecyclerView.Adapter<QuoteAdapter.QuoteItemViewHolder>() {
    // (2)
    class QuoteItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var quote : Quote
        val quoteText = view.findViewById<TextView>(R.id.quote_text)
        val quoteFrom = view.findViewById<TextView>(R.id.quote_from)

        // (1)
        val shareBtn = view.findViewById<Button>(R.id.quote_share_btn)
        val fromSearchBtn = view.findViewById<Button>(R.id.quote_from_search_btn)

        init {
            shareBtn.setOnClickListener {
                // (2)
                val intent = Intent(Intent.ACTION_SEND)
                // (3)
                intent.putExtra(Intent.EXTRA_TITLE, "힘이 되는 명언")
                intent.putExtra(Intent.EXTRA_SUBJECT, "힘이 되는 명언")
                intent.putExtra(Intent.EXTRA_TEXT, "${quote.text}\n출처 : ${quote.from}")
                intent.type = "text/plain"

                // (4)
                val chooser = Intent.createChooser(intent, "명언 공유")

                // (5)
                it.context.startActivity(chooser)
            }

            fromSearchBtn.setOnClickListener {
                // (6)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${quote.from}"))
                it.context.startActivity(intent)
            }
        }

        fun bind(q : Quote) {
            this.quote = q

            quoteText.text = quote.text
            quoteFrom.text = quote.from

            // (7)
            if(quote.from.isBlank()) {
                fromSearchBtn.visibility = View.GONE
            }
        }
    }

    // (3)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteItemViewHolder {
        // (4)
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return QuoteItemViewHolder(view)
    }

    // (5)
    override fun onBindViewHolder(holder: QuoteItemViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    // (6)
    override fun getItemCount() = dataList.size

    // (7)
    override fun getItemViewType(position: Int) = R.layout.quote_list_item
}
