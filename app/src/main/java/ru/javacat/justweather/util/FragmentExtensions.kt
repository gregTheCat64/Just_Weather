package ru.javacat.justweather.util

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.isPermissionGranted(p: String): Boolean{
    return ContextCompat.checkSelfPermission(requireContext(), p) == PackageManager.PERMISSION_GRANTED
}