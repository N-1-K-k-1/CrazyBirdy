package com.n1Kk1.crazybirdy.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.SoundPool
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.n1Kk1.crazybirdy.MainActivity
import com.n1Kk1.crazybirdy.R
import com.n1Kk1.crazybirdy.objects.Bird
import com.n1Kk1.crazybirdy.objects.Coin
import com.n1Kk1.crazybirdy.objects.Fly
import io.paperdb.Paper
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt
import kotlin.random.Random


@SuppressLint("ViewConstructor")
class MainGameView(context: Context?, private val x: Int = 0, private val y: Int = 0) : SurfaceView(context), Runnable {

    private lateinit var thread: Thread
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var paintStroke: Paint
    private lateinit var background1: GameBackground
    private lateinit var background2: GameBackground
    private lateinit var bird: Bird
    private lateinit var fly: MutableList<Fly>
    private lateinit var killedFlies: MutableList<Fly>
    private lateinit var coins: MutableList<Coin>
    private lateinit var collectedCoins: MutableList<Coin>
    private lateinit var sounds: SoundPool
    private val screenRatioX = 2088f / x
    private val screenRatioY = 1080f / y
    private var isPlaying = false
    private var isEating = false
    private var isGameOver = false
    private var isMute = false
    private var mLastTouchX = 0
    private var mLastTouchY = 0
    private var mActivePointerId = INVALID_POINTER_ID
    private var timeOut = 0
    private var timer = 0
    private var coinTimeOut = 0
    private var coinTimer = 0
    private var score = 0
    private var sound = 0
    private var soundEat = 0
    private var soundOfDefeat = 0

    override fun run() {
        background1 = GameBackground(x, y, resources)
        background2 = GameBackground(x, y, resources)
        bird = Bird(x, y, resources)
        fly = mutableListOf()
        fly.add(Fly(Random.nextInt(bird.height, y - bird.height), x, y, resources))
        killedFlies = mutableListOf()
        timeOut = Random.nextInt(30, 60)
        coins = mutableListOf()
        coins.add(Coin(Random.nextInt(bird.height, y - bird.height), x, y, resources))
        collectedCoins = mutableListOf()
        coinTimeOut = Random.nextInt(20, 50)
        mLastTouchX = bird.x
        mLastTouchY = bird.y
        background2.x = x
        paint = Paint()
        paintStroke = Paint()
        paint.textSize = 100F
        paint.color = Color.WHITE
        paintStroke.textSize = 100F
        paintStroke.style = Paint.Style.STROKE
        paintStroke.color = Color.BLACK
        paintStroke.strokeWidth = 3F

        val attrs = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()

        sounds = SoundPool.Builder().setAudioAttributes(attrs).build()
        sound = sounds.load(context, R.raw.coin_money_4, 1)
        soundEat = sounds.load(context, R.raw.crunch, 2)
        soundOfDefeat = sounds.load(context, R.raw.sound_of_defeat, 1)
        isMute = Paper.book().read<Boolean>("isMute")

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
            fly.add(Fly(Random.nextInt(bird.height, y - bird.height), x, y, resources))
            timeOut = Random.nextInt((30 - (difficulty * 0.23)).roundToInt(), (60 - (difficulty * 0.23)).roundToInt())
            timer = 0
        }

        runBlocking {
            fly.forEach {
                launch {
                    it.x -= it.speed

                    if ((it.getRectCollision().left <= bird.getRect().right) && (it.getRectCollision().right >= bird.getRect().left)
                            && (((it.getRectCollision().top) in (bird.getRect().bottom..bird.getRect().top)) || ((it.getRectCollision().bottom) in (bird.getRect().bottom..bird.getRect().top)))
                            || Rect.intersects(it.getRectCollision(), bird.getRect()))
                        isEating = true

                    if ((bird.getRect().left in (it.getRectCollision().left..it.getRectCollision().right)) && (it.getRectCollision().centerY() in (bird.getRect().bottom..bird.getRect().top))) {
                        if (!isMute)
                            sounds.play(soundEat, 1F, 1F, 0, 0, 1F)

                        killedFlies.add(it)
                    }

                    if (it.x + it.width < 0) {
                        isGameOver = true
                        sounds.play(soundOfDefeat, 1F, 1F, 0, 0, 1F)
                    }

                }
            }
        }

        if (coinTimer == coinTimeOut) {
            coins.add(Coin(Random.nextInt(bird.height, y - bird.height), x, y, resources))
            coinTimeOut = Random.nextInt(30, 80)
            coinTimer = 0
        }

        val trash = mutableListOf<Coin>()

