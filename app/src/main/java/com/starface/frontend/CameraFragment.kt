package com.starface.frontend

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.media.MediaScannerConnection
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.starface.frontend.databinding.FragmentCameraBinding
import com.starface.frontend.recognition.ImageClassificationOperation
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(), FaceDetectorHelper.DetectorListener {

    private val TAG = "FaceDetection"

    private var _fragmentCameraBinding: FragmentCameraBinding? = null

    private val fragmentCameraBinding
        get() = _fragmentCameraBinding!!

    private lateinit var faceDetectorHelper: FaceDetectorHelper
    private val viewModel: MainViewModel by activityViewModels()
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyz = ImageClassificationOperation()
    // Necessary for taking photo and saving it in the files.
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService



    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        // Shut down our background executor.
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE,
            TimeUnit.NANOSECONDS
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding =
            FragmentCameraBinding.inflate(inflater, container, false)
        outputDirectory = getOutputDirectory()
        return fragmentCameraBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()

        // Create the FaceDetectionHelper that will handle the inference
        backgroundExecutor.execute {
            faceDetectorHelper =
                FaceDetectorHelper(
                    context = requireContext(),
                    threshold = viewModel.currentThreshold,
                    currentDelegate = viewModel.currentDelegate,
                    faceDetectorListener = this,
                    runningMode = RunningMode.LIVE_STREAM
                )

            // Wait for the views to be properly laid out
            fragmentCameraBinding.viewFinder.post {
                // Set up the camera and its use cases
                setUpCamera()
            }
        }

        // Attach listeners to UI control widgets
        initBottomSheetControls()

        // Attach listener to capture button
        fragmentCameraBinding.imageCaptureButton.setOnClickListener { takePhoto() }
    }

    private fun initBottomSheetControls() {
        // Init bottom sheet settings
        /*fragmentCameraBinding.bottomSheetLayout.thresholdValue.text =
            String.format("%.2f", viewModel.currentThreshold)

        // When clicked, lower detection score threshold floor
        fragmentCameraBinding.bottomSheetLayout.thresholdMinus.setOnClickListener {
            if (faceDetectorHelper.threshold >= 0.1) {
                faceDetectorHelper.threshold -= 0.1f
                updateControlsUi()
            }
        }

        // When clicked, raise detection score threshold floor
        fragmentCameraBinding.bottomSheetLayout.thresholdPlus.setOnClickListener {
            if (faceDetectorHelper.threshold <= 0.8) {
                faceDetectorHelper.threshold += 0.1f
                updateControlsUi()
            }
        }

        // When clicked, change the underlying hardware used for inference. Current options are CPU
        // GPU, and NNAPI
        fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.setSelection(
            viewModel.currentDelegate,
            false
        )
        fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    faceDetectorHelper.currentDelegate = p2
                    updateControlsUi()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    /* no op */
                }
            }*/
    }

    // Update the values displayed in the bottom sheet. Reset detector.
    private fun updateControlsUi() {
        /*fragmentCameraBinding.bottomSheetLayout.thresholdValue.text =
            String.format("%.2f", faceDetectorHelper.threshold)

        backgroundExecutor.execute {
            faceDetectorHelper.clearFaceDetector()
            faceDetectorHelper.setupFaceDetector()
        }

        fragmentCameraBinding.overlay.clear()*/
    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        if (_fragmentCameraBinding == null) return
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        if (_fragmentCameraBinding == null) return
        // CameraProvider
        val cameraProvider =
            cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance


        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer,
                imageCapture
            )
            setUpZoom()
            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun takePhoto() {
        if (_fragmentCameraBinding == null || imageCapture == null) return

        // Fotoğraf dosyasının kaydedileceği konumu belirle
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Fotoğraf kaydedilirken hata oluştu: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Fotoğraf kaydedildi: ${photoFile.absolutePath}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    val bundle = Bundle()
                    bundle.putString("photo_path", photoFile.absolutePath)
                    bundle.putFloat("scale_factor", scaleFactor)
                    findNavController().navigate(R.id.action_cameraFragment_to_imagePreviewFragment, bundle)
                    // Galeriyi taratarak fotoğrafın görünmesini sağla
                    MediaScannerConnection.scanFile(
                        requireContext(),
                        arrayOf(photoFile.absolutePath),
                        null,
                        null
                    )
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }

    private var scaleFactor = 1.0f // Scale factor değişkeni tanımlandı

    // Diğer kodlar...

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpZoom() {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)

                scaleFactor = currentZoomRatio * delta // Ölçek faktörünü güncelle

                // Ölçek faktörünü bir log kaydına yaz
                Log.d(TAG, "Scale factor: $scaleFactor")

                // Ölçek faktörünü bir UI bileşeninde göster
                // Örneğin: fragmentCameraBinding.scaleFactorTextView.text = "Scale: ${"%.2f".format(scaleFactor)}"

                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(requireContext(), listener)

        fragmentCameraBinding.viewFinder.setOnTouchListener { view, event ->
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) {
                val factory = fragmentCameraBinding.viewFinder.meteringPointFactory
                val point = factory.createPoint(event.x, event.y)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .setAutoCancelDuration(2, TimeUnit.SECONDS)
                    .build()

                camera?.cameraControl?.startFocusAndMetering(action)
                view.performClick()
            }
            true
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            fragmentCameraBinding.viewFinder.display.rotation
    }

    // Update UI after faces have been detected. Extracts original image height/width
    // to scale and place bounding boxes properly through OverlayView
    override fun onResults(resultBundle: FaceDetectorHelper.ResultBundle) {
        /*activity?.runOnUiThread {
            if (_fragmentCameraBinding != null) {
                fragmentCameraBinding.bottomSheetLayout.inferenceTimeVal.text =
                    String.format("%d ms", resultBundle.inferenceTime)

                // Pass necessary information to OverlayView for drawing on the canvas
                val detectionResult = resultBundle.results[0]
                val faceBitmaps = detectionResult.detections().mapNotNull { detection ->
                    getFaceBitmap(detection, resultBundle.inputImageHeight, resultBundle.inputImageWidth)
                }

                if (isAdded) {
                    fragmentCameraBinding.overlay.setResults(
                        detectionResult,
                        resultBundle.inputImageHeight,
                        resultBundle.inputImageWidth,
                        imageAnalyz,
                        faceBitmaps
                    )
                }

                // Force a redraw
                fragmentCameraBinding.overlay.invalidate()
            }
        }*/
    }
    /*private fun getFaceBitmap(detection: Detection, imageHeight: Int, imageWidth: Int): Bitmap? {
        // ViewFinder'dan alınan bit eşlemi
        val viewFinderBitmap = fragmentCameraBinding.viewFinder.bitmap ?: return null

        val boundingBox = detection.boundingBox()
        val scaleFactor = min(fragmentCameraBinding.viewFinder.width * 1f / imageWidth, fragmentCameraBinding.viewFinder.height * 1f / imageHeight)
        val left = (boundingBox.left * scaleFactor).toInt()
        val top = (boundingBox.top * scaleFactor).toInt()
        val right = (boundingBox.right * scaleFactor).toInt()
        val bottom = (boundingBox.bottom * scaleFactor).toInt()

        return Bitmap.createBitmap(viewFinderBitmap, left, top, right - left, bottom - top)
    }*/
    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            /*if (errorCode == FaceDetectorHelper.GPU_ERROR) {
                fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.setSelection(
                    FaceDetectorHelper.DELEGATE_CPU, false
                )
            }*/
        }
    }

    companion object {
        private const val TAG = "CameraFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
