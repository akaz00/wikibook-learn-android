package wikibook.learnandroid.logcatandtoaststudy

import android.widget.Toast
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.v("tag_name1", "Verbose Message") // 일반적인 메시지를 출력
        Log.d("tag_name2", "Debug Message") // 디버깅용 메시지를 출력
        Log.i("tag_name3", "Info Message") // 정보 표기용 메시지를 출력
        Log.w("tag_name4", "Warning Message") // 경고 메시지를 출력
        Log.e("tag_name5", "Error Message") // 에러 발생 메시지를 출력

        // 토스트 객체 생성
        /*
        var toast : Toast = Toast.makeText(this, "Toast Message (Short)", Toast.LENGTH_SHORT)
        // setGravity 메서드를 호출해서 토스트의 출력 위치를 지정
        toast.setGravity(Gravity.CENTER or Gravity.LEFT, 0, 0)
        toast.show()
        */

        // 토스트 객체를 반환받고 곧바로 show 메서드를 호출
        Toast.makeText(this, "Toast Message (Long)", Toast.LENGTH_LONG).show()
    }
}
