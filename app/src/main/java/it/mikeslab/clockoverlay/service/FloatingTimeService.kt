package it.mikeslab.clockoverlay.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.os.SystemClock
import android.view.*
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import it.mikeslab.clockoverlay.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class FloatingTimeService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var closeButton: ImageView? = null
    private var resizeHandle: View? = null
    private var params: WindowManager.LayoutParams? = null
    private var timeTextView: TextView? = null

    override fun onCreate() {
        super.onCreate()

        // Create notification channel
        val channel = NotificationChannel(
            "floating_time_service_channel",
            "Floating Time Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        // Create notification
        val notification: Notification = NotificationCompat.Builder(this, "floating_time_service_channel")
            .setContentTitle("Floating Time Service")
            .setContentText("Displaying floating time overlay")
            .setSmallIcon(R.drawable.ic_expand)
            .build()

        // Start foreground service
        startForeground(1, notification)

        // Initialize WindowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Inflate the floating layout
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_time_layout, null)

        // Set layout parameters for the overlay window
        params = WindowManager.LayoutParams(
            (300 * resources.displayMetrics.density).toInt(),
            (180 * resources.displayMetrics.density).toInt(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params!!.gravity = Gravity.TOP or Gravity.START
        params!!.x = 0
        params!!.y = 100

        // Add the view to the window
        windowManager.addView(floatingView, params)

        // Get the time TextView
        timeTextView = floatingView!!.findViewById(R.id.time_text)

        adjustTextSize()

        // Set initial time text
        val handler = android.os.Handler(mainLooper)
        val updateTimeTask = object : Runnable {
            override fun run() {
                val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                timeTextView?.text = currentTime
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateTimeTask)

        // Draggable overlay
        floatingView!!.setOnTouchListener(object : View.OnTouchListener {
            private var lastX = 0
            private var lastY = 0
            private var offsetX = 0
            private var offsetY = 0
            private var isMoving = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                        offsetX = params!!.x
                        offsetY = params!!.y
                        isMoving = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX.toInt() - lastX
                        val dy = event.rawY.toInt() - lastY
                        if (dx.absoluteValue > 5 || dy.absoluteValue > 5) {
                            isMoving = true
                        }
                        params!!.x = offsetX + dx
                        params!!.y = offsetY + dy
                        windowManager.updateViewLayout(floatingView, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> return isMoving
                }
                return false
            }
        })

        // Close Button
        closeButton = floatingView!!.findViewById(R.id.close_button)
        closeButton!!.setOnClickListener {
            stopSelf()  // Close the overlay
        }

        val chronometer: Chronometer = floatingView!!.findViewById(R.id.chronometer)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.visibility = View.VISIBLE
        chronometer.start()

        // Resizing functionality
        resizeHandle = floatingView!!.findViewById(R.id.resize_handle)
        resizeHandle!!.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialWidth = 0
            private var initialHeight = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = event.rawX.toInt()
                        initialY = event.rawY.toInt()
                        initialWidth = params!!.width
                        initialHeight = params!!.height
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = event.rawX.toInt() - initialX
                        val deltaY = event.rawY.toInt() - initialY
                        params!!.width = initialWidth + deltaX
                        params!!.height = initialHeight + deltaY
                        windowManager.updateViewLayout(floatingView, params)

                        // Adjust the text size based on the new size
                        adjustTextSize()

                        return true
                    }
                }
                return false
            }
        })
    }

    private fun adjustTextSize() {
        // Get the new width and height of the overlay
        val width = params!!.width
        val height = params!!.height

        // Set a new text size based on the width (or height) of the overlay
        val newTextSize = (width / 10).coerceAtMost(200)  // Adjust text size, max 30sp
        val chronometer: Chronometer = floatingView!!.findViewById(R.id.chronometer)

        chronometer?.textSize = newTextSize.toFloat()
        timeTextView?.textSize = newTextSize.toFloat()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager.removeView(floatingView)
        }
    }
}