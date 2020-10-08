package com.n1Kk1.crazybirdy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.SurfaceView

@SuppressLint("ViewConstructor")
class MainGameView(context: Context?, private val x: Int = 0, private val y: Int = 0) : SurfaceView(context), Runnable {

    private lateinit var thread: Thread
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var background1: GameBackground
    private lateinit var background2: GameBackground
    private lateinit var bird: Bird
    private val screenRatioX = 2088f / x
    private val screenRatioY = 1080f / y
    private var isPlaying = false
    private var mLastTouchX = 0
    private var mLastTouchY = 0
    private var mActivePointerId = INVALID_POINTER_ID

    override fun run() {
        background1 = GameBackground(x, y, resources)
        background2 = GameBackground(x, y, resources)
        bird = Bird(x, y, resources)
        mLastTouchX = bird.x
        mLastTouchY = bird.y
        background2.x = x
        paint = Paint()

        while (isPlaying){
            update()
            draw()
            sleep()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                event.actionIndex.also { pointerIndex ->
                    // Remember where all started (for dragging)
                    mLastTouchY = event.getY(pointerIndex).toInt()
                }

                // Save the ID of this pointer (for dragging)
                mActivePointerId = event.getPointerId( 0)
            }

            MotionEvent.ACTION_MOVE -> {
                val y: Float =
                        event.findPointerIndex(mActivePointerId).let { pointerIndex ->
                            // Calculate the distance moved
                            event.getY(pointerIndex)
                        }
                bird.y += (y - mLastTouchY).toInt()
                if (y < 0)
                    bird.y = 0

                mLastTouchY = y.toInt()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {
                event.actionIndex.also { pointerIndex ->
                    event.getPointerId(pointerIndex)
                            .takeIf { it == mActivePointerId }
                            ?.run {
                                val newPointerIndex = if (pointerIndex == 0) 1 else 0
                                mLastTouchY = event.getY(newPointerIndex).toInt()
                                mActivePointerId = event.getPointerId(newPointerIndex)
                            }
                }
            }

        }

        return true
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

        if (bird.y < 0)
            bird.y = 0

        if (bird.y > y - bird.height)
            bird.y = y - bird.height
    }


    private fun draw() {
        if(holder.surface.isValid){
            canvas = holder.lockCanvas()
            canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
            canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)

            canvas.drawBitmap(bird.getBird(), bird.x.toFloat(), bird.y.toFloat(), paint)

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