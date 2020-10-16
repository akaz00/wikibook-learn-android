package wikibook.learnandroid.quizquiz

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import wikibook.learnandroid.quizquiz.database.QuizDatabase
import java.util.*

class QuizResultFragment : Fragment() {
    interface QuizResultListener { fun onRetry() }
    lateinit var listener : QuizResultListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(parentFragment is QuizResultFragment.QuizResultListener) {
            listener = parentFragment as QuizResultFragment.QuizResultListener
        } else {
            throw Exception("QuizResultListener 미구현")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.quiz_result_fragment, container, false)

        // (1)
        val correctCount = arguments?.getInt("correctCount")
        val totalQuizCount = arguments?.getInt("totalQuizCount")
        view.findViewById<TextView>(R.id.score).text = "${correctCount} / ${totalQuizCount}"

        // (1)
        val correctRate = correctCount!!.toDouble() / totalQuizCount!!.toDouble()
        val resultText = view.findViewById<TextView>(R.id.result_text)
        var ratingStarNum : Int

        // (2)
        resultText.text = when {
            correctRate == 1.0 -> {
                ratingStarNum = 5
                "Perfect"
            }
            correctRate >= 0.7 -> {
                ratingStarNum = 4
                "Excellect"
            }
            correctRate >= 0.5 -> {
                ratingStarNum = 3
                "Good"
            }
            else -> {
                ratingStarNum = 2
                "Not Bad"
            }
        }

        // (3)
        view.findViewById<RatingBar>(R.id.score_star).rating = ratingStarNum.toFloat()

        // (2)
        view.findViewById<Button>(R.id.retry).setOnClickListener {
            listener.onRetry()
        }

        return view
    }

    companion object {
        fun newInstance(correctCount: Int, totalQuizCount: Int) : QuizResultFragment {
            val fragment = QuizResultFragment()

            // (3)
            val args = Bundle()
            args.putInt("correctCount", correctCount)
            args.putInt("totalQuizCount", totalQuizCount)
            fragment.setArguments(args)

            return fragment
        }
    }
}
