package com.cq.tabletwodemo.table

import android.view.View
import android.view.ViewGroup

/**
 * 作者: CQ
 * 日期: 2021-08-31
 * 说明:
 */
interface TableAdapter {

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): View?

    fun onBindViewHolder(view: View, row : Int, column : Int, position: Int): View?

    fun getItemCount(): Int

    fun getItemCountRows(): Int

    fun getItemCountColumns(): Int

    fun getItemViewType(position: Int): Int

    fun getItemViewCount(): Int

}


class ViewHolder constructor(val view: View) {}


