package ru.javacat.justweather.ui.util

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.isPermissionGranted(p: String): Boolean{
    return ContextCompat.checkSelfPermission(requireContext(), p) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.snack(msg: String) {
    Snackbar.make(this.requireView(), msg, Snackbar.LENGTH_SHORT).show()
}

