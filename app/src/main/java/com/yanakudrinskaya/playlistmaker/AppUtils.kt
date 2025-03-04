package com.yanakudrinskaya.playlistmaker

import android.content.Context
import android.util.TypedValue

object AppUtils {

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}