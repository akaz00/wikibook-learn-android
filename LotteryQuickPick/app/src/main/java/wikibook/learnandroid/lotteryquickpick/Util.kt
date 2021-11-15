package wikibook.learnandroid.lotteryquickpick

import android.content.Context
import android.util.Log

fun getNumbersFromPref(ctx: Context) : MutableList<String> {
    val numbers =  mutableListOf<String>()
    val pref = ctx.getSharedPreferences("numbers", Context.MODE_PRIVATE)
    val size = pref.getInt("size", 0)

    Log.d("asdf", "size : $size")

    for(i in 0 until size) {
        numbers.add(pref.getString("$i", null)!!)
    }

    return numbers
}

fun saveNumbersToPref(ctx: Context, guesses: List<String>) {
    val pref = ctx.getSharedPreferences("numbers", Context.MODE_PRIVATE)
    val editor = pref.edit()
    var idx = 0
    for(g in guesses) {
        editor.putString("$idx", g)
        idx++
    }
    Log.d("asdf", "save : size : $idx")
    editor.putInt("size", idx)
    editor.apply()
}