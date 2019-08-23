package dev.mfazio.passcodepractice

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var emptyInputValue: String
    private lateinit var correctValue: String
    private lateinit var inputValue: String

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onResume() {
        super.onResume()

        setCorrectValue(this.prefs.getString(correctValuePreferenceKey, defaultCorrectValue)
            ?: defaultCorrectValue)

        successCount.text = prefs.getInt(successSharedPreferenceKey, 0).toString()
        ohNoCount.text = prefs.getInt(ohNoSharedPreferenceKey, 0).toString()
        clearCount.text = prefs.getInt(clearsSharedPreferenceKey, 0).toString()

        if (correctValue == "" || correctValue == defaultCorrectValue) displayCorrectValueDialog()
    }

    fun clickButton(view: View) {
        with(textResult) {
            text = ""
            setTextColor(Color.BLACK)
        }
        (view as Button).let { button ->
            when (button.text) {
                "Clear" -> clearText(true)
                "Enter" -> checkValue()
                else -> appendValue(button.text?.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menuSettingsButton -> {
            startActivity(
                Intent(this, SettingsActivity::class.java)
            )
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setCorrectValue(value: String) {
        this.correctValue = value

        emptyInputValue = (1..correctValue.length).joinToString("") { "-" }
        inputValue = emptyInputValue

        setInputText(inputValue)
    }

    private fun setInputText(value: String) {
        inputValue = value
        textInput.text = value.toList().joinToString(" ")
    }

    private fun clearText(countClear: Boolean = false) {
        setInputText(emptyInputValue)
        if (countClear) incPref(clearsSharedPreferenceKey)
    }

    private fun appendValue(value: String?) {
        if (value != null && !inputValue.all { it.isDigit() }) {
            setInputText("${inputValue.drop(1)}$value")
        }
    }

    private fun checkValue() {
        if (inputValue == correctValue) {
            textResult.text = getString(R.string.success_text)
            textResult.setTextColor(Color.GREEN)

            incPref(successSharedPreferenceKey)
        } else {
            textResult.text = getString(R.string.oh_no_text)
            textResult.setTextColor(Color.RED)

            incPref(ohNoSharedPreferenceKey)
        }
        clearText()
    }

    private fun incPref(key: String) {
        val count = this.prefs.getInt(key, 0) + 1
        savePrefValue(key, count)

        when (key) {
            successSharedPreferenceKey -> successCount.text = count.toString()
            ohNoSharedPreferenceKey -> ohNoCount.text = count.toString()
            clearsSharedPreferenceKey -> clearCount.text = count.toString()
        }
    }

    private fun savePrefValue(key: String, value: Int) {
        this.prefs.edit()?.let { editor ->
            editor.putInt(key, value)
            editor.apply()
        }
    }

    private fun savePrefValue(key: String, value: String) {
        this.prefs.edit()?.let { editor ->
            editor.putString(key, value)
            editor.apply()
        }
    }

    private fun displayCorrectValueDialog() {
        val view = this.layoutInflater.inflate(R.layout.dialog_correct_passcode, null)

        val inputField = view.findViewById<EditText>(R.id.dialogCorrectPasscodeValue)

        val dialog = AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_dialpad_black_24dp)
            .setView(view)
            .setPositiveButton("OK") { _, _ ->
                val value = inputField.text?.toString() ?: defaultCorrectValue
                savePrefValue(correctValuePreferenceKey, value)
                setCorrectValue(value)
            }.create()

        dialog.show()
    }
}
