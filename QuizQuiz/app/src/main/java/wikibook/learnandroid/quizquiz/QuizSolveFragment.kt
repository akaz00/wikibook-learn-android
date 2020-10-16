package wikibook.learnandroid.quizquiz

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import wikibook.learnandroid.quizquiz.database.Quiz
import java.util.*

class QuizSolveFragment : Fragment() {
    // (1)
    interface QuizSolveListener { fun onAnswerSelected(isCorrect: Boolean) }
    lateinit var listener : QuizSolveListener
    lateinit var quiz : Quiz

    // (1)
    var answerSelected : Boolean = false

    // (2)
    val ANIM_DURATION : Long = 250L

    // (1)
    lateinit var timer : Timer
    val MAX_REMAIN_TIME = 10 * 1000
    var remainTime = MAX_REMAIN_TIME

    // (2)
    lateinit var soundPool : SoundPool
    var soundVolume : Float = 0.5F
    var correctAnswerSoundId : Int = 0
    var incorrectAnswerSoundId : Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(parentFragment is QuizSolveFragment.QuizSolveListener) {
            listener = parentFragment as QuizSolveFragment.QuizSolveListener
        } else {
            throw Exception("QuizSolveListener 미구현")
        }
    }

    companion object {
        fun newInstance(quiz: Quiz, currentQuizIdx: Int, totalQuizCount : Int) : QuizSolveFragment {
            val fragment = QuizSolveFragment()

            // (2)
            val args = Bundle()
            args.putParcelable("quiz", quiz)

            // (9)
            args.putInt("currentQuizIdx", currentQuizIdx)
            args.putInt("totalQuizCount", totalQuizCount)

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.quiz_solve_fragment, container, false)

        val remainTimeBar = view.findViewById<ProgressBar>(R.id.remain_time_bar)

        // (3)
        soundPool = SoundPool.Builder().build()
        correctAnswerSoundId = soundPool.load(context, R.raw.correct, 1)
        incorrectAnswerSoundId = soundPool.load(context, R.raw.incorrect, 1)

        // (4)
        val currentQuizIdx = arguments?.getInt("currentQuizIdx")
        val totalQuizCount = arguments?.getInt("totalQuizCount")
        view.findViewById<TextView>(R.id.quiz_progress).text = "${currentQuizIdx}/${totalQuizCount}"

        // (5)
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                // (6)
                remainTime -= 1000
                remainTimeBar.progress = ((remainTime / MAX_REMAIN_TIME.toDouble()) * 100).toInt()

                // (7)
                if(remainTime <= 0) {
                    timer.cancel()
                    soundPool.play(incorrectAnswerSoundId, soundVolume, soundVolume, 1, 0, 1f)
                    activity?.runOnUiThread { listener.onAnswerSelected(false) }
                }
            }
        }, 0, 1000)

        quiz = arguments?.getParcelable("quiz")!!
        view.findViewById<TextView>(R.id.question).text = quiz.question

        val choices = view.findViewById<ViewGroup>(R.id.choices)

        // (8)
        val answerSelectListener = View.OnClickListener {
            // (1)
            if(!answerSelected) {
                timer.cancel()
                val guess = (it as Button).text.toString()

                // (1)
                val transition = it.background as TransitionDrawable
                transition.startTransition(ANIM_DURATION.toInt())

                // (2)
                val image = view.findViewById<ImageView>(R.id.answer_feedback_image)
                if(quiz.answer == guess) {
                    soundPool.play(correctAnswerSoundId, soundVolume, soundVolume, 1, 0, 1f)
                    image.setImageResource(R.drawable.ic_happy)
                } else {
                    soundPool.play(incorrectAnswerSoundId, soundVolume, soundVolume, 1, 0, 1f)
                    image.setImageResource(R.drawable.ic_unhappy)
                }

                // (3)
                val imageAlphaAnimator = ObjectAnimator.ofFloat(image, "alpha", 0.0F, 1.0F)
                val imageScaleXAnimator = ObjectAnimator.ofFloat(image, "scaleX", 1.0F, 1.5F)
                val imageScaleYAnimator = ObjectAnimator.ofFloat(image, "scaleY", 1.0F, 1.5F)

                // (4)
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(imageAlphaAnimator, imageScaleXAnimator, imageScaleYAnimator)
                animatorSet.duration = ANIM_DURATION
                animatorSet.interpolator = LinearInterpolator()
                animatorSet.start()

                // (5)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        activity?.runOnUiThread { listener.onAnswerSelected(quiz.answer == guess) }
                    }
                }, 1500)
            }

            answerSelected = true
        }

        when {
            quiz.type == "ox" -> {
                for(sign in listOf("o", "x")) {
                    // (1)
                    val btn = inflater.inflate(R.layout.answer_choice_button, choices, false) as Button
                    btn.text = sign
                    btn.setOnClickListener(answerSelectListener)
                    choices.addView(btn)
                }
            }
            quiz.type == "multiple_choice" -> {
                for(guess in quiz.guesses!!) {
                    // (1)
                    val btn = inflater.inflate(R.layout.answer_choice_button, choices, false) as Button
                    btn.text = guess
                    btn.isAllCaps = false
                    btn.setOnClickListener(answerSelectListener)
                    choices.addView(btn)
                }
            }
        }

        return view
    }
}
