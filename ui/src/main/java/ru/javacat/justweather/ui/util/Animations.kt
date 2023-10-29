package ru.javacat.justweather.ui.util

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import ru.javacat.ui.R


fun View.refreshAnimation(context: Context){
    this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate))
}

fun View.pushAnimation(context: Context){
    this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.push))
}

fun View.changeColorOnPush(context: Context){
    val color = context.resources.getColor(R.color.md_theme_light_primary)
    this.setBackgroundColor(color)
}