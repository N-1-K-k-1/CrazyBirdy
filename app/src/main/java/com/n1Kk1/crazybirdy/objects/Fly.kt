package com.n1Kk1.crazybirdy.objects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.n1Kk1.crazybirdy.view.MainGameView
import com.n1Kk1.crazybirdy.R
import kotlin.math.roundToInt
import kotlin.random.Random

class Fly(var y: Int, screenX: Int, screenY: Int, res: Resources?) {

    private var fly1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.fly1)
    private var fly2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.fly2)
    private var counter = 0
    private var screenRatioX = 0f
    private var screenRatioY = 0f
    var width: Int = 0
    var height: Int = 0
    var x: Int = 0
    private val until: Int = (25 * (2088f / screenX)).toInt()
    private val from: Int = (15 * (2088f / screenX)).toInt()
    var speed: Int = Random.nextInt(from + (MainGameView.difficulty * 0.2).roundToInt(), until + (MainGameView.difficulty * 0.2).roundToInt())

    init {
        width = fly1.width
        height = fly1.height

        width /= 18
        height /= 18

        screenRatioX = (2088f / screenX)
        screenRatioY = (1080f / screenY)

        width *= (screenRatioX).toInt()
        height *= (screenRatioY).toInt()

        fly1 = Bitmap.createScaledBitmap(fly1, width, height, false)
        fly2 = Bitmap.createScaledBitmap(fly2, width, height, false)

        x = screenX
    }

    fun getFly(): Bitmap {
        return when(counter) {
            0 -> {
                counter++
                fly1
            }
            else -> {
                counter--
                fly2
            }
        }
    }

    fun getRectCollision(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}