package wikibook.learnandroid.todayquote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class QuoteEditAdapter(private val dataList: List<Quote>) : RecyclerView.Adapter<QuoteEditAdapter.QuoteItemViewHolder>() {
    class QuoteItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var quote : Quote
        val quoteTextEdit = view.findViewById<EditText>(R.id.quote_text_edit)
        val quoteFromEdit = view.findViewById<EditText>(R.id.quote_from_edit)
        val quoteDeleteBtn = view.findViewById<Button>(R.id.quote_delete_btn)
        val quoteModifyBtn = view.findViewById<Button>(R.id.quote_modify_btn)

        init {
            // (1)
            val pref = view.context.getSharedPreferences("quotes", Context.MODE_PRIVATE)

            // (2)
            quoteDeleteBtn.setOnClickListener {
                quoteTextEdit.setText("")
                quoteFromEdit.setText("")
                quote.text = ""
                quote.from = ""
                Quote.removeQuoteFromPreference(pref, adapterPosition)
                Toast.makeText(it.context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }

            // (3)
            quoteModifyBtn.setOnClickListener {
                val newQuoteText = quoteTextEdit.text.toString()
                val newQuoteFrom = quoteFromEdit.text.toString()
                quote.text = newQuoteText
                quote.from = newQuoteFrom
                Quote.saveToPreference(pref, adapterPosition, newQuoteText, newQuoteFrom)
                Toast.makeText(it.context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // (4)
        fun bind(q : Quote) {
            quote = q

            quoteTextEdit.setText(quote.text)
            quoteFromEdit.setText(quote.from)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return QuoteItemViewHolder(view)
    }
    override fun onBindViewHolder(holder: QuoteItemViewHolder, position: Int) = holder.bind(dataList[position])
    override fun getItemCount() = dataList.size
    override fun getItemViewType(position: Int) = R.layout.quote_edit_list_item
}
