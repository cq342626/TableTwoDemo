package com.cq.tabletwodemo.table

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.cq.tabletwodemo.R

/**
 * 作者: CQ
 * 日期: 2021-08-31
 * 说明:
 */
class TableRVAdapter constructor(val context : Context, var dataList : Array<Array<TableDataBean?>>?) : TableAdapter {
    fun onAutoData(data : Array<Array<TableDataBean?>>) {
        dataList = data
    }

    // 定义好的二维数据
    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : View {
        return LayoutInflater.from(context).inflate(R.layout.item_gird_edit, parent, false)
    }

    @SuppressLint("CutPasteId")
    override fun onBindViewHolder(view : View, row : Int, column : Int, position : Int) : View {
        Log.e("adapter", "onBindViewHolder $position")
        view.findViewById<EditText>(R.id.item_grid_edit).setText("${dataList!![row][column]!!.value}")
        if (dataList!![row][column]!!.type == 2) {
            val editText = view.findViewById<TableInputEditText>(R.id.item_grid_edit)
            editText.setBackgroundResource(R.color.white)
            editText.setTag(R.id.table_rv_row, row)
            editText.setTag(R.id.table_rv_column, column)
            editText.setOnFocusChangeListener { v, hasFocus ->
                val inputEditText = v as TableInputEditText
                val rowValue = v.getTag(R.id.table_rv_row) as Int
                val columnValue = v.getTag(R.id.table_rv_column) as Int
                if (hasFocus) {
                    editText.setBackgroundResource(R.color.purple_500)
                    editText.selectAll()
                    inputEditText.addChangedInputTextListener {
                        val rowValue2 = v.getTag(R.id.table_rv_row) as Int
                        val columnValue2 = v.getTag(R.id.table_rv_column) as Int
                        onTextChanged(rowValue2, columnValue2, it.toInt())
                    }
                    inputEditText.isChangedSelectAll = true
                } else {
                    inputEditText.setBackgroundResource(R.color.white)
                    inputEditText.removeChangedInputTextListener()
                }
                itemFocusListener(rowValue, columnValue, hasFocus)

            }
        } else {
            view.findViewById<EditText>(R.id.item_grid_edit).setBackgroundResource(R.color.holo_green_light)
        }
        view.findViewById<EditText>(R.id.item_grid_edit).isEnabled = dataList!![row][column]!!.isEdit
        return view
    }

    /**
     * item总个数
     */
    override fun getItemCount() : Int {
        if (dataList == null)
            return 0
        return dataList!!.size * dataList!![0].size
    }

    /**
     * item总行数
     */
    override fun getItemCountRows() : Int {
        if (dataList == null)
            return 0
        return dataList!!.size
    }

    /**
     * item总列数
     */
    override fun getItemCountColumns() : Int {
        if (dataList == null)
            return 0
        return dataList!![0].size
    }

    override fun getItemViewType(position : Int) : Int {
        return 0
    }

    override fun getItemViewCount() : Int {
        return 1
    }

    private var itemFocusListener: (Int,Int, Boolean) -> Unit = { row, column, isHas -> }
    /**
     * item焦点获取丢失的监听
     */
    fun setItemFocusListener(itemFocusListener: (Int,Int, Boolean) -> Unit = { row, column, isHas -> }) {
        this.itemFocusListener = itemFocusListener
    }

    // 监听item改变的值
    private var onTextChanged : (row : Int, column : Int, value : Int) -> Unit = { row, column, value -> }

    fun addOnTextChanged(onTextChanged : (row : Int, column : Int, value : Int) -> Unit = { row, column, value -> }) {
        this.onTextChanged = onTextChanged
    }
}