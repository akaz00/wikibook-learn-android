package wikibook.learnandroid.quizquiz

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import wikibook.learnandroid.quizquiz.database.QuizDatabase

class QuizStartFragment : Fragment() {
    // (1)
    interface QuizStartListener { fun onQuizStart(category: String) }
    lateinit var listener : QuizStartListener

    // (2)
    lateinit var db : QuizDatabase

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // (2)
        if(parentFragment is QuizStartFragment.QuizStartListener) {
            listener = parentFragment as QuizStartFragment.QuizStartListener
        } else {
            throw Exception("QuizStartListener 미구현")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.quiz_start_fragment, container, false)

        // (2)
        db = QuizDatabase.getInstance(context!!)
        val categorySpinner = view.findViewById<Spinner>(R.id.quiz_category)

        view.findViewById<Button>(R.id.start).setOnClickListener {
            // (3)
            val selected = categorySpinner.selectedItem.toString()
            listener.onQuizStart(selected)
        }

        AsyncTask.execute {
            // (4)
            var categories = db.quizDAO().getCategories().toMutableList()
            categories.add(0, "전부")

            activity?.runOnUiThread {
                // (5)
                val spinnerAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_dropdown_item, categories)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = spinnerAdapter
            }
        }

        return view
    }
}
