package wikibook.learnandroid.quizquiz

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import wikibook.learnandroid.quizquiz.database.Quiz
import wikibook.learnandroid.quizquiz.database.QuizDatabase

class QuizFragment : Fragment(),
    QuizStartFragment.QuizStartListener,
    QuizSolveFragment.QuizSolveListener,
    QuizResultFragment.QuizResultListener {
    var currentQuizIdx = 0
    var correctCount = 0
    lateinit var db : QuizDatabase
    lateinit var quizList : List<Quiz>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.quiz_fragment, container, false)

        db = QuizDatabase.getInstance(context!!)

        // (1)
        childFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, QuizStartFragment()).commit()

        return view
    }

    override fun onQuizStart(category: String) {
        // 퀴즈 풀이가 시작됐으므로 액션바를 숨기도록 hide 메서드를 호출
        (activity as AppCompatActivity).supportActionBar?.hide()

        AsyncTask.execute {
            currentQuizIdx = 0
            correctCount = 0

            // 카테고리 정보에 따라 퀴즈 정보를 모두 조회하거나 특정 카테고리에 포함된 퀴즈 정보만 조회
            quizList = if(category == "전부") db.quizDAO().getAll() else db.quizDAO().getAll(category)

            childFragmentManager.beginTransaction().replace(R.id.fragment_container,
                // 퀴즈 객체와 현재 순서 및 퀴즈의 총 개수를 전달
                QuizSolveFragment.newInstance(quizList[currentQuizIdx], 1, quizList.size)
            ).commit()
        }
    }

    override fun onAnswerSelected(isCorrect: Boolean) {
        // (6)
        if(isCorrect) correctCount++
        currentQuizIdx++

        if(currentQuizIdx == quizList.size) {
            // 퀴즈 풀이가 모두 끝났으므로 액션바를 다시 보여주도록 show 메서드를 호출
            (activity as AppCompatActivity).supportActionBar?.show()
            childFragmentManager.beginTransaction().replace(R.id.fragment_container, QuizResultFragment.newInstance(correctCount, quizList.size)).commit()
        } else {
            childFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container,
                    QuizSolveFragment.newInstance(quizList[currentQuizIdx], currentQuizIdx + 1, quizList.size)
                ).commit()
        }
    }

    // (9)
    override fun onRetry() {
        childFragmentManager.beginTransaction().replace(R.id.fragment_container, QuizStartFragment()).commit()
    }
}
