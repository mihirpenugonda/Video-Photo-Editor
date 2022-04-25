package com.mhirrr.videophotoeditor.presentation.document

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.mhirrr.videophotoeditor.R
import com.mhirrr.videophotoeditor.data.local.models.EditedPhotosModel
import com.mhirrr.videophotoeditor.databinding.ActivityDocumentsBinding
import com.mhirrr.videophotoeditor.presentation.editor.EditingActivity
import com.mhirrr.videophotoeditor.utils.dataUtils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class DocumentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentsBinding

    private val adapter by lazy {
        DocumentsAdapter()
    }

    private val viewModel: DocumentsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.documentsRv.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.documentsRv.adapter = adapter

        // initial state setup
        lifecycleScope.launchWhenStarted {
            delay(100)
            viewModel.getAllDocuments()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.documents.collect { response ->
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
            editImage(document)
        }

        adapter.setOnDocumentMenuClickListener { document, option ->
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

                        document.name = newName
                        lifecycleScope.launch {
                            viewModel.renameDocument(document)
                        }

                        dialog.dismiss()
                    }
                    builder.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> dialog.cancel() }

                    builder.show()
                }
                1 -> {
                    editImage(document)
                }
                2 -> {
                    val storageLoc =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val editedPhotoFile = File(storageLoc, document.fileName)

                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/png"
                    intent.putExtra(Intent.EXTRA_STREAM, editedPhotoFile.toURI())
                    startActivity(Intent.createChooser(intent, "Share Image"))
                }
                3 -> {
                    viewModel.deleteDocument(document.id)
                }
            }

        }
    }

    private fun editImage(document: EditedPhotosModel) {
        val intent = Intent(this, EditingActivity::class.java)
        intent.putExtra("image_name", document.fileName)
        intent.putExtra("image_type", 2)
        intent.putExtra("image_filGrpAdjusters", document.filterValue.toFloatArray())
        intent.putExtra("image_id", document.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllDocuments()
    }

}