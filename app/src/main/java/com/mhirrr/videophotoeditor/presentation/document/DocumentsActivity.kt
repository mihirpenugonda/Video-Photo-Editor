package com.mhirrr.videophotoeditor.presentation.document

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.mhirrr.videophotoeditor.R
import com.mhirrr.videophotoeditor.databinding.ActivityDocumentsBinding
import com.mhirrr.videophotoeditor.domain.EditedPhotosRepository
import com.mhirrr.videophotoeditor.presentation.editor.EditingActivity
import com.mhirrr.videophotoeditor.utils.dataUtils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DocumentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentsBinding

    @Inject
    lateinit var editedPhotosRepository: EditedPhotosRepository

    private val adapter by lazy {
        DocumentsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.documentsRv.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.documentsRv.adapter = adapter

        // initial state setup
        lifecycleScope.launchWhenStarted {
            delay(100)
            editedPhotosRepository.getAllPhotos().collect { response ->
                Log.d("HERE", response.toString())
                when (response) {
                    is Resource.Success -> {
                        adapter.documents = response.data!!
                    }
                    else -> {

                    }
                }
            }
        }

        adapter.setOnDocumentClickListener { document ->
            val intent = Intent(this, EditingActivity::class.java)
            intent.putExtra("image_name", document.fileName)
            intent.putExtra("image_type", 2)
            intent.putExtra("image_filGrpAdjusters", document.filterValue.toFloatArray())
            intent.putExtra("image_id", document.id)
            startActivity(intent)
        }

        adapter.setOnDocumentMenuClickListener { photo, option ->
            when (option) {
                0 -> {
                    val builder = android.app.AlertDialog.Builder(this)
                    builder.setTitle("rename image")

                    val inputLayout =
                        LayoutInflater.from(this).inflate(R.layout.ad_edit_document_name, null)
                    builder.setView(inputLayout)
                    builder.setPositiveButton(
                        "OK"
                    ) { dialog, _ ->
                        val newName =
                            inputLayout.findViewById<TextInputEditText>(R.id.ad_rename_input).text.toString()

                        photo.name = newName
                        lifecycleScope.launch {
                            editedPhotosRepository.addPhoto(photo).collect { addPhotoResponse ->
                                Log.d("HERE IAM", addPhotoResponse.toString())
                                when (addPhotoResponse) {
                                    is Resource.Success -> {
                                        editedPhotosRepository.getAllPhotos().collect { response ->
                                            when (response) {
                                                is Resource.Success -> {
                                                    adapter.documents = response.data!!
                                                }
                                                else -> {

                                                }
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }


                        }

                        dialog.dismiss()
                    }
                    builder.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> dialog.cancel() }

                    builder.show()
                }
                1 -> {

                }
                2 -> {

                }
            }

        }
    }
}