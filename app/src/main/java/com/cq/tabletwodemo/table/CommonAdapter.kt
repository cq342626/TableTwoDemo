package com.cq.tabletwodemo.table

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.cq.tabletwodemo.R
import com.cq.tabletwodemo.recycleview.BaseRVMultiTypeDiffAdapter

/**
 * 作者: CQ
 * 日期: 2021-08-11
 * 说明: 中间列表适配器，不同布局
 */
class CommonAdapter : BaseRVMultiTypeDiffAdapter<TableDataBean>() {

    // 下标；光标位置
    private val locationMap = mutableMapOf<Int, Int>()

    fun clearLocationMap(){
        locationMap.clear()
    }

    companion object {
        private const val ITEM_TYPE_TOP = 0
        private const val ITEM_TYPE_LEFT = 1
        private const val ITEM_TYPE_CONTENT = 2
        private const val ITEM_TYPE_SUM = 3
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateMultiViewHolder(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            ITEM_TYPE_TOP -> loadLayout(R.layout.item_gird_text, parent)
            ITEM_TYPE_LEFT -> loadLayout(R.layout.item_gird_text, parent)
            ITEM_TYPE_CONTENT -> loadLayout(R.layout.item_gird_edit, parent)
            ITEM_TYPE_SUM -> loadLayout(R.layout.item_gird_text, parent)
            else -> loadLayout(R.layout.item_gird_text, parent)
        }
    }

