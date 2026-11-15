package com.theo.gamesidekick

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class SidebarService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: View? = null
    private var panel: View? = null
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var panelParams: WindowManager.LayoutParams
    private var expanded = false

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun overlayType() = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        createChannel()
        startForeground(41, notification())
        showBubble()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("sidekick", "Game Sidekick", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    private fun notification(): Notification {
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, "sidekick")
            .setSmallIcon(android.R.drawable.ic_menu_view).setContentTitle("Game Sidekick is running")
            .setContentText("Tap the floating edge tab to open app shortcuts")
            .setContentIntent(open).setOngoing(true).build()
        else Notification.Builder(this).setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentTitle("Game Sidekick is running").setContentIntent(open).setOngoing(true).build()
    }

    private fun showBubble() {
        val tab = TextView(this).apply {
            text = "◀"
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = rounded(0xEE263B63.toInt(), 22)
            setOnClickListener { if (expanded) hidePanel() else showPanel() }
            setOnTouchListener(DragTouch())
        }
        bubble = tab
        bubbleParams = WindowManager.LayoutParams(dp(48), dp(62), overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0; y = dp(220)
        }
        wm.addView(tab, bubbleParams)
    }

    private fun showPanel() {
        if (panel != null) return
        expanded = true
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(0xF21A2030.toInt(), 20)
            setPadding(dp(10), dp(10), dp(10), dp(10))
        }
        val head = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        head.addView(TextView(this).apply {
            text = "GAME SIDEKICK"
            textSize = 13f; setTextColor(Color.WHITE); typeface = Typeface.DEFAULT_BOLD
        }, LinearLayout.LayoutParams(0, dp(42), 1f))
        head.addView(TextView(this).apply {
            text = "×"; textSize = 26f; gravity = Gravity.CENTER; setTextColor(Color.WHITE)
            setOnClickListener { hidePanel() }
        }, LinearLayout.LayoutParams(dp(38), dp(42)))
        outer.addView(head)
        outer.addView(TextView(this).apply {
            text = "Tap an app to launch it. Pop-up behavior depends on your phone's window support."
            textSize = 11f; setTextColor(0xFFBFCBE0.toInt()); setPadding(0, 0, 0, dp(8))
        })
        val scroll = ScrollView(this)
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)
        val apps = try {
            packageManager.getInstalledApplications(0)
                .filter { it.packageName != packageName && packageManager.getLaunchIntentForPackage(it.packageName) != null }
                .sortedBy { packageManager.getApplicationLabel(it).toString().lowercase() }
        } catch (_: Exception) { emptyList<ApplicationInfo>() }
        apps.forEach { info ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(6), dp(5), dp(6), dp(5))
                background = rounded(0xFF29344A.toInt(), 12)
                val icon = android.widget.ImageView(this@SidebarService).apply {
                    setImageDrawable(packageManager.getApplicationIcon(info))
                }
                addView(icon, LinearLayout.LayoutParams(dp(34), dp(34)))
                addView(TextView(this@SidebarService).apply {
                    text = packageManager.getApplicationLabel(info).toString()
                    textSize = 14f; setTextColor(Color.WHITE); setPadding(dp(10), 0, 0, 0)
                }, LinearLayout.LayoutParams(0, dp(42), 1f))
                setOnClickListener { launch(info.packageName) }
            }
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48))
            lp.bottomMargin = dp(6)
            list.addView(row, lp)
        }
        if (apps.isEmpty()) list.addView(TextView(this).apply { text = "No launchable apps found."; setTextColor(Color.WHITE) })
        outer.addView(scroll, LinearLayout.LayoutParams(LinearLayout.MATCH_PARENT, dp(390)))
        panel = outer
        panelParams = WindowManager.LayoutParams(dp(270), dp(470), overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.END
            x = dp(54); y = dp(170)
        }
        try { wm.addView(outer, panelParams) } catch (_: Exception) { panel = null; expanded = false }
    }

    private fun launch(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try { startActivity(intent) } catch (_: Exception) { }
    }

    private fun hidePanel() {
        panel?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        panel = null; expanded = false
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat()
    }

    private inner class DragTouch : View.OnTouchListener {
        private var sx = 0f; private var sy = 0f; private var ox = 0; private var oy = 0
        private var moved = false
        override fun onTouch(v: View, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { sx = e.rawX; sy = e.rawY; ox = bubbleParams.x; oy = bubbleParams.y; moved = false; return true }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (e.rawX - sx).toInt(); val dy = (e.rawY - sy).toInt()
                    if (kotlin.math.abs(dx) + kotlin.math.abs(dy) > dp(5)) moved = true
                    bubbleParams.x = (ox - dx).coerceAtLeast(0); bubbleParams.y = (oy + dy).coerceAtLeast(0)
                    try { wm.updateViewLayout(bubble, bubbleParams) } catch (_: Exception) {}
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) v.performClick()
                    return true
                }
            }
            return false
        }
    }

    override fun onDestroy() {
        hidePanel()
        bubble?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        bubble = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
