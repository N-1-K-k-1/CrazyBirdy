package com.n1Kk1.crazybirdy

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory


class GameBackground internal constructor(
    screenX: Int,
    screenY: Int,
    res: Resources?
) {
    var x = 0
    var y = 0
    var background: Bitmap = BitmapFactory.decodeResource(res, R.drawable.hills)

    init {
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false)
    }
}