package wikibook.learnandroid.quizquiz

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import org.w3c.dom.Element
// Quiz 관련 클래스 import 구문 추가
import wikibook.learnandroid.quizquiz.database.Quiz
import wikibook.learnandroid.quizquiz.database.QuizDatabase
import javax.xml.parsers.DocumentBuilderFactory

class QuizMainActivity : AppCompatActivity() {
    lateinit var drawerToggle : ActionBarDrawerToggle
    // (1)
    lateinit var db : QuizDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_main_activity)

        // (1)
        db = QuizDatabase.getInstance(this)

        // (2)
        val sp : SharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        if(sp.getBoolean("initialized", true)) {
            initQuizDataFromXMLFile()

            val editor = sp.edit()
            editor.putBoolean("initialized", false)
            editor.commit()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.drawer_nav_view)

        supportFragmentManager.beginTransaction().add(R.id.frame, QuizFragment()).commit()

        // (1)
        navView.setNavigationItemSelectedListener {
            // (2)
            when(it.itemId) {
                R.id.quiz_solve -> supportFragmentManager.beginTransaction().replace(R.id.frame, QuizFragment()).commit()
                R.id.quiz_manage -> supportFragmentManager.beginTransaction().replace(R.id.frame, QuizListFragment()).commit()
            }

            drawerLayout.closeDrawers()

            true
        }

        // (3)
        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {}
        // (4)
        drawerToggle.isDrawerIndicatorEnabled = true
        // (5)
        drawerLayout.addDrawerListener(drawerToggle)

        // (6)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // (7)
        drawerToggle.syncState()
    }

    // (8)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // (9)
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    // (3)
    fun initQuizDataFromXMLFile() {
        // (4)
        AsyncTask.execute {
            // (5)
            val stream = assets.open("quizzes.xml")

            // (6)
            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

            // (7)
            val doc = docBuilder.parse(stream)

            // (8)
            val quizzesFromXMLDoc = doc.getElementsByTagName("quiz")

            // (9)
            val quizList = mutableListOf<Quiz>()
            for(idx in 0 until quizzesFromXMLDoc.length) {
                // org.w3c.dom 패키지의 Element 클래스 import
                val e = quizzesFromXMLDoc.item(idx) as Element

                // (10)
                val type = e.getAttribute("type")

                // (11)
                val question = e.getElementsByTagName("question").item(0).textContent
                val answer = e.getElementsByTagName("answer").item(0).textContent
                val category = e.getElementsByTagName("category").item(0).textContent

                // (12)
                when {
                    type == "ox" -> {
                        quizList.add(Quiz(type=type, question=question, answer=answer, category=category))
                    }
                    type == "multiple_choice" -> {
                        // (13)
                        var choices = e.getElementsByTagName("choice")
                        var choiceList = mutableListOf<String>()
                        for(idx in 0 until choices.length) {
                            choiceList.add(choices.item(idx).textContent)
                        }
                        quizList.add(Quiz(type=type, question=question, answer=answer, category=category, guesses=choiceList))
                    }
                }
            }

            // (14)
            for(quiz in quizList) {
                db.quizDAO().insert(quiz)
            }
        }
    }
}
