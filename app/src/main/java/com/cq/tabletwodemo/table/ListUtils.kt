package com.cq.tabletwodemo.table

import android.os.Build
import android.view.View
import android.widget.EditText
import com.cq.tabletwodemo.recycleview.BaseViewHolder

/**
 * 作者: CQ
 * 日期: 2021-08-11
 * 说明:
 */
fun <T : CloneBean> List<T>.copyList(): MutableList<T> {
    val list = mutableListOf<T>()
    this.forEach {
        list.add(it.onCopy() as T)
    }
    return list
}

fun  List<String>.copyList1(): MutableList<String> {
    val list = mutableListOf<String>()
    this.forEach {
        list.add(it)
    }
    return list
}



fun BaseViewHolder.getDoubleClickText(viewId: Int): DoubleClickText {
    return getView(viewId) as DoubleClickText
}


// 判断点是否在view中
fun View.isTouchPointInView(x: Int, y: Int) : Boolean {
    val location = IntArray(2)
    getLocationOnScreen(location)
    val left = location[0]
    val top = location[1]
    val right = left + measuredWidth
    val bottom = top + measuredHeight
    return x in left..right && y in top..bottom
}

/**
 * 是否禁止弹出软键盘
 */
fun EditText.isShowSoftInputOnFocus(isShow : Boolean){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        showSoftInputOnFocus = isShow
    } else {
        val clazz = this.javaClass
        try {
            val method = clazz.getMethod("setShowSoftInputOnFocus", Boolean::class.java)
            method.isAccessible = true
            method.invoke(this, isShow)
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
}