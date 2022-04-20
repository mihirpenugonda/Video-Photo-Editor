package com.mhirrr.videophotoeditor.utils

import GPUImageFilterTools
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

object Constants {

    const val TAG = "videoEditor"
    const val DEFAULT_FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
    const val FILE_NAME_FORMAT = "yy-MM"
    const val REQUEST_CODE_PERMISSIONS = 100

    @RequiresApi(Build.VERSION_CODES.R)
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val Filters = listOf(
        GPUImageFilterTools.FilterType.BRIGHTNESS to 50f,
        GPUImageFilterTools.FilterType.CONTRAST to 50f,
        GPUImageFilterTools.FilterType.SHARPEN to 50f,
        GPUImageFilterTools.FilterType.HUE to 0f,
        GPUImageFilterTools.FilterType.PIXELATION to 0f,
        GPUImageFilterTools.FilterType.RGB to 100f,
        GPUImageFilterTools.FilterType.SATURATION to 50f
    )
}