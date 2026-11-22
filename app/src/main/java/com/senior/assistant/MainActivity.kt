package com.senior.assistant

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(30, 30, 30, 30)
            setBackgroundColor(Color.rgb(10, 15, 30))
        }

        val title = TextView(this).apply {
            text = "SENIOR"
            textSize = 38f
            setTextColor(Color.CYAN)
            gravity = Gravity.CENTER
        }

        val status = TextView(this).apply {
            text = "Shaxsiy ovozli yordamchingiz"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val info = TextView(this).apply {
            text = "\nOvozli boshqaruv tayyorlanmoqda...\n\n"
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(info)

        setContentView(layout)
    }
}
