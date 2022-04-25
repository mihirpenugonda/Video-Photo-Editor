package com.mhirrr.videophotoeditor.data.repository

import android.util.Log
import com.mhirrr.videophotoeditor.data.local.dao.EditedPhotosDao
import com.mhirrr.videophotoeditor.data.local.models.EditedPhotosModel
import com.mhirrr.videophotoeditor.domain.EditedPhotosRepository
import com.mhirrr.videophotoeditor.utils.dataUtils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IEditedPhotosRepository @Inject constructor(private val editedPhotosDao: EditedPhotosDao) :
    EditedPhotosRepository {

    override suspend fun addPhoto(editedPhoto: EditedPhotosModel): Flow<Resource<Int>> = flow {
        try {
            Log.d("HERE", editedPhoto.toString())
            editedPhotosDao.savePhoto(editedPhoto)

            emit(Resource.Success(1))
        } catch (e: Exception) {
            Log.d("HEREAAA", e.printStackTrace().toString())
            emit(Resource.Error(e.stackTrace.toString()))
        }
    }

    override suspend fun getAllPhotos(): Flow<Resource<List<EditedPhotosModel>>> = flow {
        try {
            val editedPhotos = editedPhotosDao.getAllPhotos()
            if (editedPhotos.isEmpty()) emit(Resource.Empty())
            else emit(Resource.Success(editedPhotos))
        } catch (e: Exception) {
            Log.d("HERE", e.toString())
        }
    }

}