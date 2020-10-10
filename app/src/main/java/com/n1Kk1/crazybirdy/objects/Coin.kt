package com.n1Kk1.crazybirdy.objects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.n1Kk1.crazybirdy.R

class Coin(var y: Int, screenX: Int, screenY: Int, res: Resources?) {
    private var coin1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.coin1)
    private var coin2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.coin2)
    private var coin3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.coin3)
    private var coin4: Bitmap = BitmapFactory.decodeResource(res, R.drawable.coin4)
    private var coin5: Bitmap = BitmapFactory.decodeResource(res, R.drawable.coin5)
    private var coin6: Bitmap = BitmapFactory.decodeResource(res, R.drawable.coin6)
    private var counter = 0
    private var screenRatioX = 0f
    private var screenRatioY = 0f
    var width: Int = 0
    var height: Int = 0
    var x: Int = 0

    init {
        width = coin1.width
        height = coin1.height

        width /= 15
        height /= 15

        screenRatioX = (2088f / screenX)
        screenRatioY = (1080f / screenY)

        width *= (screenRatioX).toInt()
        height *= (screenRatioY).toInt()

        coin1 = Bitmap.createScaledBitmap(coin1, width, height, false)
        coin2 = Bitmap.createScaledBitmap(coin2, width, height, false)
        coin3 = Bitmap.createScaledBitmap(coin3, width, height, false)
        coin4 = Bitmap.createScaledBitmap(coin4, width, height, false)
        coin5 = Bitmap.createScaledBitmap(coin5, width, height, false)
        coin6 = Bitmap.createScaledBitmap(coin6, width, height, false)

        x = screenX
    }

    fun getCoin(): Bitmap {
        when(counter){
            0 -> {
                counter++
                return coin1
            }
            1 -> {
                counter++
                return coin2
            }
            2 -> {
                counter++
                return coin3
            }
            3 -> {
                counter++
                return coin4
            }
            4 -> {
                counter++
                return coin5
            }
            else -> {
                counter = 0
                return coin6
            }
        }
    }

    fun getRectCollision(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}