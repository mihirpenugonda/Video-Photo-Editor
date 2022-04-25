package com.mhirrr.videophotoeditor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mhirrr.videophotoeditor.data.local.models.EditedPhotosModel

@Dao
interface EditedPhotosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePhoto(newPhoto: EditedPhotosModel)

    @Query("select * from editedphotosmodel")
    suspend fun getAllPhotos(): List<EditedPhotosModel>

    @Query("delete from editedphotosmodel where id = :document_id")
    suspend fun deletePhoto(document_id: Int)

}