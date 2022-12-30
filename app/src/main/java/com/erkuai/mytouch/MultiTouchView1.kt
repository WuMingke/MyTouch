package com.erkuai.mytouch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 接力型:在新手指按下的时候，接管事件
 * 抢夺手指
 */
class MultiTouchView1(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val bitmap = getAvatar(resources, 200.dp.toInt())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var offsetX = 0f
    private var offsetY = 0f
    private var downX = 0f
    private var downY = 0f
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f
    private var trackingPointerId = 0 // 正在跟踪的手指id

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(bitmap, offsetX, offsetY, paint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { // ACTION_DOWN,只有一个手指
                trackingPointerId = event.getPointerId(0)

                downX = event.x
                downY = event.y
                originalOffsetX = offsetX
                originalOffsetY = offsetY

            }
            MotionEvent.ACTION_POINTER_DOWN -> { // 接管
                val actionIndex = event.actionIndex
                trackingPointerId = event.getPointerId(actionIndex)

                downX = event.getX(actionIndex)
                downY = event.getY(actionIndex)
                originalOffsetX = offsetX
                originalOffsetY = offsetY

            }
            MotionEvent.ACTION_MOVE -> {
                val index = event.findPointerIndex(trackingPointerId)
                offsetX = event.getX(index) - downX + originalOffsetX
                offsetY = event.getY(index) - downY + originalOffsetY
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP -> { // 非最后一根手指抬起
                // 如果是正在跟踪的手指,不是就不管
                val actionIndex = event.actionIndex
                val pointerId = event.getPointerId(actionIndex)
                if (pointerId == trackingPointerId) { // 如果是，就找个手指接管
                    val newIndex = if (actionIndex == event.pointerCount - 1) { // 是不是最大的那个
                        event.pointerCount - 2
                    } else {
                        event.pointerCount - 1
                    }

                    trackingPointerId = event.getPointerId(newIndex)

                    downX = event.getX(newIndex)
                    downY = event.getY(newIndex)
                    originalOffsetX = offsetX
                    originalOffsetY = offsetY
                }


            }
            MotionEvent.ACTION_UP -> { // 最后一根手指抬起

            }

        }
        return true
    }
}