package com.mhirrr.videophotoeditor.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mhirrr.videophotoeditor.data.local.dao.EditedPhotosDao
import com.mhirrr.videophotoeditor.data.local.models.EditedPhotosModel

@Database(entities = [EditedPhotosModel::class], version = 2)
abstract class EditedPhotosDatabase: RoomDatabase() {

    abstract fun editedPhotosDao(): EditedPhotosDao

}