package com.starface.frontend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.navigation.findNavController
import android.os.Bundle
val actorNumbers = arrayOf(
    ActorNumber("Ahmet Kural", 1),
    ActorNumber("Ahmet Mümtaz Taylan", 24),
    ActorNumber("Alper Kul", 25),
    ActorNumber("Ana de Armas", 16),
    ActorNumber("Andrew Garfield", 41),
    ActorNumber("Armie Hammer", 42),
    ActorNumber("Ayhan Taş", 2),
    ActorNumber("Barış Yıldız", 3),
    ActorNumber("Ben Whishaw", 18),
    ActorNumber("Cem Yılmaz", 46),
    ActorNumber("Daniel Craig", 19),
    ActorNumber("Demet Evgar", 47),
    ActorNumber("Devrim Yakut", 4),
    ActorNumber("Doğu Demirkol", 26),
    ActorNumber("Fatma Toptaş", 33),
    ActorNumber("Feyyaz Yiğit", 27),
    ActorNumber("Gwyneth Paltrow", 12),
    ActorNumber("Hakan Bilgin", 40),
    ActorNumber("Jeff Bridges", 13),
    ActorNumber("Jesse Eisenberg", 43),
    ActorNumber("Jordana Brewster", 9),
    ActorNumber("Justin Timberlake", 44),
    ActorNumber("Lashana Lynch", 20),
    ActorNumber("Lemi Filozof", 35),
    ActorNumber("Léa Seydoux", 21),
    ActorNumber("Max Minghella", 45),
    ActorNumber("Mehmet Özgür", 29),
    ActorNumber("Meltem Kaptan", 30),
    ActorNumber("Murat Cemcir", 6),
    ActorNumber("Ozan Güven", 48),
    ActorNumber("Paul Walker", 10),
    ActorNumber("Ralph Fiennes", 22),
    ActorNumber("Rami Malek", 23),
    ActorNumber("Rasim Öztekin", 7),
    ActorNumber("Robert Downey Jr", 14),
    ActorNumber("Sarp Apak", 32),
    ActorNumber("Shaun Toub", 15),
    ActorNumber("Somer Karvan", 37),
    ActorNumber("Terrence Howard", 17),
    ActorNumber("Tuluğ Çizen", 38),
    ActorNumber("Unknown", 0),
    ActorNumber("Vin Diesel", 11),
    ActorNumber("Vural Buldu", 39),
    ActorNumber("Zafer Algöz", 50),
    ActorNumber("Özgür Emre", 31),
    ActorNumber("Özkan Uğur", 49),
    ActorNumber("İnan Ulaş Torun", 5),
    ActorNumber("İrem Sak", 28),
    ActorNumber("Şahan Gökbakar", 36),
    ActorNumber("Şinasi Yurtsever", 8)
)

fun getActorNumber(name: String): Int {
    val index = actorNumbers.indexOfFirst { it.name == name }
    return if (index != -1) actorNumbers[index].id else 0
}
data class ActorNumber(val name: String, val id: Int)
class FaceOverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE // Change border color to black
        strokeWidth = 5f
        setShadowLayer(10f, 0f, 0f, Color.BLACK) // Add shadow to the circle
    }
    private val outlinePaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
        style = Paint.Style.STROKE
        strokeWidth = 1f // Adjust the width of the outline
    }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 30f
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL_AND_STROKE
        setShadowLayer(5f, 0f, 0f, Color.BLACK) // Add shadow to the text
    }

    private var faces = listOf<RectF>()
    private var labels = listOf<String>()
    private var bitmapWidth: Int = 1
    private var bitmapHeight: Int = 1
    private var scaleX: Float = 1f
    private var scaleY: Float = 1f
    private var transX: Float = 0f
    private var transY: Float = 0f

    fun setFaces(faces: List<RectF>, labels: List<String>, bitmapWidth: Int, bitmapHeight: Int, scaleX: Float, scaleY: Float, transX: Float, transY: Float) {
        this.faces = faces
        this.labels = labels
        this.bitmapWidth = bitmapWidth
        this.bitmapHeight = bitmapHeight
        this.scaleX = scaleX
        this.scaleY = scaleY
        this.transX = transX
        this.transY = transY
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        faces.forEachIndexed { index, rect ->
            val centerX = (rect.left * scaleX + transX + rect.right * scaleX + transX) / 2
            val centerY = (rect.top * scaleY + transY + rect.bottom * scaleY + transY) / 2
            val radius = Math.min(rect.width() * scaleX, rect.height() * scaleY) / 2 * 1.5f

            // Draw circle
            canvas.drawCircle(centerX, centerY, radius, paint)

            // Draw text below the circle
            val textX = centerX
            val textY = centerY + radius + textPaint.textSize
            canvas.drawText(labels[index], textX, textY, textPaint)
            canvas.drawText(labels[index], textX, textY, outlinePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            faces.forEachIndexed { index, rect ->
                val centerX = (rect.left * scaleX + transX + rect.right * scaleX + transX) / 2
                val centerY = (rect.top * scaleY + transY + rect.bottom * scaleY + transY) / 2
                val radius = Math.min(rect.width() * scaleX, rect.height() * scaleY) / 2 * 1.5f
                val scaledRect = RectF(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius
                )
                if (scaledRect.contains(x, y)) {
                    val bundle = Bundle().apply {
                        putInt("id", getActorNumber(labels[index]))
                    }
                    findNavController().navigate(R.id.action_imagePreviewFragment_to_actorFragment, bundle)
                }
            }
        }
        return true
    }
}
