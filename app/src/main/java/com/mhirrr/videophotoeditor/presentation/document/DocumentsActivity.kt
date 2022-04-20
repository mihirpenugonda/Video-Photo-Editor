package com.mhirrr.videophotoeditor.presentation.document

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mhirrr.videophotoeditor.data.local.models.EditedPhotosModel
import com.mhirrr.videophotoeditor.databinding.ActivityDocumentsBinding
import com.mhirrr.videophotoeditor.domain.EditedPhotosRepository
import com.mhirrr.videophotoeditor.utils.dataUtils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class DocumentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentsBinding

    @Inject
    lateinit var editedPhotosRepository: EditedPhotosRepository

    private var documents: List<EditedPhotosModel>? = null

    private val adapter by lazy {
        DocumentsAdapter()
    }

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.documentsRv.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.documentsRv.adapter = adapter

        lifecycleScope.launchWhenStarted {
            delay(100)
            editedPhotosRepository.getAllPhotos().collect { response ->
                Log.d(
                    "HERE",
                    response.data.toString()
                )
                when (response) {
                    is Resource.Success -> {
                        adapter.documents = response.data!!
                    }
                    else -> {

                    }
                }
            }
        }

    }
}