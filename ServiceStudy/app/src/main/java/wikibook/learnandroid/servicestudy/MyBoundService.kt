package wikibook.learnandroid.servicestudy

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlin.random.Random

class MyBoundService : Service() {
    // (1)
    inner class MyBinder : Binder() {
        // (2)
        val service = this@MyBoundService
    }

    // (3)
    private val binder = MyBinder()

    // (4)
    override fun onBind(intent: Intent?): IBinder? {
        Log.d("mytag", "service : onBind")

        return binder
    }

    // (5)
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("mytag", "service : onUnbind")

        return super.onUnbind(intent)
    }

    // (6)
    fun getRandomNum(from : Int = 0, until : Int = 100) = Random.nextInt(from, until)
}
