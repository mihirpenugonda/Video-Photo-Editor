package com.mhirrr.videophotoeditor.presentation.editor

import GPUImageFilterTools
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.slider.Slider
import com.mhirrr.videophotoeditor.databinding.ActivityEditingBinding
import com.mhirrr.videophotoeditor.presentation.document.DocumentsActivity
import com.mhirrr.videophotoeditor.utils.Constants
import com.mhirrr.videophotoeditor.utils.dataUtils.Resource
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import java.io.File

@AndroidEntryPoint
class EditingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditingBinding

    private val viewModel: EditingViewModel by viewModels()

    private var filPosition: Int? = null
    private var filAdjuster: GPUImageFilterTools.FilterAdjuster? = null
    private var filGrp: GPUImageFilterGroup? = null
    private var filGrpAdjusters: MutableList<GPUImageFilterTools.FilterAdjuster>? = null
    private var filGrpAdjustersValue: MutableList<Float>? = null

    private val slider by lazy {
        binding.editFilterSlider
    }

    private val image by lazy {
        binding.editImage
    }

    private val adapter by lazy {
        EditingFilterAdapter()
    }

    // initializing filter and data for default values
    init {
        val filterGroup = GPUImageFilterGroup()
        val filterGroupAdjusters: MutableList<GPUImageFilterTools.FilterAdjuster> = mutableListOf()
        val filterGroupAdjustersValue: MutableList<Float> = mutableListOf()

        Constants.Filters.forEach { filterType ->
            val currentFilter =
                GPUImageFilterTools.createFilterForType(this, filterType.first)
            val currentFilterAdjustor = GPUImageFilterTools.FilterAdjuster(currentFilter)
            val currentFilterDefaultVal = filterType.second

            filterGroup.addFilter(currentFilter)
            filterGroupAdjusters.add(currentFilterAdjustor)
            filterGroupAdjustersValue.add(currentFilterDefaultVal)

            currentFilterAdjustor.adjust(currentFilterDefaultVal.toInt())
        }

        filPosition = -1
        filGrp = filterGroup
        filGrpAdjusters = filterGroupAdjusters
        filGrpAdjustersValue = filterGroupAdjustersValue
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set image to surface
        lifecycleScope.launchWhenStarted {
            val imageUriString = intent.getStringExtra("image_uri")
            val isGalleryImage = intent.getBooleanExtra("is_gallery_image", false)

            if (isGalleryImage) {
                image.setImage(imageUriString!!.toUri())
            } else {
                val file = File(filesDir, "temp.jpg")
                image.setImage(file)
            }

            image.filter = filGrp
            image.requestRender()
        }

        // listen for save image event
        lifecycleScope.launchWhenStarted {
            viewModel.saveImageState.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        scanFile(applicationContext, response.data!!)

                        val intent = Intent(applicationContext, DocumentsActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {

                    }
                }
            }
        }

        adapter.setOnFilterClickListener { name, index ->
            if (filPosition != -1) {
                val currentFilterValue = slider.value * 100
                filGrpAdjustersValue!![filPosition!!] = currentFilterValue
            }

            filAdjuster = filGrpAdjusters?.get(index)
            filAdjuster!!.adjust(filGrpAdjustersValue!![index].toInt())
            filPosition = index

            slider.value = filGrpAdjustersValue!![index] / 100

            binding.editFilterName.text = name
            image.requestRender()
        }

        binding.editFilterList.adapter = adapter
        binding.editFilterList.layoutManager =
            LinearLayoutManager(this@EditingActivity, LinearLayoutManager.HORIZONTAL, false)

        slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            filAdjuster?.adjust((value * 100).toInt())
            image.requestRender()
        })

        binding.editFilterSave.setOnClickListener {
            viewModel.saveImage(
                image.gpuImage.bitmapWithFilterApplied,
                filGrpAdjustersValue
            )
        }
    }

    private fun scanFile(context: Context, imageUri: Uri) {
        val file = imageUri.toFile()
        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()),
            null, null
        )
    }
}