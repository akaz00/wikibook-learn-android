package wikibook.learnandroid.viewstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnLongClickListener {
            Toast.makeText(this, "롱 클릭 이벤트 발생", Toast.LENGTH_SHORT).show()

            true
        }

        val toggleButton = findViewById<ToggleButton>(R.id.toggle_button)
        toggleButton.setOnCheckedChangeListener { view, isChecked ->
            Toast.makeText(
                this,
                "isChecked : ${isChecked}, view.isChecked : ${view.isChecked}",
                Toast.LENGTH_SHORT
            ).show()
        }

        val editText = findViewById<EditText>(R.id.edit_text)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Toast.makeText(this@MainActivity, "${s.toString()}", Toast.LENGTH_SHORT).show()
            }
        })

        editText.setOnFocusChangeListener { view, hasFocus ->
            Toast.makeText(this, "포커스 변경 : ${hasFocus}", Toast.LENGTH_SHORT).show()
        }
        // 포커스 할당 메서드
        editText.requestFocus()
        // 포커스 해제 메서드
        editText.clearFocus()

        val checkBox1 = findViewById<CheckBox>(R.id.checkbox1)
        checkBox1.setOnCheckedChangeListener { view, isChecked ->
            Toast.makeText(this, "${isChecked} ${view.isChecked}", Toast.LENGTH_SHORT).show()
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_button1 -> Toast.makeText(this, "라디오 버튼 1 선택", Toast.LENGTH_SHORT).show()
                R.id.radio_button2 -> Toast.makeText(this, "라디오 버튼 2 선택", Toast.LENGTH_SHORT).show()
            }
        }

        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(this, R.array.string_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@MainActivity, "${position} ${item} ${spinner.selectedItem.toString()}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}