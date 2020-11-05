package com.example.giphysample.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

fun View.gone() : View {
    this.visibility = View.GONE
    return this
}

fun View.visible() : View {
    this.visibility = View.VISIBLE
    return this
}

fun View.startShowAnimation() {
    alpha = 0f

    val animatorSet = AnimatorSet().apply {
        duration = 250
        startDelay = 100

        playTogether(
            ObjectAnimator.ofPropertyValuesHolder(
                this@startShowAnimation,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 100f, 0f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
            )
        )
    }

    animatorSet.start()
}