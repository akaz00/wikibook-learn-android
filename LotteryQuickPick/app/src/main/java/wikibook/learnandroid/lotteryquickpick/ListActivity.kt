package wikibook.learnandroid.lotteryquickpick

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(private val dataList: MutableList<String>, private val ctx: Context): RecyclerView.Adapter<ListAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val numbers = dataList[position]
        holder.view.findViewById<TextView>(R.id.numbers).text = numbers
        holder.view.findViewById<Button>(R.id.delete).setOnClickListener {
            dataList.removeAt(position)
            saveNumbersToPref(ctx, dataList)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = dataList.size

    override fun getItemViewType(position: Int) = R.layout.list_item
}

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val numbersList = getNumbersFromPref(this)

        val layoutManager = LinearLayoutManager(this)
        val adapter = ListAdapter(numbersList, this)

        val recyclerView = findViewById<RecyclerView>(R.id.numbers_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}
