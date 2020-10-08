package com.n1Kk1.crazybirdy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceView

class MainGameView(context: Context?, private val x: Int = 0, private val y: Int = 0) : SurfaceView(context), Runnable {

    private lateinit var thread: Thread
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var background1: GameBackground
    private lateinit var background2: GameBackground
    private val screenRatioX = 2088f / x
    private val screenRatioY = 1080f / y
    private var isPlaying = false

    override fun run() {
        background1 = GameBackground(x, y, resources)
        background2 = GameBackground(x, y, resources)
        background2.x = x
        paint = Paint()

        while (isPlaying){
            update()
            draw()
            sleep()
        }
    }

    private fun update() {

        background1.x -= (10 * screenRatioX).toInt()
        background2.x -= (10 * screenRatioX).toInt()

        if (background1.x + background1.background.width < 0) {
            background1.x = x
        }

        if (background2.x + background2.background.width < 0) {
            background2.x = x
        }
    }

    private fun draw() {
        if(holder.surface.isValid){
            canvas = holder.lockCanvas()
            canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
            canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun sleep(){
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume(){
        isPlaying = true
        thread = Thread(this)
        thread.start()
    }

    fun pause(){
        try {
            isPlaying = false
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}