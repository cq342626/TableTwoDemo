package com.cq.tabletwodemo.table

import android.content.Context

/**
 * 作者: CQ
 * 日期: 2021-08-10
 * 说明:
 */
object DensityUtil {
    fun dip2px(context: Context, dpValue : Float) : Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5).toInt()
    }

    fun px2dip(context: Context, pxValue : Float) : Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5).toInt()
    }

}