package com.example.projetfinsession

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        val thread = Thread {
            try {
                Thread.sleep(4000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                val intent = Intent(
                    this@SplashScreen,
                    species::class.java
                )
                startActivity(intent)
                finish()
            }
        }
        thread.start()

    }
}