package com.cq.tabletwodemo.table

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.cq.tabletwodemo.R

/**
 * 作者: CQ
 * 日期: 2021-09-03
 * 说明:
 */
class TableLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    // item宽度
    private var itemWidth = 100

    // item高度
    private var itemHeight = 60
    private var mDivider: Drawable? = null
    init {
        mDivider = ContextCompat.getDrawable(context, R.drawable.shap_button_select_white)
        itemWidth = DensityUtil.dip2px(context, 50f) + mDivider!!.intrinsicWidth
        itemHeight = DensityUtil.dip2px(context, 30f) + mDivider!!.intrinsicWidth
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("TableLinearLayout", "onMeasure = realWidth = $itemWidth , realHeight = $itemHeight")
        setMeasuredDimension(itemWidth, itemHeight)
    }
}