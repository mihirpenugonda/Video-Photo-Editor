package com.mhirrr.videophotoeditor.presentation.document

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
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(private val editedPhotosRepository: EditedPhotosRepository) :
    ViewModel() {

    private val _documents = MutableStateFlow<Resource<List<EditedPhotosModel>>>(Resource.Empty())
    val documents: StateFlow<Resource<List<EditedPhotosModel>>> = _documents

    fun getAllDocuments() {
        viewModelScope.launch {
            editedPhotosRepository.getAllPhotos().collect { response ->
                when (response) {
                    is Resource.Success -> {
                        _documents.value = Resource.Success(response.data!!)
                    }
                    else -> {

                    }
                }
            }
        }
    }

   fun renameDocument(document: EditedPhotosModel) {
        viewModelScope.launch {
            editedPhotosRepository.addPhoto(document).collect { addPhotoResponse ->
                when (addPhotoResponse) {
                    is Resource.Success -> {
                        getAllDocuments()
                    }
                    else -> {}
                }
            }
        }
    }

}