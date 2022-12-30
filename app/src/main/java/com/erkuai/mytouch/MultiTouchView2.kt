package com.erkuai.mytouch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 配合型
 * 找到所有手指的中心点
 */
class MultiTouchView2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val bitmap = getAvatar(resources, 200.dp.toInt())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var offsetX = 0f
    private var offsetY = 0f
    private var downX = 0f
    private var downY = 0f
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f
//    private var trackingPointerId = 0 // 正在跟踪的手指id

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(bitmap, offsetX, offsetY, paint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var pointCount = event.pointerCount
        var sumX = 0f
        var sumY = 0f
        val isPointUp = event.actionMasked == MotionEvent.ACTION_POINTER_UP // 防止额外偏移，因为抬起的时候，pointCount还没有改变
        for (i in 0 until pointCount) {
            if (!(isPointUp && i == event.actionIndex)) {
                sumX += event.getX(i)
                sumY += event.getY(i)
            }
        }
        if (isPointUp) {
            pointCount--
        }
        val focusX: Float = sumX / pointCount
        val focusY: Float = sumY / pointCount

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> { // ACTION_DOWN,只有一个手指
//                trackingPointerId = event.getPointerId(0)

                downX = focusX
                downY = focusY
                originalOffsetX = offsetX
                originalOffsetY = offsetY

            }
//            MotionEvent.ACTION_POINTER_DOWN -> { // 接管
//                val actionIndex = event.actionIndex
//                trackingPointerId = event.getPointerId(actionIndex)
//
//                downX = event.getX(actionIndex)
//                downY = event.getY(actionIndex)
//                originalOffsetX = offsetX
//                originalOffsetY = offsetY
//
//            }
            MotionEvent.ACTION_MOVE -> {
//                val index = event.findPointerIndex(trackingPointerId)
                offsetX = focusX - downX + originalOffsetX
                offsetY = focusY - downY + originalOffsetY
                invalidate()
            }
//            MotionEvent.ACTION_POINTER_UP -> { // 非最后一根手指抬起
//                // 如果是正在跟踪的手指,不是就不管
//                val actionIndex = event.actionIndex
//                val pointerId = event.getPointerId(actionIndex)
//                if (pointerId == trackingPointerId) { // 如果是，就找个手指接管
//                    val newIndex = if (actionIndex == event.pointerCount - 1) { // 是不是最大的那个
//                        event.pointerCount - 2
//                    } else {
//                        event.pointerCount - 1
//                    }
//
//                    trackingPointerId = event.getPointerId(newIndex)
//
//                    downX = event.getX(newIndex)
//                    downY = event.getY(newIndex)
//                    originalOffsetX = offsetX
//                    originalOffsetY = offsetY
//                }
//
//
//            }
            MotionEvent.ACTION_UP -> { // 最后一根手指抬起

            }

        }
        return true
    }
}