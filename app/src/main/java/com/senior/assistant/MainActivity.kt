
package com.senior.assistant

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private val serverUrl =
        "https://senior-ai.sadinovogabek24.workers.dev/chat"

    private val executor = Executors.newSingleThreadExecutor()
    private val history = JSONArray()

    private lateinit var chatText: TextView
    private lateinit var input: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(10, 15, 30))
        }

        val title = TextView(this).apply {
            text = "SENIOR"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(Color.CYAN)
        }

        val status = TextView(this).apply {
            text = "Gemini sun'iy intellekti bilan suhbat"
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        }

        chatText = TextView(this).apply {
            text = "Assalomu alaykum! Men SENIORman. Menga savolingizni yozing."
            textSize = 17f
            setTextColor(Color.WHITE)
            setPadding(12, 20, 12, 20)
        }

        val scroll = ScrollView(this).apply {
            addView(chatText)
        }

        input = EditText(this).apply {
            hint = "Xabaringizni yozing..."
            textSize = 17f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            setPadding(16, 12, 16, 12)
        }

        sendButton = Button(this).apply {
            text = "YUBORISH"
            setOnClickListener {
                sendMessage()
            }
        }

        layout.addView(title)
        layout.addView(status)

        layout.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        layout.addView(input)
        layout.addView(sendButton)

        setContentView(layout)
    }

    private fun sendMessage() {
        val message = input.text.toString().trim()

        if (message.isEmpty()) {
            Toast.makeText(this, "Xabar yozing", Toast.LENGTH_SHORT).show()
            return
        }

        input.setText("")
        sendButton.isEnabled = false

        chatText.append("\n\nSiz: $message\n\nSENIOR: Javob tayyorlanmoqda...")
        
        val oldHistory = JSONArray(history.toString())

        executor.execute {
            try {
                val connection =
                    URL(serverUrl).openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.connectTimeout = 20000
                connection.readTimeout = 60000
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=utf-8"
                )

                val requestBody = JSONObject().apply {
                    put("message", message)
                    put("history", oldHistory)
                }

                OutputStreamWriter(
                    connection.outputStream,
                    Charsets.UTF_8
                ).use {
                    it.write(requestBody.toString())
                }

                val code = connection.responseCode

                val stream = if (code in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val responseText = stream.bufferedReader().use {
                    it.readText()
                }

                connection.disconnect()

                val response = JSONObject(responseText)

                if (code !in 200..299) {
                    throw Exception(
                        response.optString(
                            "error",
                            "Server xatoligi: $code"
                        )
                    )
                }

                val answer = response.optString("answer")

                if (answer.isBlank()) {
                    throw Exception("Gemini javobi bo'sh")
                }

                history.put(JSONObject().apply {
                    put("role", "user")
                    put("text", message)
                })

                history.put(JSONObject().apply {
                    put("role", "model")
                    put("text", answer)
                })

                runOnUiThread {
                    chatText.append("\n\nSENIOR: $answer")
                    sendButton.isEnabled = true
                }

            } catch (e: Exception) {
                runOnUiThread {
                    chatText.append(
                        "\n\nXatolik: ${e.message}"
                    )
                    sendButton.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }
}
