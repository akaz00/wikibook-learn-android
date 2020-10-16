package wikibook.learnandroid.quizquiz

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import wikibook.learnandroid.quizquiz.database.Quiz
import wikibook.learnandroid.quizquiz.database.QuizDatabase

class QuizManageActivity : AppCompatActivity() {
    lateinit var mode : String
    lateinit var quiz : Quiz
    lateinit var db : QuizDatabase
    lateinit var choices : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_manage_activity)

        mode = intent.getStringExtra("mode")
        if(mode == "modify") {
            quiz = intent.getParcelableExtra<Quiz>("quiz")
            // 적절한 버튼 레이블 설정
            findViewById<Button>(R.id.confirm).text = "퀴즈 수정"
        } else {
            // 내용 없는 OX 퀴즈 객체를 생성해 quiz 속성을 초기화
            quiz = Quiz(type="ox", question="", answer="o", category="")
            findViewById<Button>(R.id.confirm).text = "퀴즈 추가"
        }

        db = QuizDatabase.getInstance(this)

        // (1)
        val spinner = findViewById<Spinner>(R.id.quiz_type)
        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.quiz_type, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // (2)
        val categoryEdit = findViewById<EditText>(R.id.category_edit)
        categoryEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quiz.category = s.toString()
            }
        })

        // (3)
        val questionEdit = findViewById<EditText>(R.id.question_edit)
        questionEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quiz.question = s.toString()
            }
        })

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // (4)
                when {
                    position == 0 -> changeLayoutToOXQuizManage()
                    position == 1 -> changeLayoutToMultipleChoiceQuizManage()
                }
            }
        }

        choices = findViewById<LinearLayout>(R.id.choices)

        findViewById<Button>(R.id.confirm).setOnClickListener {
            AsyncTask.execute {
                // (1)
                var validationFail = false
                var reason : String = ""

                // (2)
                if(quiz.question!!.isBlank()) {
                    validationFail = true
                    reason = "문두가 있어야 합니다."
                }

                // (3)
                if(quiz.type == "ox") {
                    val answerShouldOorX = ((quiz.answer == "o") || (quiz.answer == "x"))
                    if(!answerShouldOorX) {
                        validationFail = true
                        reason = "정답은 o거나 x여야 합니다."
                    }
                } else {
                    val guesses = mutableListOf<String>()
                    for(i in 0 until choices.childCount) {
                        val choiceEdit = (choices.getChildAt(i) as ViewGroup).getChildAt(0) as EditText
                        val guess = (choiceEdit.text.toString())

                        // (4)
                        if(guess.isNotBlank()) {
                            guesses.add(guess.trim())
                        }
                    }

                    // (5)
                    if(guesses.size < 2) {
                        validationFail = true
                        reason = "정상적인 내용이 포함된 2개의 이상의 선지가 필요합니다."
                    } else {
                        quiz.guesses = guesses
                    }
                }

                quiz.category = quiz.category?.trim()
                quiz.question = quiz.question?.trim()

                // (6)
                if(!validationFail) {
                    if(mode == "modify") {
                        db.quizDAO().update(quiz)
                    } else {
                        // (1)
                        val id = db.quizDAO().insert(quiz)
                        quiz.id = id
                    }

                    // (2)
                    val resultIntent = Intent()
                    resultIntent.putExtra("operation", mode)
                    resultIntent.putExtra("quiz", quiz)

                    // (3)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    runOnUiThread { Toast.makeText(this, reason, Toast.LENGTH_SHORT).show() }
                }
            }
        }

        // (8)
        if(mode == "modify") {
            findViewById<Button>(R.id.delete).visibility = View.VISIBLE
            findViewById<Button>(R.id.delete).setOnClickListener {
                AsyncTask.execute {
                    db.quizDAO().delete(quiz)

                    // (1)
                    val resultIntent = Intent()
                    resultIntent.putExtra("operation", "delete")
                    resultIntent.putExtra("position", intent.getIntExtra("position", -1))
                    resultIntent.putExtra("quiz", quiz)

                    // (2)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

        // (10)
        categoryEdit.setText(quiz.category)
        questionEdit.setText(quiz.question)

        when {
            // (11)
            quiz.type == "ox" -> {
                spinner.setSelection(0)
            }
            quiz.type == "multiple_choice" -> {
                spinner.setSelection(1)
            }
        }
    }

    fun changeLayoutToOXQuizManage() {
        // (1)
        quiz.type = "ox"
        choices.removeAllViews()

        // (2)
        findViewById<Button>(R.id.add_choice).visibility = View.GONE

        // (3)
        val listener = View.OnClickListener {
            quiz.answer = (it as Button).text.toString()
        }

        // (4)
        for(choice in listOf("o", "x")) {
            var btn = Button(this)
            btn.text = choice
            btn.setOnClickListener(listener)
            choices.addView(btn)
        }
    }

    fun changeLayoutToMultipleChoiceQuizManage() {
        quiz.type = "multiple_choice"
        choices.removeAllViews()
        findViewById<Button>(R.id.add_choice).visibility = View.VISIBLE

        // (1)
        val guesses = quiz?.guesses ?: listOf("", "")

        // (4)
        val setAnswerListener = View.OnClickListener {
            quiz.answer = ((it.parent as ViewGroup).getChildAt(0) as EditText).text.toString()
        }

        // (5)
        val removeEditListener = View.OnClickListener {
            // 선택지의 개수가 2개를 초과할 경우에만 선택지를 삭제
            if(choices.childCount > 2) {
                choices.removeView(it.parent as ViewGroup)
            } else {
                // 토스트로 경고 메시지를 출력
                Toast.makeText(this, "N지선다 문제는 최소한 2개의 선택지를 포함해야 합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        for(guess in guesses) {
            // (2)
            val edit = layoutInflater.inflate(R.layout.quiz_manage_multiple_choice_edit, choices, false) as ViewGroup
            (edit.getChildAt(0) as EditText).setText(guess)
            (edit.getChildAt(1) as Button).setOnClickListener(setAnswerListener)
            (edit.getChildAt(2) as Button).setOnClickListener(removeEditListener)
            choices.addView(edit)
        }

        findViewById<Button>(R.id.add_choice).setOnClickListener {
            // (3)
            val edit = layoutInflater.inflate(R.layout.quiz_manage_multiple_choice_edit, choices, false) as ViewGroup
            (edit.getChildAt(1) as Button).setOnClickListener(setAnswerListener)
            (edit.getChildAt(2) as Button).setOnClickListener(removeEditListener)
            choices.addView(edit)
        }
    }
}
