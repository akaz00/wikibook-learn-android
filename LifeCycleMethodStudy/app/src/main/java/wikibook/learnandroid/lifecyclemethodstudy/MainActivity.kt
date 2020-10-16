package wikibook.learnandroid.lifecyclemethodstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
// 뷰 관련 import 구문 추가
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    // (1)
    private var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.saving_ui_state_demo)

        // (1)
        // onCreate 메소드 내부에서 Bundle 객체를 이용하여 데이터 복원
        if(savedInstanceState != null) {
            num = savedInstanceState.getInt("num")
        }

        var numberText = findViewById<TextView>(R.id.number)
        var numberEdit = findViewById<EditText>(R.id.number_edit)
        var increaseButton = findViewById<Button>(R.id.increase)
        var setButton = findViewById<Button>(R.id.set_number)

        // (2)
        numberText.text = num.toString()

        // (3)
        increaseButton.setOnClickListener {
            num = numberText.text.toString().toInt() + 1
            numberText.text = num.toString()
        }
        setButton.setOnClickListener {
            num = numberEdit.text.toString().toInt()
            numberText.text = num.toString()
        }

        // onCreate 메서드에서 로그 메시지를 출력하는 코드
        Log.d("mytag", "onCreate")
    }

    // onDestroy 재정의 및 로그 메시지를 출력하는 코드를 추가
    override fun onDestroy() {
        super.onDestroy()
        Log.d("mytag", "onDestroy")
    }

    // 두 콜백 메서드를 onDestroy 메서드 이후에 추가
    // (1)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("mytag", "onSaveInstanceState")
        outState.putInt("num", num)
    }

    /*
    // (2)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("mytag", "onRestoreInstanceState")
        num = savedInstanceState.getInt("num") ?: 0

        var numberText = findViewById<TextView>(R.id.number)
        numberText.text = num.toString()
    }
    */

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        // 부모 객체(super)에 접근해서 각 생명주기에 대응하는 메서드를 호출
        super.onCreate(savedInstanceState)
        Log.d("mytag", "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("mytag", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("mytag", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("mytag", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("mytag", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("mytag", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("mytag", "onRestart")
    }
    */
}
