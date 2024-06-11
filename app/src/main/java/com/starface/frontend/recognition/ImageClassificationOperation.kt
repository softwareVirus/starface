package com.starface.frontend.recognition

import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import com.starface.frontend.utils.Constants
const val TOP_K = 3

class ImageClassificationOperation : ImageAnalysis {
    private var analyzeImageErrorState: Boolean = false
    private var module: Module? = null
    private var inputTensor: Tensor? = null


    override fun analyzeImage(
        bitmap: Bitmap,
        moduleFileAbsoluteFilePath: String
    ): String {
        // Force Main thread
        Log.d("title33", "asd")
        val analysisResult = analyseImageImpl(bitmap, moduleFileAbsoluteFilePath)
        if(analysisResult?.topNScores?.get(0)!! < 4f)
            return "Unknown"
        if(analysisResult == null)
            return ""
        return analysisResult.topNClassNames[0]
    }

    fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun analyseImageImpl(
        bitmap: Bitmap,
        moduleFileAbsoluteFilePath: String
    ): AnalysisResult? {

        return try {
            module = Module.load(moduleFileAbsoluteFilePath)

            val startTime = SystemClock.elapsedRealtime()

            inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resizeBitmap(bitmap, 128, 128),
                floatArrayOf(0.485f, 0.456f, 0.406f),  // mean
                floatArrayOf(0.229f, 0.224f, 0.225f)   // std
            )

            val moduleForwardStartTime = SystemClock.elapsedRealtime()
            Log.d("title33", "asd")
            val outputTensor = module?.forward(IValue.from(inputTensor))?.toTensor()
            Log.d("title33", "asd22")
            val moduleForwardDuration = SystemClock.elapsedRealtime() - moduleForwardStartTime

            val scores = outputTensor?.dataAsFloatArray
            Log.d("title33", scores.toString())
            /*return scores?.let {
                val ixs = Utils.topK(scores, TOP_K)
                val topKClassNames = Array(TOP_K) { i -> (i * i).toString() }
                val topKScores = FloatArray(TOP_K)
                for (i in 0 until TOP_K) {
                    val ix = ixs[i]
                    if (ix <= Constants.IMAGE_NET_CLASSNAME.size) {
                        topKClassNames[i] = Constants.IMAGE_NET_CLASSNAME[ix]
                    }
                    topKScores[i] = scores[ix]
                }
                val analysisDuration = SystemClock.elapsedRealtime() - startTime
                return AnalysisResult(
                    topKClassNames,
                    topKScores,
                    moduleForwardDuration,
                    analysisDuration
                )
            }*/
            var maxScore = -Float.MAX_VALUE
            var maxScoreIdx = -1
            scores?.let {
                for (i in 0 until scores.size) {
                    if (scores[i] > maxScore) {
                        maxScore = scores[i]
                        maxScoreIdx = i
                    }
                    Log.d("value", scores.get(i).toString()+ " "+i.toString())

                }
            }

            val className: String = Constants.IMAGE_NET_CLASSNAME.get(maxScoreIdx)
            return AnalysisResult(
                arrayOf(className),
                floatArrayOf(maxScore),
                moduleForwardDuration,
                0
            )

            null
        } catch (e: Exception) {
            Log.e("TAG", "Error during image analysis", e)
            analyzeImageErrorState = true
            return AnalysisResult(
                arrayOf("Error during image analysis " + e.message),
                floatArrayOf(0F),
                0,
                0
            )
            null
        }
    }
}

interface ImageAnalysis {
    fun analyzeImage(
        bitmap: Bitmap,
        moduleFileAbsoluteFilePath: String
    ): String
}
