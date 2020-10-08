package com.n1Kk1.crazybirdy

import android.graphics.Insets
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity


class MainGameActivity : AppCompatActivity() {

    private lateinit var gameView: MainGameView
    private lateinit var metrics: WindowMetrics
    private lateinit var windowInsets: WindowInsets
    private lateinit var insets: Insets

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        metrics = windowManager.currentWindowMetrics

        windowInsets = metrics.windowInsets
        insets = windowInsets.getInsetsIgnoringVisibility(
            WindowInsets.Type.navigationBars()
                    or WindowInsets.Type.displayCutout()
        )
        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom
        val bounds: Rect = metrics.bounds
        val legacySize = Size(
            bounds.width() - insetsWidth,
            bounds.height() - insetsHeight
        )

        val point: Point = Point()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getSize(point)
        println("${point.x} ${point.y}")

        println("${legacySize.width} ${legacySize.height}")
        gameView = MainGameView(this, legacySize.width, legacySize.height)

        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }
}