package com.mhirrr.videophotoeditor.presentation.document

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mhirrr.videophotoeditor.data.local.models.EditedPhotosModel
import com.mhirrr.videophotoeditor.databinding.RvDocumentItemBinding
import java.io.File

class DocumentsAdapter :
    RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder>() {

    var documents: List<EditedPhotosModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    var context: Context? = null

    inner class DocumentViewHolder(var binding: RvDocumentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        context = parent.context

        return DocumentViewHolder(
            RvDocumentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val editedPhoto = documents[position]
        val storageLoc =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val editedPhotoFile = File(storageLoc, editedPhoto.fileName)

        with(holder.binding) {
            rvDocumentImagePreview.setImageURI(editedPhotoFile.toUri())
            rvDocumentImageName.text = editedPhoto.name

            val arr = arrayOf("rename", "edit", "share")

            rvDocumentMenu.setOnClickListener {
                androidx.appcompat.app.AlertDialog.Builder(context!!)
                    .setItems(arr) { _, which ->
                        onDocumentMenuClickListener?.let { it(editedPhoto, which) }
                    }.create().show()
            }
        }

        holder.itemView.setOnClickListener {
            onDocumentClickListener?.let { it(editedPhoto) }
        }
    }

    override fun getItemCount(): Int = documents.size


    private val diffUtil = object : DiffUtil.ItemCallback<EditedPhotosModel>() {
        override fun areItemsTheSame(
            oldItem: EditedPhotosModel,
            newItem: EditedPhotosModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: EditedPhotosModel,
            newItem: EditedPhotosModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffUtil)

    private var onDocumentClickListener: ((EditedPhotosModel) -> Unit)? = null
    private var onDocumentMenuClickListener: ((EditedPhotosModel, Int) -> Unit)? = null

    fun setOnDocumentClickListener(listener: ((EditedPhotosModel) -> Unit)) {
        onDocumentClickListener = listener
    }

    fun setOnDocumentMenuClickListener(listener: ((EditedPhotosModel, Int) -> Unit)) {
        onDocumentMenuClickListener = listener
    }

}