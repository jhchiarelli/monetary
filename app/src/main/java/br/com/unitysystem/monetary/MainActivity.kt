package br.com.unitysystem.monetary

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result = findViewById<TextView>(R.id.txt_result)

        result.text = getString(R.string.place_return)

        val buttonConverter = findViewById<Button>(R.id.btn_converter)

        buttonConverter.setOnClickListener{
            toconverter()
        }

    }

    private fun toconverter() {
        val selectedCurrency = findViewById<RadioGroup>(R.id.radioGroup)

        val currency = when (selectedCurrency.checkedRadioButtonId) {
            R.id.radio_usd -> "USD"
            R.id.radio_eur -> "EUR"
            else  -> "GBP"
        }

        val editField = findViewById<EditText>(R.id.edit_field)

        val value =  editField.text.toString()

        if (value.isEmpty()) {
            result.text = getString(R.string.place_return)
            return
        }

        if (editField.isFocusable) {
            checkKeyboard()
            editField.clearFocus()
        }


        Thread {
            // thread da internet
            val url = URL("https://free.currconv.com/api/v7/convert?q=${currency}_BRL&compact=ultra&apiKey=51d850ff2cea99d24f4b")
            val conn = url.openConnection() as HttpsURLConnection

            try {
                val data = conn.inputStream.bufferedReader().readText()

                val obj =  JSONObject(data)

                runOnUiThread {
                    val res = obj.getDouble("${currency}_BRL")
                    result.text = "${getString(R.string.place_currency)} ${"%.4f".format(value.toDouble() * res)}"
                    result.visibility = View.VISIBLE
                }

            } finally {
                conn.disconnect()
            }
        }.start()

    }

    private fun checkKeyboard() {
        hideKeyboard()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}