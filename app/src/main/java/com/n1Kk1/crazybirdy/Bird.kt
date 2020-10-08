package com.n1Kk1.crazybirdy

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Bird(screenX: Int, screenY: Int, res: Resources?) {

    private var bird1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird1)
    private var bird2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird2)
    private var bird3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird3)
    private var counter = 0
    private var wingsDown = true
    var width = 0
    var height = 0
    var x: Int = 0
    var y: Int = 0

    init {
        width = bird1.width
        height = bird1.height

        width /= 11
        height /= 11

        width *= (2088f / screenX).toInt()
        height *= (1080f / screenY).toInt()

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false)
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false)
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false)

        y = screenY / 2 - height / 2
        x = 64 * (2088f / screenX).toInt()
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

}