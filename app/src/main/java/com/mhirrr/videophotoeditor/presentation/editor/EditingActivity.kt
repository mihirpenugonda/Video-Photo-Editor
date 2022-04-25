package com.mhirrr.videophotoeditor.presentation.editor

import GPUImageFilterTools
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

    private var fileName: String? = null

    private val slider by lazy {
        binding.editFilterSlider
    }

    private val image by lazy {
        binding.editImage
    }

    private val adapter by lazy {
        EditingFilterAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set image to surface
        lifecycleScope.launchWhenStarted {
            val imageUriString = intent.getStringExtra("image_uri")
            val imageName = intent.getStringExtra("image_name")
            val imageFilGrpAdjustersValue = intent.getFloatArrayExtra("image_filGrpAdjusters")

            when (intent.getIntExtra("image_type", 0)) {
                0 -> {
                    setUpData(true, null)
                    fileName = imageName!!
                    val file = File(filesDir, imageName)
                    image.setImage(file)
                }
                1 -> {
                    setUpData(true, null)
                    image.setImage(imageUriString!!.toUri())
                }
                2 -> {

                    setUpData(false, imageFilGrpAdjustersValue!!.toMutableList())
                    fileName = imageName!!
                    val file = File(filesDir, imageName)
                    image.setImage(file)
                }
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

        // setup recycler view
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

        // slider on change listener
        slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            filAdjuster?.adjust((value * 100).toInt())
            image.requestRender()
        })

        // save button setup
        if (intent.getIntExtra("image_type", -1) == 2) {
            binding.editFilterSave.visibility = View.INVISIBLE
            binding.editFilterUpdate.visibility = View.VISIBLE
        } else {
            binding.editFilterSave.setOnClickListener {
                filGrpAdjustersValue!![filPosition!!] = slider.value * 100
                viewModel.saveImage(
                    image.gpuImage.bitmapWithFilterApplied,
                    filGrpAdjustersValue,
                    fileName!!
                )
            }
        }

        binding.editFilterUpdate.setOnClickListener {
            val fileId = intent.getIntExtra("image_id", -1)
            filGrpAdjustersValue!![filPosition!!] = slider.value * 100
            viewModel.updateImage(image.gpuImage.bitmapWithFilterApplied, filGrpAdjustersValue, fileName!!, fileId)
        }
    }

    private fun scanFile(context: Context, imageUri: Uri) {
        val file = imageUri.toFile()
        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()),
            null, null
        )
    }

    private fun setUpData(isNew: Boolean, adjusterValues: MutableList<Float>?) {
        val filterGroup = GPUImageFilterGroup()
        val filterGroupAdjusters: MutableList<GPUImageFilterTools.FilterAdjuster> = mutableListOf()
        val filterGroupAdjustersValue: MutableList<Float> = mutableListOf()

        Constants.Filters.forEachIndexed { index, filterType ->
            val currentFilter =
                GPUImageFilterTools.createFilterForType(this, filterType.first)
            val currentFilterAdjustor = GPUImageFilterTools.FilterAdjuster(currentFilter)
            val currentFilterDefaultVal = if (isNew) filterType.second else adjusterValues!![index]

            currentFilterAdjustor.adjust(currentFilterDefaultVal.toInt())

            filterGroup.addFilter(currentFilter)
            filterGroupAdjusters.add(currentFilterAdjustor)
            filterGroupAdjustersValue.add(currentFilterDefaultVal)

        }

        filPosition = -1
        filGrp = filterGroup
        filGrpAdjusters = filterGroupAdjusters
        filGrpAdjustersValue = filterGroupAdjustersValue
    }
}