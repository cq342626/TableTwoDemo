package com.cq.tabletwodemo.table

/**
 * 作者: CQ
 * 日期: 2021-09-08
 * 说明: 二维列表滑动监听
 */
interface OnTableScrollListener {
    fun onTableScroll(dx : Int, dy : Int)

    /**
     * 初始化点击状态，点击前清除点击定位
     */
    fun onTableClearState()

    /**
     * item焦点获取丢失的监听
     */
    fun onItemHasFocus(row : Int, column : Int, isHas : Boolean)
}