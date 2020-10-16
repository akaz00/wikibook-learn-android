package wikibook.learnandroid.weatherdustchecker

import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL

// (1)
class APICall(val callback: APICall.APICallback) : AsyncTask<URL, Void, String>() {
    // (2)
    interface APICallback {
        fun onComplete(result: String)
    }

    override fun doInBackground(vararg params: URL?): String {
        // (3)
        val url = params.get(0)
        val conn : HttpURLConnection = url?.openConnection() as HttpURLConnection
        conn.connect()

        // (4)
        var body = conn?.inputStream.bufferedReader().use { it.readText() }

        // (5)
        conn.disconnect()

        return body
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        // (6)
        if(result != null) {
            callback.onComplete(result)
        }
    }
}
