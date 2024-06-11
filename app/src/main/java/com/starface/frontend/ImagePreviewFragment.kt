package com.starface.frontend

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.starface.frontend.recognition.ImageClassificationOperation
import com.starface.frontend.utils.Utils.assetFilePath
import java.io.File


private const val ARG_PARAM1 = "photo_path"
private const val ARG_PARAM2 = "scale_factor"

class ImagePreviewFragment : Fragment() {
    private var photoPath: String? = null
    private var scaleFactor: Float? = null

    private lateinit var faceDetectorHelper: FaceDetectorHelper
    private lateinit var imageClassificationOperation: ImageClassificationOperation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoPath = it.getString(ARG_PARAM1)
            scaleFactor = it.getFloat(ARG_PARAM2)
        }
        faceDetectorHelper = FaceDetectorHelper(context = requireContext())
        imageClassificationOperation = ImageClassificationOperation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_preview, container, false)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val faceOverlayView: FaceOverlayView = view.findViewById(R.id.faceOverlayView)

        photoPath?.let {
            val transition = null
            Glide.with(this)
                .asBitmap()
                .load(it)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImageBitmap(resource)
                        var listFaces: List<RectF> = listOf()
                        val resultBundle = faceDetectorHelper.detectImage(resource)
                        var index = 0
                        var listLabels: List<String> = listOf()
                        resultBundle?.results?.get(0).let { result ->
                            if (result != null) {
                                for (detection in result.detections()) {
                                    val boundingBox = detection.boundingBox()
                                    val rect = RectF(
                                        boundingBox.left,
                                        boundingBox.top,
                                        boundingBox.right,
                                        boundingBox.bottom
                                    )
                                    listFaces += rect

                                    if (resultBundle?.bitmaps?.size != null && resultBundle.bitmaps.size > index) {
                                        listLabels += ImageClassificationOperation()?.analyzeImage(
                                            resultBundle?.bitmaps?.get(index), File(
                                                assetFilePath(context!!, "mobilenetv3_quantized.pt")
                                            ).absolutePath
                                        )
                                    }
                                    index++
                                }
                            }
                        }
                        imageView.post {
                            val imageMatrixValues = FloatArray(9)
                            imageView.imageMatrix.getValues(imageMatrixValues)
                            val scaleX = imageMatrixValues[Matrix.MSCALE_X]
                            val scaleY = imageMatrixValues[Matrix.MSCALE_Y]
                            val transX = imageMatrixValues[Matrix.MTRANS_X]
                            val transY = imageMatrixValues[Matrix.MTRANS_Y]
                            faceOverlayView.setFaces(listFaces, listLabels, resource.width, resource.height, scaleX, scaleY, transX, transY)
                        }

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        }

        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(photoPath: String, param2: String) =
            ImagePreviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, photoPath)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