        runBlocking {
            coins.forEach {
                launch {
                    it.x -= (10 * screenRatioX).toInt()

                    if (Rect.intersects(bird.getRectCollision(), it.getRectCollision())) {
                        if (!isMute)
                            sounds.play(sound, 1F, 1F, 0, 0, 1F)

                        collectedCoins.add(it)
                        score++
                    }

                    if (it.x + it.width < 0)
                        trash.add(it)
                }
            }
        }

        runBlocking {
            killedFlies.forEach { it1 ->
                launch {
                    fly.remove(it1)
                }
            }

            trash.forEach { coin ->
                launch {
                    coins.remove(coin)
                }
            }

            collectedCoins.forEach { coin ->
                launch {
                    coins.remove(coin)
                }
            }
        }

        timer++
        coinTimer++
        if (difficulty <= 100.0)
            difficulty += 0.05
    }


    private fun draw() {
        if(holder.surface.isValid){
            canvas = holder.lockCanvas()

            runBlocking {
                launch {
                    canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
                }

                launch {
                    canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)
                }

                launch {
                    drawObject(coins)
                }


                launch {
                    drawObject(fly)
                }

                launch {
                    canvas.drawText(score.toString(), (x - 120).toFloat(), 120F, paint)
                }

                launch {
                    canvas.drawText(score.toString(), (x - 120).toFloat(), 120F, paintStroke)
                }

                if (isEating)
                    launch {
                        canvas.drawBitmap(bird.getBirdOpenMouth(), bird.x.toFloat(), bird.y.toFloat(), paint)
                    }
                else
                    launch {
                        canvas.drawBitmap(bird.getBird(), bird.x.toFloat(), bird.y.toFloat(), paint)
                    }
            }


            if (isGameOver) {
                isPlaying = false
                runBlocking {
                    launch {
                        if (Paper.book().read("highScore", 0) < score) {
                            Paper.book().write("highScore", score)
                        }
                    }
                }
                when {
                    score == 0 -> {
                        canvas.drawText(context.getString(R.string.youScored, score), (x / 2 - (context.getString(R.string.youScored, score).length * 22)).toFloat(), (y / 2 - 100).toFloat(), paint)
                        canvas.drawText(context.getString(R.string.youScored, score), (x / 2 - (context.getString(R.string.youScored, score).length * 22)).toFloat(), (y / 2 - 100).toFloat(), paintStroke)
                    }
                    score == 1 -> {
                        canvas.drawText(context.getString(R.string.youScored2, score), (x / 2 - (context.getString(R.string.youScored2, score).length * 22)).toFloat(), (y / 2).toFloat(), paint)
                        canvas.drawText(context.getString(R.string.youScored2, score), (x / 2 - (context.getString(R.string.youScored2, score).length * 22)).toFloat(), (y / 2).toFloat(), paintStroke)
                    }
                    score in 2..4 -> {
                        canvas.drawText(context.getString(R.string.youScored1, score), (x / 2 - (context.getString(R.string.youScored1, score).length * 22)).toFloat(), (y / 2).toFloat(), paint)
                        canvas.drawText(context.getString(R.string.youScored1, score), (x / 2 - (context.getString(R.string.youScored1, score).length * 22)).toFloat(), (y / 2).toFloat(), paintStroke)
                    }
                    score > 4  -> {
                        canvas.drawText(context.getString(R.string.youScored, score), (x / 2 - (context.getString(R.string.youScored, score).length * 22)).toFloat(), (y / 2).toFloat(), paint)
                        canvas.drawText(context.getString(R.string.youScored, score), (x / 2 - (context.getString(R.string.youScored, score).length * 22)).toFloat(), (y / 2).toFloat(), paintStroke)
                    }
                }

                if (score < 11) {
                    canvas.drawText(context.getString(R.string.tryNotTo), (x / 2 - (context.getString(R.string.tryNotTo).length * 20)).toFloat(), (y / 2 + 100).toFloat(), paint)
                    canvas.drawText(context.getString(R.string.tryNotTo), (x / 2 - (context.getString(R.string.tryNotTo).length * 20)).toFloat(), (y / 2 + 100).toFloat(), paintStroke)
                }

                holder.unlockCanvasAndPost(canvas)
                try {
                    Thread.sleep(3000)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as AppCompatActivity).finish()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                return
            }

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

    private fun <T> drawObject(arr: MutableList<T>) {
        arr.forEach {
            if (it is Coin)
                canvas.drawBitmap(it.getCoin(), it.x.toFloat(), it.y.toFloat(), paint)
            if (it is Fly)
                canvas.drawBitmap(it.getFly(), it.x.toFloat(), it.y.toFloat(), paint)
        }
    }

}