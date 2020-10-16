package wikibook.learnandroid.fragmentstudy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), CurrencyConverterFragment3.CurrencyCalculationListener {
    override fun onCalculate(result: Double, amount: Double, from: String, to: String) {
        Toast.makeText(this, "${String.format("%.5f",amount)}($from) -> ${String.format("%.5f",result)}($to)", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, CurrencyConverterFragment3.newInstance("KRW", "USD"))
        transaction.add(R.id.fragment_container, CurrencyConverterFragment3.newInstance("JPY", "KRW"))
        transaction.add(R.id.fragment_container, CurrencyConverterFragment3.newInstance("EUR", "JPY"))

        transaction.commit()
    }
}

