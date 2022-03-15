package com.yudistudios.foodland.utils

import android.view.View

fun View.fadeOutThenInAnimation() {
    animate()
        .alpha(0f)
        .setDuration(300L)
        .withEndAction {
            fadeInAnimation()
        }
}

fun View.fadeOutAnimation() {
    animate()
        .alpha(0f)
        .setDuration(300L)
        .withEndAction {
            visibility = View.GONE
        }
}

fun View.fadeInAnimation() {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(300L)
        .withEndAction {
        }
}
