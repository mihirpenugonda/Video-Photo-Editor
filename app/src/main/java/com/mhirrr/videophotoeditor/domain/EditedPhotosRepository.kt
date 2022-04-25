package com.mhirrr.videophotoeditor.domain

import com.mhirrr.videophotoeditor.data.local.models.EditedPhotosModel
import com.mhirrr.videophotoeditor.utils.dataUtils.Resource
import kotlinx.coroutines.flow.Flow

interface EditedPhotosRepository {

    suspend fun addPhoto(editedPhoto: EditedPhotosModel): Flow<Resource<Int>>

    suspend fun getAllPhotos(): Flow<Resource<List<EditedPhotosModel>>>

    suspend fun deletePhoto(document_id: Int): Flow<Resource<Int>>

}