    override fun onBindViewHolder(holder: MultiTypeViewHolder, item: TableDataBean, position: Int) {
        when (item.type) {
            ITEM_TYPE_TOP -> {
                holder.getTextView(R.id.item_grid_text).text = item.gg1dm
            }
            ITEM_TYPE_LEFT -> {
                holder.getTextView(R.id.item_grid_text).text = item.gg2dm
            }
            ITEM_TYPE_CONTENT -> {
                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        locationMap.clear()
                        var valueInput = p0.toString() // 获取监听的值
                        val pos = holder.getEditText(R.id.item_grid_edit).getTag(R.id.RecyclerView_pos) as Int // 获取当前变化的位置
                        val editText = holder.getEditText(R.id.item_grid_edit).getTag(R.id.RecyclerView_item) as EditText
                        val textWatcherT = holder.getEditText(R.id.item_grid_edit).getTag(R.id.RecyclerView_textWatcher) as TextWatcher // 因为修改一次数量，变化的是4个位置（当前item、gg1合计、gg2合计、总合计），如果有一个地方的数量超过了限制值 2147483648，所有修改的值都需要回滚
                        val refreshMap = mutableMapOf<Int, Int>() // 记录需要刷新的位置及值
                        val oldMap = mutableMapOf<Int, Int>() // 记录需要刷新前的位置及值
                        val twoDimenDataOld = getData()[pos].onCopy() as TableDataBean // 获取变化前item的值
                        if (valueInput == "") { // 如果输入值变成空了
                            valueInput = "0"  // 输入为空默认为0
                        }
                        oldMap[pos] = twoDimenDataOld.value
                        if (valueInput.toIntOrNull() == null) { // 当前输入异常，不能转成int
                            refreshMap[pos] = twoDimenDataOld.value // 当前位置，需要刷新，值改成修改前的数据
                            editText.removeTextChangedListener(textWatcherT)
                            locationMap[pos] = editText.selectionStart - 1
                            Handler(Looper.getMainLooper()).post {
                                notifyItemChanged(pos)
                            }
                            return
                        }

                        var isCountError = false // 是否是计算异常 // 当前输入正常，记录
                        refreshMap[pos] = valueInput.toInt()  // 当前位置，需要刷新，值为输入的值
                        val value = refreshMap[pos]!! - twoDimenDataOld.value // 修改数量后相比之前增加或者减少的值，可直接用于合计的计算
                        for (i in getData().indices) {
                            var isT = false // 三处需要计算的合计值
                            if (getData()[i].gg1dm == twoDimenDataOld.gg1dm && getData()[i].gg2dm == "合计") {
                                isT = true
                            } else if (getData()[i].gg1dm == "合计" && getData()[i].gg2dm == twoDimenDataOld.gg2dm) {
                                isT = true
                            } else if (getData()[i].gg1dm == "合计" && getData()[i].gg2dm == "合计") {
                                isT = true
                            }
                            if (isT) {
                                val va = getData()[i].value + value // 计算合计
                                if (getData()[i].value >= 0 && value >= 0 && va < 0) { // 两数相加小于0，计算异常，说明超出了int值的范围
                                    isCountError = true
                                    break
                                } else {
                                    refreshMap[i] = va
                                }
                            }
                        }
                        if (isCountError) {
                            refreshMap[pos] = twoDimenDataOld.value // 当前位置，需要刷新，值改成修改前的数据
                            locationMap[pos] = editText.selectionStart
                            editText.removeTextChangedListener(textWatcherT)
                            Handler(Looper.getMainLooper()).post {
                                notifyItemChanged(pos)
                            }
                        } else {
                            refreshMap.forEach {
                                getData()[it.key].value = it.value
                                if (it.key == pos) { // 两种特殊场景：1、修改后的值为0；2、修改前后都是一位数
                                    if (it.value == 0 || (it.value.toString().length == 1 && twoDimenDataOld.value.toString().length == 1)) {
                                        locationMap[pos] = 1
                                    } else {
                                        locationMap[pos] = editText.selectionStart
                                    }
                                    editText.removeTextChangedListener(textWatcherT)
                                }
                                Handler(Looper.getMainLooper()).post {
                                    notifyItemChanged(it.key)
                                }
                            }
                        }
                    }
                }
                holder.getEditText(R.id.item_grid_edit).setTag(R.id.RecyclerView_pos, position)
                holder.getEditText(R.id.item_grid_edit).setTag(R.id.RecyclerView_item, holder.getEditText(R.id.item_grid_edit))
                holder.getEditText(R.id.item_grid_edit).setTag(R.id.RecyclerView_textWatcher, textWatcher)
                holder.getEditText(R.id.item_grid_edit).onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
                    val pos = holder.getEditText(R.id.item_grid_edit).getTag(R.id.RecyclerView_pos) as Int
                    val editText = holder.getEditText(R.id.item_grid_edit).getTag(R.id.RecyclerView_item) as EditText
                    val textWatcherT = holder.getEditText(R.id.item_grid_edit).getTag(R.id.RecyclerView_textWatcher) as TextWatcher
                    if (p1) {
                        holder.getEditText(R.id.item_grid_edit).setTag(R.id.RecyclerView_pos, pos)
                        holder.getEditText(R.id.item_grid_edit).setTag(R.id.RecyclerView_item, editText)
                        holder.getEditText(R.id.item_grid_edit).setTag(R.id.RecyclerView_textWatcher, textWatcherT)
                        holder.getEditText(R.id.item_grid_edit).addTextChangedListener(textWatcherT)
                    } else {
                        editText.isFocusable = false
                        editText.isFocusableInTouchMode = false
                        editText.removeTextChangedListener(textWatcherT)
                    }
                }
                holder.getEditText(R.id.item_grid_edit).setOnClickListener {
                    val pos = holder.getEditText(R.id.item_grid_edit).getTag(R.id.RecyclerView_pos) as Int
                    val editText = holder.getEditText(R.id.item_grid_edit).getTag(R.id.RecyclerView_item) as EditText
                    editText.isFocusable = true
                    editText.isFocusableInTouchMode = true
                    editText.requestFocus()
                }
                holder.getEditText(R.id.item_grid_edit).setText(item.value.toString())
                if (locationMap.isNotEmpty() && locationMap.containsKey(position)) {
                    holder.getEditText(R.id.item_grid_edit).addTextChangedListener(textWatcher)
                    holder.getEditText(R.id.item_grid_edit).isFocusable = true
                    holder.getEditText(R.id.item_grid_edit).isFocusableInTouchMode = true
                    holder.getEditText(R.id.item_grid_edit).requestFocus()
                    if (locationMap[position]!! <= item.value.toString().length) {
                        holder.getEditText(R.id.item_grid_edit).setSelection(locationMap[position]!!)
                    } else {
                        holder.getEditText(R.id.item_grid_edit).setSelection(item.value.toString().length)
                    }
                }
            }
            ITEM_TYPE_SUM -> {
                holder.getTextView(R.id.item_grid_text).text = item.value.toString()
            }
        }
    }

    private var listener: (Int) -> Unit = {}

    fun setOnDoubleClickListener(listener: (Int) -> Unit = {}) {
        this.listener = listener
    }

    override fun areContentsTheSame(oldItem: TableDataBean, newItem: TableDataBean, oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemPosition == newItemPosition
    }

    override fun areItemsTheSame(oldItem: TableDataBean, newItem: TableDataBean): Boolean {
        return oldItem.value == newItem.value
    }

    override fun getItemId(position: Int): Long {
        return getData()[position].position.toLong()
    }


}