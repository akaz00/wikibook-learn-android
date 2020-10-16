package wikibook.learnandroid.quizquiz

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import wikibook.learnandroid.quizquiz.database.Quiz

class QuizListAdapter(private val dataList: List<Quiz>, private val fragment: Fragment): RecyclerView.Adapter<QuizListAdapter.ItemViewHolder>() {
    // (1)
    class ItemViewHolder(val view: View, val fragment: Fragment) : RecyclerView.ViewHolder(view) {
        lateinit var quiz : Quiz
        val quizQuestion = view.findViewById<TextView>(R.id.question)

        // 클릭 리스너 설정을 위한 init 블록 추가
        init {
            view.setOnClickListener {
                val intent = Intent(it.context, QuizManageActivity::class.java)
                intent.putExtra("mode", "modify")
                intent.putExtra("quiz", quiz)

                // (4)
                intent.putExtra("position", adapterPosition)
                fragment.startActivityForResult(intent, 1)
            }
        }

        fun bind(q : Quiz) {
            this.quiz = q

            quizQuestion.text = q.question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        // (3)
        return ItemViewHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(dataList[position])

    override fun getItemCount() = dataList.size

    // (2)
    override fun getItemViewType(position: Int) = R.layout.quiz_list_item
}
