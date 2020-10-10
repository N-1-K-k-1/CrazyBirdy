package com.n1Kk1.crazybirdy

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.SurfaceView
import kotlin.math.roundToInt
import kotlin.random.Random


@SuppressLint("ViewConstructor")
class MainGameView(context: Context?, private val x: Int = 0, private val y: Int = 0) : SurfaceView(context), Runnable {

    private lateinit var thread: Thread
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var background1: GameBackground
    private lateinit var background2: GameBackground
    private lateinit var bird: Bird
    private lateinit var fly: MutableList<Fly>
    private lateinit var killedFlies: MutableList<Fly>
    private val screenRatioX = 2088f / x
    private val screenRatioY = 1080f / y
    private var isPlaying = false
    private var isEating = false
    private var mLastTouchX = 0
    private var mLastTouchY = 0
    private var mActivePointerId = INVALID_POINTER_ID
    private var timeOut = 0
    private var timer = 0

    override fun run() {
        background1 = GameBackground(x, y, resources)
        background2 = GameBackground(x, y, resources)
        bird = Bird(x, y, resources)
        fly = mutableListOf()
        fly.add(Fly(Random.nextInt(bird.height,y - bird.height), x, y, resources))
        timeOut = Random.nextInt(30, 60)

        killedFlies = mutableListOf()
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

    @SuppressLint("ClickableViewAccessibility")
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

        isEating = false

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

        if (timer == timeOut) {
            fly.add(Fly(Random.nextInt(bird.height,y - bird.height), x, y, resources))
            timeOut = Random.nextInt((30 - (difficulty * 0.23)).roundToInt(), (60 - (difficulty * 0.23)).roundToInt())
            timer = 0
        }

        fly.forEach {
            it.x -= it.speed
            if (it.x + it.width < 0) {
                /*val bound = (30 * screenRatioX).toInt()
                it.speed = Random.nextInt(bound)

                if (it.speed < 15 * screenRatioX)
                    it.speed = (15 * screenRatioX * difficulty).toInt()*/

                it.x = x
            }

            if ((it.getRectCollision().left <= bird.getRect().right) && (it.getRectCollision().right >= bird.getRect().left)
                    && (((it.getRectCollision().top) in (bird.getRect().bottom..bird.getRect().top)) || ((it.getRectCollision().bottom) in (bird.getRect().bottom..bird.getRect().top)))
                    || Rect.intersects(it.getRectCollision(), bird.getRect()))
                isEating = true

            if ((bird.getRect().left in (it.getRectCollision().left..it.getRectCollision().right)) && (it.getRectCollision().centerY() in (bird.getRect().bottom..bird.getRect().top))) {
                killedFlies.add(it)
            }
        }

        killedFlies.forEach {
            fly.remove(it)
        }

        timer++
        if (difficulty <= 100.0)
            difficulty += 0.05

        println(difficulty)

    }


    private fun draw() {
        if(holder.surface.isValid){
            canvas = holder.lockCanvas()
            canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
            canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)

            fly.forEach {
                canvas.drawBitmap(it.getFly(), it.x.toFloat(), it.y.toFloat(), paint)
            }

            if (isEating)
                canvas.drawBitmap(bird.getBirdOpenMouth(), bird.x.toFloat(), bird.y.toFloat(), paint)
            else
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

    companion object {
        @JvmStatic
        var difficulty: Double = 1.0
    }

}