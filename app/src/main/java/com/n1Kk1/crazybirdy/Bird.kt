package com.n1Kk1.crazybirdy

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Bird(screenX: Int, screenY: Int, res: Resources?) {

    private var bird1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird1)
    private var bird2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird2)
    private var bird3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird3)
    private var birdEat1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird_eat1)
    private var birdEat2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird_eat2)
    private var birdEat3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird_eat3)

    private val rect: Rect = Rect()
    private var counter = 0
    private var wingsDown = true
    private var screenRatioX = 0f
    private var screenRatioY = 0f
    var width: Int = 0
    var height: Int = 0
    var x: Int = 0
    var y: Int = 0

    init {
        width = bird1.width
        height = bird1.height

        width /= 11
        height /= 11

        screenRatioX = (2088f / screenX)
        screenRatioY = (1080f / screenY)

        width *= (screenRatioX).toInt()
        height *= (screenRatioY).toInt()

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false)
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false)
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false)

        birdEat1 = Bitmap.createScaledBitmap(birdEat1, width, height, false)
        birdEat2 = Bitmap.createScaledBitmap(birdEat2, width, height, false)
        birdEat3 = Bitmap.createScaledBitmap(birdEat3, width, height, false)

        y = screenY / 2 - height / 2
        x = 64 * (screenRatioX).toInt()
    }

    fun getBird(): Bitmap {
        when(counter){
            0 -> {
                counter++
                wingsDown = true
                return bird1
            }
            1 -> {
                if (wingsDown)
                    counter++
                else
                    counter--
                return bird2
            }
            else -> {
                counter--
                wingsDown = false
                return bird3
            }
        }
    }

    fun getBirdOpenMouth(): Bitmap {
        when(counter){
            0 -> {
                counter++
                wingsDown = true
                return birdEat1
            }
            1 -> {
                if (wingsDown)
                    counter++
                else
                    counter--
                return birdEat2
            }
            else -> {
                counter--
                wingsDown = false
                return birdEat3
            }
        }
    }

    fun getRect(): Rect {
        rect.set(((x + width - 40) * screenRatioX).toInt(), ((y + height / 1.1)* screenRatioY).toInt(), ((x + width + 100) * screenRatioX).toInt(), ((y + height / 1.7) * screenRatioY).toInt())

        return rect
    }

    fun getRectCollision(): Rect {
        return Rect(x, y, x + width, y + height)
    }

}