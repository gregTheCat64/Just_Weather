package ru.javacat.justweather.ui.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

fun ImageView.load(url: String, vararg  transforms: BitmapTransformation = emptyArray()) =
    Glide.with(this)
        .load("https://$url")
        .circleCrop()
        .transform(*transforms)
        .into(this)