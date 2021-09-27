package com.cq.tabletwodemo.recycleview

import android.view.View

/**
 * 作者: CQ
 * 日期: 2021-03-22
 * 说明: 自定义监听
 */
interface OnItemClickListener {
    fun onItemClickListener(v: View?, position: Int)
}

interface OnItemLongClickListener {
    fun onItemLongClickListener(v: View?, position: Int)
}