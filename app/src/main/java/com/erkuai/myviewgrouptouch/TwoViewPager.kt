package com.erkuai.myviewgrouptouch

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.OverScroller
import androidx.core.view.children
import kotlin.math.abs

class TwoViewPager(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private var downX = 0f
    private var downY = 0f
    private var downScrollX = 0f
    private var scrolling = false
    private val overScroller = OverScroller(context)
    private val viewConfiguration = ViewConfiguration.get(context)
    private val velocityTracker = VelocityTracker.obtain()
    private val minVelocity = viewConfiguration.scaledMinimumFlingVelocity
    private val maxVelocity = viewConfiguration.scaledMaximumFlingVelocity
    private val pagingSlop = viewConfiguration.scaledPagingTouchSlop

    /**
     * velocityTracker 使用
     * 1、初始化 获取VelocityTracker.obtain() / 清理velocityTracker.clear()
     * 2、数据更新 velocityTracker.addMovement(event)
     * 3、速度计算 手动通知计算 velocityTracker.computeCurrentVelocity()
     */

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec) // 给所有子View统一的测量标准
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childLeft = 0
        val childTop = 0
        var childRight = width
        val childBottom = height
        for (child in children) {
            child.layout(childLeft, childTop, childRight, childBottom)
            childLeft += width
            childRight += width
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear()
        }
        velocityTracker.addMovement(event)
        var result = false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrolling = false
                downX = event.x
                downY = event.y
                downScrollX = scrollX.toFloat()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!scrolling) {
                    val dx = downX - event.x
                    if (abs(dx) > pagingSlop) {
                        scrolling = true
                        parent.requestDisallowInterceptTouchEvent(true) // 往上通知父View，让父View不要拦截我
                        result = true // 往下通知子View
                    }
                }
            }
        }

        return result
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear()
        }
        velocityTracker.addMovement(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                downScrollX = scrollX.toFloat()
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (downX - event.x + downScrollX).toInt()
                    .coerceAtLeast(0)
                    .coerceAtMost(width)
                scrollTo(dx, 0)
            }
            MotionEvent.ACTION_UP -> {
                // 速度跟踪器
                velocityTracker.computeCurrentVelocity(1000, maxVelocity.toFloat()) // 计算1000ms内移动的像素数
                val vx = velocityTracker.xVelocity // 这个就是获取计算后的值
                val scrollX = scrollX
                val targetPage = if (abs(vx) < minVelocity) {
                    if (scrollX > width / 2) 1 else 0
                } else {
                    if (vx < 0) 1 else 0
                }
                val scrollDistance = if (targetPage == 1) width - scrollX else -scrollX
                overScroller.startScroll(getScrollX(), 0, scrollDistance, 0)
                postInvalidateOnAnimation()
            }

        }
        return true
    }

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            scrollTo(overScroller.currX, overScroller.currY)
            postInvalidateOnAnimation()
        }
    }
}