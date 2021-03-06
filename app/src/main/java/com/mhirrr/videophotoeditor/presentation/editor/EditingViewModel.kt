package com.mhirrr.videophotoeditor.presentation.editor

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhirrr.videophotoeditor.data.local.models.EditedPhotosModel
import com.mhirrr.videophotoeditor.domain.EditedPhotosRepository
import com.mhirrr.videophotoeditor.utils.dataUtils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EditingViewModel @Inject constructor(private val editedPhotosRepository: EditedPhotosRepository) :
    ViewModel() {

    private val _saveImageState = MutableStateFlow<Resource<Uri>>(Resource.Empty())
    val saveImageState: StateFlow<Resource<Uri>> = _saveImageState

    fun saveImage(bitmap: Bitmap, filGrpAdjustersValue: MutableList<Float>?, fileName: String) {
        val storageLoc =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val file = File(storageLoc, fileName)
        val newPhoto = EditedPhotosModel(fileName, fileName, filGrpAdjustersValue!!.toList())

        saveImageToGallery(newPhoto, bitmap, file)
    }

    fun updateImage(
        bitmap: Bitmap,
        filGrpAdjustersValue: MutableList<Float>?,
        fileName: String,
        fileId: Int
    ) {
        val storageLoc =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val file = File(storageLoc, fileName)

        file.delete()
        val newPhoto =
            EditedPhotosModel(fileName, fileName, filGrpAdjustersValue!!.toList(), fileId)

        saveImageToGallery(newPhoto, bitmap, file)
    }

    private fun saveImageToGallery(newPhoto: EditedPhotosModel, bitmap: Bitmap, file: File) {
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()


            viewModelScope.launch {
                try {
                    editedPhotosRepository.addPhoto(newPhoto).collect {}
                } catch (e: Exception) {
                    _saveImageState.value = Resource.Error("error saving image in db")
                }
            }

            _saveImageState.value = Resource.Success(Uri.fromFile(file))
        } catch (e: FileNotFoundException) {
            _saveImageState.value = Resource.Error(e.printStackTrace().toString())
        } catch (e: IOException) {
            _saveImageState.value = Resource.Error(e.printStackTrace().toString())
        }
    }


}