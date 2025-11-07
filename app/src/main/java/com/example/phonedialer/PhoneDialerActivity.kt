package com.example.phonedialer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PhoneDialerActivity : ComponentActivity() {

    private lateinit var phoneNumberEditText: EditText

    companion object {
        private const val REQ_CALL_PHONE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_dialer)

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        phoneNumberEditText.isEnabled = false

        val listener = ButtonClickListener()

        // Cifre + simboluri
        val buttonIds = listOf(
            R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6,
            R.id.button7, R.id.button8, R.id.button9,
            R.id.buttonZero, R.id.buttonStar, R.id.buttonHash
        )
        buttonIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener(listener)
        }

        // backspace, call, end
        findViewById<ImageButton>(R.id.imageButtonEdit).setOnClickListener(listener)
        findViewById<ImageButton>(R.id.imageButtonCall).setOnClickListener(listener)   // CALL
        findViewById<ImageButton>(R.id.imageButtonEnd).setOnClickListener(listener)  // END
    }

    // Clasa internă care tratează TOATE click-urile
    private inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(view: View?) {
            when (view?.id) {
                // Cifre + simboluri
                R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6,
                R.id.button7, R.id.button8, R.id.button9,
                R.id.buttonZero, R.id.buttonStar, R.id.buttonHash -> {
                    val text = (view as Button).text.toString()
                    phoneNumberEditText.append(text)
                }

                // Backspace (șterge ultimul caracter)
                R.id.imageButtonEdit -> {
                    val txt = phoneNumberEditText.text.toString()
                    if (txt.isNotEmpty()) {
                        phoneNumberEditText.setText(txt.dropLast(1))
                    }
                }

                // END (închide activitatea)
                R.id.imageButtonEnd -> finish()

                // CALL (apelează)
                R.id.imageButtonCall -> {
                    val number = phoneNumberEditText.text.toString()
                    if (number.isBlank()) {
                        Toast.makeText(this@PhoneDialerActivity, "No number entered", Toast.LENGTH_SHORT).show()
                        return
                    }
                    // cerem permisiunea CALL_PHONE dacă nu e acordată
                    if (ContextCompat.checkSelfPermission(
                            this@PhoneDialerActivity,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@PhoneDialerActivity,
                            arrayOf(Manifest.permission.CALL_PHONE),
                            REQ_CALL_PHONE
                        )
                    } else {
                        startCall(number)
                    }
                }
            }
        }
    }

    private fun startCall(number: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$number")
        }
        try {
            startActivity(intent)
        } catch (e: SecurityException) {
            Toast.makeText(this, "CALL_PHONE permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Dacă utilizatorul răspunde la cererea de permisiune:
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CALL_PHONE) {
            val number = phoneNumberEditText.text.toString()
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && number.isNotBlank()) {
                startCall(number)
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
