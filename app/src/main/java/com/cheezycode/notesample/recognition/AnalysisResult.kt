package com.cheezycode.notesample.recognition

class AnalysisResult(
    val topNClassNames: Array<String>,
    val topNScores: FloatArray,
    val moduleForwardDuration: Long,
    val analysisDuration: Long
)
