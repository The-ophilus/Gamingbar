package com.theo.gamesidekick

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(28), dp(22), dp(22))
            setBackgroundColor(0xFF10131B.toInt())
        }
        root.addView(TextView(this).apply {
            text = "GAME SIDEKICK"
            textSize = 25f
            setTextColor(0xFFFFFFFF.toInt())
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        root.addView(TextView(this).apply {
            text = "Your shortcuts while gaming"
            textSize = 15f
            setTextColor(0xFFB8C2D8.toInt())
            setPadding(0, dp(6), 0, dp(22))
        })
        val status = TextView(this).apply {
            textSize = 14f
            setTextColor(0xFFE4E9F4.toInt())
            text = "Step 1: Allow display over other apps.\nStep 2: Start the gaming sidebar.\nStep 3: Open your game. Tap the small tab on the screen edge to choose an app."
            setPadding(0, 0, 0, dp(22))
        }
        root.addView(status)
        val permission = makeButton("1  •  Allow display over other apps") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else status.text = "Overlay permission is already enabled. Start the sidebar below."
        }
        root.addView(permission)
        root.addView(makeButton("2  •  Start gaming sidebar") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                status.text = "Please enable 'Display over other apps' first."
            } else {
                val i = Intent(this, SidebarService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i) else startService(i)
                status.text = "Sidebar started. You can now leave this app and open your game."
            }
        })
        root.addView(makeButton("Stop sidebar") { stopService(Intent(this, SidebarService::class.java)) })
        root.addView(TextView(this).apply {
            text = "Note: Android/HiOS decides whether another app can open in a true pop-up window. If your phone does not support freeform windows for that app, selecting it may switch away from the game. This app cannot override that system restriction."
            textSize = 13f
            setTextColor(0xFFFFD58A.toInt())
            setPadding(0, dp(24), 0, 0)
        })
        setContentView(root)
    }

    private fun makeButton(label: String, action: () -> Unit): Button = Button(this).apply {
        text = label
        isAllCaps = false
        setOnClickListener { action() }
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(54)).apply {
            bottomMargin = dp(10)
        }
    }
}
