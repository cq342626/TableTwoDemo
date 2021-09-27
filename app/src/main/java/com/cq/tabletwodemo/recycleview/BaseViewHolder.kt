package com.cq.tabletwodemo.recycleview

import android.util.SparseArray
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者: CQ
 * 日期: 2021-03-16
 * 说明:recycleView适配器的ViewHolder
 */
open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var mViews: SparseArray<View> = SparseArray<View>()

    /**
     * 获取view 可以装换成想要的控件，然后处理
     */
    fun getView(viewId: Int): View? {
        var view: View? = mViews.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view
    }

    /**
     * 获取TextView
     */
    fun getTextView(viewId: Int): TextView {
        return getView(viewId) as TextView
    }

    /**
     * 获取EditText
     */
    fun getEditText(viewId: Int): EditText {
        return getView(viewId) as EditText
    }

    /**
     * 获取imageView
     */
    fun getImageView(viewId: Int): ImageView {
        return getView(viewId) as ImageView
    }

    fun getRecyclerView(viewId: Int): RecyclerView {
        return getView(viewId) as RecyclerView
    }

    /**
     * 获取itemView
     */
    fun getRootView(): View {
        return itemView
    }

}