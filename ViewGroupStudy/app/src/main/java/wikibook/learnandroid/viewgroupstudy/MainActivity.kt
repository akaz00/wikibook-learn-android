package wikibook.learnandroid.viewgroupstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 리소스 식별자 변경
        // setContentView(R.layout.linear_layout_demo1)
        // setContentView(R.layout.linear_layout_demo2)
        // setContentView(R.layout.linear_layout_demo3)
        // setContentView(R.layout.linear_layout_demo4)
        // setContentView(R.layout.relative_layout_demo1)
        // setContentView(R.layout.relative_layout_demo2)
        setContentView(R.layout.nested_view_group_demo)
    }
}