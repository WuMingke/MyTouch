package com.erkuai.mytouch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * ACTION_CANCEL 一般是父View抢夺了事件，给子View发送该事件
 */

/**
 * MotionEvent 的 x y 是属于手指的 Pointer(x,y,index,id)，第一个根手指的index就是0，通过event.getX()方法可以看出来
 *              它获取的就是第一个手指的x，同一个Pointer的index是不断变化的(方便遍历)
 *              只要不抬起，id是不变的，可以在按下的时候保存，找到这个手指
 * MotionEvent 属于View，是针对View的，就是说这个View上有xx事件
 *
 *
 */
/**
 *
 * 多点触控，3种类型
 *      接力型
 *      配合型
 *      各自为战型
 *
 * 主要是记住几个api，已经3种类型的实现框架
 */
class MultiTouchView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val bitmap = getAvatar(resources, 200.dp.toInt())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var offsetX = 0f
    private var offsetY = 0f
    private var downX = 0f
    private var downY = 0f
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(bitmap, offsetX, offsetY, paint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                originalOffsetX = offsetX
                originalOffsetY = offsetY
            }
            MotionEvent.ACTION_MOVE -> {
//                for (i in 0 until event.pointerCount){
//                    event.getX(i)
//                    event.getY(i)
//                }
//                event.findPointerIndex(downId) // 这个手指在此次事件中的id
//                event.actionIndex // 正在按下或者抬起的的手指的index
                // 正在移动的手指，所有手指都可能晃动，所以 event.actionIndex 在移动时获取，只会是0
                //
                offsetX = event.x - downX + originalOffsetX
                offsetY = event.y - downY + originalOffsetY
                invalidate()
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }
}