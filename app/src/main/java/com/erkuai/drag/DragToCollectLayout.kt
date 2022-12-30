package com.erkuai.drag

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.erkuai.mytouch.R

/**
 * OnDragListener 实用举例
 *
 * 是关注数据的
 */
class DragToCollectLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var dragStarter = OnLongClickListener { v ->
        val imageData = ClipData.newPlainText("name", v.contentDescription)
        // DragShadowBuilder(v) 就是拖起来的那个View的样式
        ViewCompat.startDragAndDrop(v, imageData, DragShadowBuilder(v), null, 0)
    }
    private var dragListener: OnDragListener = CollectListener()

    override fun onFinishInflate() {
        super.onFinishInflate()
        // 测试时这里放开
//        findViewById<ImageView>(R.id.avatarView).setOnLongClickListener(dragStarter)
//        findViewById<ImageView>(R.id.logoView).setOnLongClickListener(dragStarter)
//        findViewById<LinearLayout>(R.id.collectorLayout).setOnDragListener(dragListener)
    }

    inner class CollectListener : OnDragListener { // 拖的是数据
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DROP -> if (v is LinearLayout) {
                    val textView = TextView(context)
                    textView.textSize = 16f
                    // event.clipData 就是上面的 imageData；clipData 可以跨进程，myLocalState 不可以跨进程
                    textView.text = event.clipData.getItemAt(0).text
                    v.addView(textView)
                }
            }
            return true
        }
    }
}