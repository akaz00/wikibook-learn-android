package wikibook.learnandroid.quizquiz

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import wikibook.learnandroid.quizquiz.database.Quiz
import wikibook.learnandroid.quizquiz.database.QuizDatabase

class QuizListFragment : Fragment() {
    lateinit var recyclerView : RecyclerView
    lateinit var db : QuizDatabase
    lateinit var quizzes : MutableList<Quiz>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.quiz_list_fragment, container, false)

        // 액션바의 메뉴가 보이도록 setHasOptionsMenu 메서드를 호출
        setHasOptionsMenu(true)

        db = QuizDatabase.getInstance(context!!)

        AsyncTask.execute {
            // (1)
            quizzes = db.quizDAO().getAll().toMutableList()

            // (2)
            activity?.runOnUiThread {
                val layoutManager = LinearLayoutManager(activity)
                // 어댑터 객체를 생성할 때 프래그먼트 객체를 전달
                val adapter = QuizListAdapter(quizzes, this)

                recyclerView = view.findViewById<RecyclerView>(R.id.quiz_list)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter

                recyclerView.setHasFixedSize(true)
            }
        }

        view.findViewById<FloatingActionButton>(R.id.add_quiz).setOnClickListener {
            val intent = Intent(activity!!, QuizManageActivity::class.java)
            intent.putExtra("mode", "add")

            // (1)
            startActivityForResult(intent, 1)
        }


        return view
    }

    // (1)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // (2)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // (3)
            val operation = data?.getStringExtra("operation")
            val quiz = data?.getParcelableExtra<Quiz>("quiz")

            // (4)
            if(operation == "modify") {
                // (5)
                // 변경 상황
                for((i, q) in quizzes.withIndex()) {
                    if(quiz?.id == q.id) {
                        quizzes[i] = quiz!!
                        recyclerView.adapter?.notifyItemChanged(i)
                    }
                }
            } else if(operation == "delete") {
                // (6)
                // 삭제 상황
                val position = data.getIntExtra("position", -1)
                if(position != -1) {
                    quizzes.removeAt(position)
                    recyclerView.adapter?.notifyItemRemoved(position)
                }
            } else {
                // (7)
                // 추가 상황
                quizzes.add(quiz!!)
                recyclerView.adapter?.notifyItemInserted(quizzes.size)
            }
        }
    }

    // 메뉴 생성과 관련된 작업을 진행할 onCreateOptionsMenu 메서드를 재정의
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        AsyncTask.execute {
            val categories = db.quizDAO().getCategories()

            activity?.runOnUiThread {
                // (1)
                val categoryMenu = menu.add("전부")
                categoryMenu.setIcon(android.R.drawable.ic_menu_view)
                categoryMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

                // (2)
                for(c in categories) {
                    val categoryMenu = menu.add(c)
                    categoryMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                }
            }
        }

        true
    }

    // 메뉴를 선택한 이후 수행할 작업을 정의할 onOptionsItemSelected 메서드를 재정의
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        AsyncTask.execute {
            // (3)
            val category = item.toString()
            if(category == "전부") {
                quizzes = db.quizDAO().getAll().toMutableList()
            } else {
                quizzes = db.quizDAO().getAll(category).toMutableList()
            }

            activity?.runOnUiThread {
                // (4)
                val adapter = QuizListAdapter(quizzes, this)
                recyclerView.swapAdapter(adapter, false)
            }
        }

        return true
    }
}

