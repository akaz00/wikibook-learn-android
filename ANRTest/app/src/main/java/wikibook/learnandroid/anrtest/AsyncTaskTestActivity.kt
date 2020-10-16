package wikibook.learnandroid.anrtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AsyncTaskTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.async_task_test_activity)

        // (1)
        MyAsyncTask(this).execute("Hello", "Android", "AsyncTask")
    }
}
