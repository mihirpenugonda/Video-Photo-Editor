package com.mhirrr.videophotoeditor.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mhirrr.videophotoeditor.data.local.converters.EditedPhotosConverter

@Entity
@TypeConverters(EditedPhotosConverter::class)
data class EditedPhotosModel(

    var name: String,
    var filterValue: List<Float>,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    )
