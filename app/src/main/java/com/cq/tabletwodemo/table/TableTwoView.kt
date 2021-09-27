package com.cq.tabletwodemo.table

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cq.tabletwodemo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 作者: CQ
 * 日期: 2021-09-06
 * 说明:
 */
class TableTwoView @JvmOverloads constructor(
    context : Context, attrs : AttributeSet? = null, defStyleAttr : Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr), OnTableScrollListener {
    // 头部列表
    lateinit var recyclerViewTop : RecyclerView

    // 左侧列表
    lateinit var recyclerViewLeft : RecyclerView

    // 中间列表
    lateinit var recyclerViewContent : TableRecyclerView

    // 头部列表适配器
    private var adapterTop = CommonAdapter()

    // 左侧列表适配器
    private var adapterLeft = CommonAdapter()

    // 中间列表适配器
    private lateinit var adapterContent : TableRVAdapter

    // 头部列表数据（横向线行）
    private var listDataTop = mutableListOf<TableDataBean>()

    // 左侧列表数据（纵向线行）
    private var listDataLeft = mutableListOf<TableDataBean>()

    // 中间列表数据（横向网格）
    private var dataListContent = array2d<TableDataBean>()

    // 是否是顶部recycleView移动
    var isIntoTop = false

    // 是否是中间recycleView移动
    var isIntoContent = false

    // 是否是左边recycleVIew移动
    var isIntoLeft = false

    /**
     * 同步滑动，及对应item颜色同步
     */
    private var onTableScrollListener : OnTableScrollListener? = null

    fun setOnTableScrollListener(onTableScrollListener : OnTableScrollListener) {
        this.onTableScrollListener = onTableScrollListener
    }

    init {
        initView()
    }

    private fun initView() {
        val view = View.inflate(context!!, R.layout.view_table_two, this) // 头部列表
        recyclerViewTop = view.findViewById(R.id.recyclerView_top) // 左侧列表
        recyclerViewLeft = view.findViewById(R.id.recyclerView_left) // 中间列表
        recyclerViewContent = view.findViewById(R.id.recyclerView_content) // 滑动控件
        // 头部列表设置
        // 定义一个线性布局
        val linearLayoutManager = LinearLayoutManager(context)
        // 设置横向
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        // 头部列表设置布局器
        recyclerViewTop.layoutManager = linearLayoutManager
        // 定义一个适配器
        recyclerViewTop.addItemDecoration(LinearItemDecoration(context))
        recyclerViewTop.adapter = adapterTop
        // 左侧列表设置
        // 定义一个线性布局
        val linearLayoutManagerLeft = LinearLayoutManager(context)
        // 设置纵向
        linearLayoutManagerLeft.orientation = RecyclerView.VERTICAL
        // 左侧列表设置布局器
        recyclerViewLeft.layoutManager = linearLayoutManagerLeft
        // 定义一个适配器
        recyclerViewLeft.addItemDecoration(LinearItemDecoration(context))
        recyclerViewLeft.adapter = adapterLeft
        // 中间列表设置
        adapterContent = TableRVAdapter(context, dataListContent)
        recyclerViewContent.setAdapter(adapterContent)

        adapterContent.addOnTextChanged { row, column, value ->
            val valueOld = dataListContent[row][column]!!.value

            dataListContent[row][column]!!.value = value
            if (valueOld == value) {
                return@addOnTextChanged
            }
            val changeValue = value - valueOld
            val rowSumNumber = dataListContent[row][listDataTop.size - 1]!!.value + changeValue
            val columnSumNumber = dataListContent[listDataLeft.size - 1][column]!!.value + changeValue
            val sumNumber = dataListContent[listDataLeft.size - 1][listDataTop.size - 1]!!.value + changeValue

            if (rowSumNumber < 0 || columnSumNumber < 0 || sumNumber < 0) { // 两数相加小于0，计算异常，说明超出了int值的范围
                GlobalScope.launch(Dispatchers.Main) { recyclerViewContent.changeValue(row, column, value) }
                return@addOnTextChanged
            }

            GlobalScope.launch(Dispatchers.Main) {
                // 行合计
                dataListContent[row][listDataTop.size - 1]!!.value = rowSumNumber
                recyclerViewContent.changeValue(row, listDataTop.size - 1, rowSumNumber)
                // 列合计
                dataListContent[listDataLeft.size - 1][column]!!.value = columnSumNumber
                recyclerViewContent.changeValue(listDataLeft.size - 1, column, columnSumNumber)
                // 总合计
                dataListContent[listDataLeft.size - 1][listDataTop.size - 1]!!.value = sumNumber
                recyclerViewContent.changeValue(listDataLeft.size - 1, listDataTop.size - 1, sumNumber)
            }
        }
        adapterContent.setItemFocusListener { row, column, hasFocu ->
            onTableScrollListener?.onItemHasFocus(row, column, hasFocu)
        }
        // 滑动
        onScrollRelation()
    }

    /**
     * 滑动关联
     */
    private fun onScrollRelation() {
        recyclerViewLeft.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {
                super.onScrolled(recyclerView, dx, dy) // 滑动的是左侧列表
                if (!isIntoTop && isIntoLeft && !isIntoContent) { // 头部列表滑动时，关联中间列表同步滑动，由于中间列表上下滑动是NestedScrollView控制的，所以滑动NestedScrollView
                    recyclerViewTop.scrollBy(dx, dy)
                    recyclerViewContent.scrollBy(dx, dy)
                    onTableScrollListener?.onTableScroll(dx, dy)
                    onTableScrollListener?.onTableClearState()
                }
            }
        })

        recyclerViewTop.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {
                super.onScrolled(recyclerView, dx, dy) // 滑动的是左侧列表
                if (isIntoTop && !isIntoLeft && !isIntoContent) { // 头部列表滑动时，关联中间列表同步滑动，由于中间列表上下滑动是NestedScrollView控制的，所以滑动NestedScrollView
                    recyclerViewLeft.scrollBy(dx, dy)
                    recyclerViewContent.scrollBy(dx, dy)
                    onTableScrollListener?.onTableClearState()
                    onTableScrollListener?.onTableScroll(dx, dy)
                }
            }
        })

        recyclerViewContent.addOnScrollListener { dx, dy ->
            if (!isIntoTop && !isIntoLeft && isIntoContent) { //
                recyclerViewTop.scrollBy(dx, dy)
                recyclerViewLeft.scrollBy(dx, dy)
                onTableScrollListener?.onTableClearState()
                onTableScrollListener?.onTableScroll(dx, dy)
            }
        }

    }

    // 定义好的二维数据
    //    val data = Array(100) { Array(100) { "" } }
    fun setDataTop(list : MutableList<TableDataBean>) { // 主要是添加合计字段
        val topBean = TableDataBean()
        topBean.type = 0
        topBean.gg1dm = "合计"
        list.add(topBean)
        listDataTop = list
    }

    fun setDataLeft(list : MutableList<TableDataBean>) { // 主要是添加合计字段
        val leftBean = TableDataBean()
        leftBean.type = 1
        leftBean.gg2dm = "合计"
        list.add(leftBean)
        listDataLeft = list
    }

    fun setDataContent(data : Array<Array<TableDataBean?>>) { // 主要是添加合计字段
        if (data.isEmpty())
            return
        val rows = data.size
        val columns = data[0].size
        val mapRows = mutableMapOf<String, Int>()
        val mapColumns = mutableMapOf<String, Int>()
        var allValue = 0
        for (row in 0 until rows) { // 遍历行
            for (column in 0 until columns) { // 遍历列
                if (row < rows - 1 && column < columns - 1) { // 计算合计
                    if (mapRows.containsKey(data[row][column]!!.gg2dm)) { // 计算规格2之间的值
                        mapRows[data[row][column]!!.gg2dm] = mapRows[data[row][column]!!.gg2dm]!! + data[row][column]!!.value
                    } else {
                        mapRows[data[row][column]!!.gg2dm] = data[row][column]!!.value
                    }
                    if (mapColumns.containsKey(data[row][column]!!.gg1dm)) { // 计算规格1之间的值
                        mapColumns[data[row][column]!!.gg1dm] = mapColumns[data[row][column]!!.gg1dm]!! + data[row][column]!!.value
                    } else {
                        mapColumns[data[row][column]!!.gg1dm] = data[row][column]!!.value
                    }
                    continue
                }
                val tableDataBean = TableDataBean()
                tableDataBean.isEdit = false
                tableDataBean.row = row
                tableDataBean.column = column
                tableDataBean.position = Utils.pointToPosition(columns, row, column)
                if (row == rows - 1 && column == columns - 1) {
                    tableDataBean.gg1id = -1
                    tableDataBean.gg1dm = "合计"
                    tableDataBean.gg1mc = "合计"
                    tableDataBean.gg2id = -1
                    tableDataBean.gg2dm = "合计"
                    tableDataBean.gg2mc = "合计"

                    tableDataBean.type = 3
                    tableDataBean.value = allValue
                    data[row][column] = tableDataBean
                } else if (column == columns - 1) { // 最后一列，添加纵向合计数据
                    tableDataBean.gg2id = -1
                    tableDataBean.gg2dm = "合计"
                    tableDataBean.gg2mc = "合计"
                    tableDataBean.gg1id = data[row][column - 1]!!.gg1id
                    tableDataBean.gg1dm = data[row][column - 1]!!.gg1dm
                    tableDataBean.gg1mc = data[row][column - 1]!!.gg1mc

                    tableDataBean.type = 3
                    tableDataBean.value = mapRows[data[row][column - 1]!!.gg2dm]!!
                    data[row][column] = tableDataBean
                } else if (row == rows - 1) { // 最后一行，添加横向合计数据
                    tableDataBean.gg1id = -1
                    tableDataBean.gg1dm = "合计"
                    tableDataBean.gg1mc = "合计"
                    tableDataBean.gg2id = data[row - 1][column]!!.gg2id
                    tableDataBean.gg2dm = data[row - 1][column]!!.gg2dm
                    tableDataBean.gg2mc = data[row - 1][column]!!.gg2mc

                    tableDataBean.type = 3
                    tableDataBean.value = mapColumns[data[row - 1][column]!!.gg1dm]!!
                    allValue += tableDataBean.value
                    data[row][column] = tableDataBean
                }
            }
        }
        dataListContent = data
    }

    fun onRefreshRV() {
        adapterTop.onAutoData(listDataTop)
        adapterLeft.onAutoData(listDataLeft)
        adapterContent.onAutoData(dataListContent)
        recyclerViewContent.setAdapter(adapterContent)
    }

    // 监听手指按下，获取按下的位置，判断按下的位置属于哪个区域
    override fun dispatchTouchEvent(event : MotionEvent?) : Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                isIntoTop = recyclerViewTop.isTouchPointInView(event.rawX.toInt(), event.rawY.toInt())
                isIntoLeft = recyclerViewLeft.isTouchPointInView(event.rawX.toInt(), event.rawY.toInt())
                isIntoContent = recyclerViewContent.isTouchPointInView(event.rawX.toInt(), event.rawY.toInt()) // 横向或者纵向移动时，布局移动，可以导致按下的位置可能也在中间布局，
                if (isIntoTop || isIntoLeft) {
                    isIntoContent = false
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * 同步滑动
     */
    override fun onTableScroll(dx : Int, dy : Int) {
        recyclerViewLeft.scrollBy(dx, dy)
        recyclerViewTop.scrollBy(dx, dy)
        recyclerViewContent.scrollBy(dx, dy)
    }

    /**
     * 清除点击位置，防止调用滑动时走了滑动监听内部逻辑
     */
    override fun onTableClearState() {
        isIntoTop = false
        isIntoLeft = false
        isIntoContent = false
    }

    /**
     * item获取或者失去焦点时，修改对应的背景颜色
     */
    override fun onItemHasFocus(row : Int, column : Int, isHas : Boolean) {
        recyclerViewContent.setItemBackgroundColor(row, column, isHas)
    }

}