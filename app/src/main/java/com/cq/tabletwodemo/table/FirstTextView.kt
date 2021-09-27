package com.cq.tabletwodemo.table

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.cq.tabletwodemo.R

/**
 * 作者: CQ
 * 日期: 2021-08-10
 * 说明:
 */
class FirstTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.shap_button_select_white)!!

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec) + mDivider.intrinsicWidth * 2
        val height = MeasureSpec.getSize(heightMeasureSpec) + mDivider.intrinsicHeight * 2
        setMeasuredDimension(width, height)
    }

    @SuppressLint("DrawAllocation", "ResourceAsColor")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val mDividerH = mDivider.intrinsicHeight
        val mDividerW = mDivider.intrinsicWidth
        val paint = Paint()
        paint.let {
            it.strokeWidth = mDividerW.toFloat()
            it.color = Color.BLACK
            it.style = Paint.Style.STROKE
        }
        val rectF = RectF(mDividerW.toFloat() / 2, mDividerH.toFloat() / 2,width.toFloat() - mDividerW.toFloat() / 2,height.toFloat() - mDividerH.toFloat() / 2)
        canvas!!.drawRect(rectF, paint)
        paint.reset()
        paint.let {
            it.strokeWidth = mDividerW.toFloat()
            it.setARGB(255, 153,204,0)
            it.style = Paint.Style.FILL
        }
        val rectF1 = RectF(mDividerW.toFloat(), mDividerH.toFloat(),width.toFloat() - mDividerW.toFloat(),height.toFloat() - mDividerH.toFloat())
        canvas.drawRect(rectF1, paint)

    }

}