package wikibook.learnandroid.todayquote
import android.content.SharedPreferences

data class Quote(var idx: Int, var text: String, var from: String = "") {
    companion object {
        // 명언을 저장하고 수정하는 함수
        fun saveToPreference(pref: SharedPreferences, idx: Int, text: String, from: String = "") : Quote {
            val editor = pref.edit()

            // (1)
            editor.putString("$idx.text", text)
            editor.putString("$idx.from", from.trim())

            editor.apply()

            return Quote(idx, text, from)
        }

        // 저장된 모든 명언을 담은 리스트를 반환하는 함수
        fun getQuotesFromPreference(pref: SharedPreferences): MutableList<Quote> {
            // (2)
            val quotes = mutableListOf<Quote>()

            for (i in 0 until 20) {
                val quoteText = pref.getString("$i.text", "")!!
                val quoteFrom = pref.getString("$i.from", "")!!

                if(quoteText.isNotBlank()) {
                    quotes.add(Quote(i, quoteText, quoteFrom))
                }
            }

            return quotes
        }

        // 특정 위치의 명언을 삭제하는 함수
        fun removeQuoteFromPreference(pref: SharedPreferences, idx: Int) {
            val editor = pref.edit()

            // (3)
            editor.remove("$idx.text")
            editor.remove("$idx.from")

            editor.apply()
        }
    }
}
