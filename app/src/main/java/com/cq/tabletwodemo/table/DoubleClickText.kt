package com.cq.tabletwodemo.table

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView

/**
 * 作者: CQ
 * 日期: 2021-08-12
 * 说明:
 */
@SuppressLint("AppCompatCustomView")
class DoubleClickText(context: Context?, attrs: AttributeSet?) : TextView(context, attrs) {

    // 点击时间
    private var clickTime = 0L

    companion object {
        // 双击间隔时间
        private const val DOUBLE_CLICK = 1000L
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - clickTime > DOUBLE_CLICK) {
                clickTime = System.currentTimeMillis()
            } else {
                clickTime = System.currentTimeMillis()
                // 引用lambda表达式
                listener()
            }
        }
        return super.onTouchEvent(event)
    }

    // 定义一个lambda表达式
   private var listener: () -> Unit = {}

    // 赋值回调
    fun setOnDoubleClickListener(e : () -> Unit){
        this.listener = e
    }
}