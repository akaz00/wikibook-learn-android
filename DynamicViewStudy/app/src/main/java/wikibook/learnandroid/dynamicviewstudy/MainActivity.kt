package wikibook.learnandroid.dynamicviewstudy

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootViewGroup = LinearLayout(this)
        rootViewGroup.orientation = LinearLayout.VERTICAL
        rootViewGroup.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        val DPToPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()

        val btn = Button(this)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(DPToPX, DPToPX, DPToPX, DPToPX)
        btn.setPadding(DPToPX, DPToPX, DPToPX, DPToPX)
        btn.layoutParams = params
        btn.text = "Hello"
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26F)
        btn.setTextColor(Color.RED)

        val editText = EditText(this)
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.getDisplayMetrics()).toInt()
        editText.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        editText.setBackgroundColor(Color.parseColor("#FFFF00"))
        editText.gravity = Gravity.CENTER or Gravity.RIGHT

        val imageView = ImageView(this)
        imageView.setImageResource(android.R.drawable.ic_menu_zoom)
        val imageViewParams = LinearLayout.LayoutParams(height, height)
        imageViewParams.gravity = Gravity.CENTER
        imageView.layoutParams = imageViewParams

        rootViewGroup.addView(btn)
        rootViewGroup.addView(editText)
        rootViewGroup.addView(imageView)

        setContentView(rootViewGroup)
    }
}
