package com.rj.poc.coraltask.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.rj.poc.coraltask.data.room.Box

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = android.graphics.Color.RED
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private val boxes = mutableListOf<Box>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        boxes.forEach { box ->
            canvas.drawRect(box.startX, box.startY, box.endX, box.endY, paint)
        }
        canvas.drawRect(startX, startY, endX, endY, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                endX = startX
                endY = startY
            }
            MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                endX = event.x
                endY = event.y
                boxes.add(Box(startX = startX, startY = startY, endX = endX, endY = endY))
                invalidate()
            }
        }
        return true
    }

    fun setBoxes(newBoxes: List<Box>) {
        boxes.clear()
        boxes.addAll(newBoxes)
        invalidate()
    }

    fun clearBoxes() {
        boxes.clear()
        invalidate()
    }

    fun getBoxes(): List<Box> {
        return boxes.toList()
    }
}
