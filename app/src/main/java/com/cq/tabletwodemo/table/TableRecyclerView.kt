package com.cq.tabletwodemo.table

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.cq.tabletwodemo.R
import kotlin.math.abs

/**
 * 作者: CQ
 * 日期: 2021-08-31
 * 说明: 自定义二维列表，可横竖滑动
 */
class TableRecyclerView @JvmOverloads constructor(
    context : Context, attrs : AttributeSet? = null, defStyleAttr : Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {
    // 获取滑动的最小距离
    val scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    // 是否可以刷新
    private var isRefresh = false

    // 适配器
    private var mAdapter : TableAdapter? = null

    // 回收站
    private var recycler : Recycler? = null
    private lateinit var rawWidth : IntArray
    private lateinit var rawHeight : IntArray

    // item宽度
    private var itemWidth = 100

    // item高度
    private var itemHeight = 60
    private var moveX = 0
    private var moveY = 0

    // x轴可以显示的最大宽度
    private var childShowX = 0

    // y轴可以显示的最大宽度
    private var childShowY = 0

    // x轴可以滑动的最大距离
    private var canScrollDisX = 0

    // y轴可以滑动的最大距离
    private var canScrollDisY = 0

    // 显示的第一行
    private var firstItemRow = 0

    // 显示的第一行
    private var firstItemColumn = 0

    // 显示的最后一列
    private var lastItemRow = 0

    // 显示的最后一列
    private var lastItemColumn = 0

    // 显示的左上角第一个的item的位置
    private var firstItemPos = 0

    // 记录显示在屏幕中的view
    lateinit var listView : Array<Array<View?>>
    private var isInitData = false
    private var isCanHorizontalScroll = false // 是否支持横向滑动
    private var isCanVerticalScroll = false // 是否支持横向滑动
    private var mDivider : Drawable? = null

    init {
        isRefresh = true
        mDivider = ContextCompat.getDrawable(context, R.drawable.shap_button_select_white)
        itemWidth = DensityUtil.dip2px(context, 50f) + mDivider!!.intrinsicWidth
        itemHeight = DensityUtil.dip2px(context, 30f) + mDivider!!.intrinsicWidth
    }

    fun setAdapter(adapter : TableAdapter?) {
        if (adapter == null) {
            return
        }
        val adapters = adapter as TableRVAdapter
        if (adapters.dataList == null || adapters.dataList!!.isEmpty()) {
            return
        }
        isRefresh = true
        mAdapter = adapter
        listView = array2d(adapter.getItemCountRows(), adapter.getItemCountColumns()) { null }
        recycler = Recycler(adapter.getItemViewCount())

        rawHeight = IntArray(adapter.getItemCountRows())
        rawWidth = IntArray(adapter.getItemCountColumns())
        for (y in 0 until adapter.getItemCountColumns()) {
            rawWidth[y] = itemWidth * y
        }
        for (x in 0 until adapter.getItemCountRows()) {
            rawHeight[x] = itemHeight * x
        }
        isInitData = true
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed : Boolean, l : Int, t : Int, r : Int, b : Int) {
        if (changed || isRefresh) {
            isRefresh = false
            if (mAdapter == null) {
                return
            }
            if (!isInitData) {
                return
            }
            isInitData = false
            val itemCountRows = mAdapter!!.getItemCountRows() // 获取总行数
            val itemCountColumns = mAdapter!!.getItemCountColumns() // 获取总列数
            removeAllViews()
            listView = array2d(itemCountRows, itemCountColumns)
            childShowX = 0
            childShowY = 0
            firstItemRow = 0
            firstItemColumn = 0
            val width = r - l
            val height = b - t

            isCanHorizontalScroll = width < itemCountColumns * itemWidth // 是否可以横向滑动
            isCanVerticalScroll = height < itemCountRows * itemHeight // 是否可以纵向滑动
            canScrollDisX = itemCountColumns * itemWidth - width // x轴方向可以滑动的距离
            canScrollDisY = itemCountRows * itemHeight - height // y轴方向可以滑动的距离
            for (column in 0 until itemCountColumns) { //
                if (childShowX >= width) {
                    lastItemColumn = column - 1 // 最后一列
                    break
                }
                childShowX += itemWidth
                if (column == itemCountColumns - 1) {
                    lastItemColumn = column // // 最后一列
                }
            }
            for (row in 0 until itemCountRows) { //
                if (childShowY >= height) {
                    lastItemRow = row - 1 // 最后一行
                    break
                }
                childShowY += itemHeight
                if (row == itemCountRows - 1) {
                    lastItemRow = row // 最后一行
                    break
                }
            }
            for (row in firstItemRow .. lastItemRow) { // 遍历行
                for (column in firstItemColumn .. lastItemColumn) { // 遍历列
                    val position = Utils.pointToPosition(itemCountColumns, row, column)
                    val left = itemWidth * column
                    val top = itemHeight * row
                    val right = itemWidth * (column + 1)
                    val bottom = itemHeight * (row + 1)
                    val view = makeAndSetUp(row, column, position, left, top, right, bottom)
                    listView[row][column] = view // 记录显示在屏幕的view
                }
            }
        }
    }

    private fun makeAndSetUp(row : Int, column : Int, position : Int, left : Int, top : Int, right : Int, bottom : Int) : View {
        val view = obtain(row, column, position)
        view.layout(left, top, right, bottom)
        return view
    }

    /**
     * 获取view并测量宽高
     */
    private fun obtain(row : Int, column : Int, position : Int) : View {
        var view = recycler!!.get(mAdapter!!.getItemViewType(position))
        if (view == null) {
            view = mAdapter!!.onCreateViewHolder(this, mAdapter!!.getItemViewType(position))!!
        }
        mAdapter!!.onBindViewHolder(view, row, column, position)
        view.measure(itemWidth, itemHeight)
        addView(view)
        return view
    }

    private var dispatchTouchEventOldX = 0
    private var dispatchTouchEventOldY = 0
    override fun dispatchTouchEvent(event : MotionEvent?) : Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                dispatchTouchEventOldX = event.rawX.toInt()
                dispatchTouchEventOldY = event.rawY.toInt()
                Log.e("TableRecyclerView", "dispatchTouchEvent ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                if (scrollOrientation != 0) {
                    return super.dispatchTouchEvent(event)
                }
                val newX = event.rawX.toInt()
                val newY = event.rawY.toInt()
                val moveX = dispatchTouchEventOldX - newX
                val moveY = dispatchTouchEventOldY - newY
                // 1、判断滑动是否产生（有了滑动，则UP前都走滑动逻辑）
                // 2、判断滑动的方向（横向滑动优先于纵向滑动）
                if (scrollOrientation == 0) {
                    if (abs(moveX) > scaledTouchSlop || abs(moveY) > scaledTouchSlop) {
                        scrollOrientation = if (abs(moveX) >= abs(moveY)) {
                            ScrollHorizontal
                        } else {
                            ScrollVertical
                        }
                    }
                }
                Log.e("TableRecyclerView", "dispatchTouchEvent ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                scrollOrientation == 0
                Log.e("TableRecyclerView", "dispatchTouchEvent ACTION_UP")
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event : MotionEvent?) : Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("TableRecyclerView", "onInterceptTouchEvent ACTION_DOWN ")
                onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("TableRecyclerView", "onInterceptTouchEvent ACTION_MOVE")
                if (scrollOrientation != 0)
                    return true
            }
            MotionEvent.ACTION_UP -> {
                Log.e("TableRecyclerView", "onInterceptTouchEvent ACTION_UP ")
                if (scrollOrientation != 0)
                    return true
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    private var scrollOrientation = 0 // 0、不滑动；1、左右滑动；2、上下滑动
    private val ScrollHorizontal = 1
    private val ScrollVertical = 2
    var oldX = 0
    var oldY = 0
    override fun onTouchEvent(event : MotionEvent?) : Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("TableRecyclerView", "onTouchEvent ACTION_DOWN")
                oldX = event.rawX.toInt()
                oldY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("TableRecyclerView", "onTouchEvent ACTION_MOVE")
                val newX = event.rawX.toInt()
                val newY = event.rawY.toInt()
                val moveX = oldX - newX
                val moveY = oldY - newY
                // 1、判断滑动是否产生（有了滑动，则UP前都走滑动逻辑）
                // 2、判断滑动的方向（横向滑动优先于纵向滑动）
                // 3、执行滑动逻辑
                if (scrollOrientation == ScrollHorizontal && isCanHorizontalScroll) { // 左右滑动 且 移动的距离大于屏幕最小移动距离，则属于滑动
                    scrollBy(moveX, 0)
                    oldX = newX
                }
                if (scrollOrientation == ScrollVertical && isCanVerticalScroll) { // 上下滑动
                    scrollBy(0, moveY)
                    oldY = newY
                }
            }
            MotionEvent.ACTION_UP -> {
                oldX = 0
                oldY = 0
                scrollOrientation = 0
                Log.e("TableRecyclerView", "onTouchEvent ACTION_UP")
            }
        }
        return super.onTouchEvent(event)
    }

    private var scrollAllX = 0 // x轴滑动的总距离 大于0的
    private var scrollAllY = 0 // Y轴滑动的总距离 大于0的

    override fun scrollBy(dx : Int, dy : Int) {
        if (dx == 0) {
            moveY = dy // 获取纵向移动距离
            if (moveY < 0 && scrollAllY + moveY <= 0) {  // 下滑动
                if (scrollAllY == 0) {
                    return
                }
                moveY = -scrollAllY
            } else if (moveY > 0 && scrollAllY + moveY >= canScrollDisY) {
                if (scrollAllY == canScrollDisY) {
                    return
                }
                moveY = canScrollDisY - scrollAllY
            }
            onScrollListener(0, moveY)
            while (abs(moveY) > 0) { // 产生了滑动
                val firstItemRowOld = firstItemRow // 第一行
                val lastItemRowOld = lastItemRow // 最后一行
                if (moveY < 0) { // 向下滑动
                    if (-moveY >= itemHeight) {  // 向下滑动超过了一行
                        scrollAllY -= itemHeight
                        moveY += itemHeight
                    } else {
                        scrollAllY += moveY // 不足一个item宽度，则取手指滑动的距离
                        moveY = 0
                    }
                } else {
                    if (moveY >= itemHeight) {
                        scrollAllY += itemHeight
                        moveY -= itemHeight
                    } else {
                        scrollAllY += moveY
                        moveY = 0
                    }
                }
                for (row in firstItemRowOld .. lastItemRowOld) { // 遍历行
                    for (column in firstItemColumn .. lastItemColumn) { // 遍历列
                        val view = listView[row][column]!!
                        val itemScrollLastTop = rawHeight[row] - scrollAllY
                        val itemScrollLastBottom = itemScrollLastTop + itemHeight
                        if (dy > 0) {
                            if (row == firstItemRowOld && itemScrollLastBottom <= 0) { // 第一行, 移出了屏幕，回收
                                if (column == firstItemColumn) {
                                    firstItemRow++
                                }
                                removeView(view)
                                Log.e("移出", "row = $row, column = $column")
                                listView[row][column] = null
                                continue
                            }
                            view.layout(view.left, itemScrollLastTop, view.right, itemScrollLastBottom)
                            if (row == lastItemRowOld && itemScrollLastBottom < height) { // 最后一行, 进出了屏幕，增加
                                if (column == firstItemColumn) {
                                    lastItemRow++
                                }
                                Log.e("增加", "row = ${row + 1}, column = $column")
                                val position = Utils.pointToPosition(mAdapter!!.getItemCountColumns(), row + 1, column)
                                val newView = makeAndSetUp(row + 1, column, position, view.left, itemScrollLastTop, view.right, itemScrollLastBottom)
                                listView[row + 1][column] = newView // 记录显示在屏幕的view
                                continue
                            }
                        } else {
                            if (row == lastItemRowOld && itemScrollLastTop >= height) { // 最后一行, 移出了屏幕，回收
                                if (column == firstItemColumn) {
                                    lastItemRow--
                                }
                                removeView(view)
                                listView[row][column] = null
                                continue
                            }
                            view.layout(view.left, itemScrollLastTop, view.right, itemScrollLastBottom)
                            if (row == firstItemRowOld && itemScrollLastTop > 0) { // 第一行 进出了屏幕，增加
                                if (column == firstItemColumn) {
                                    firstItemRow--
                                }
                                val position = Utils.pointToPosition(mAdapter!!.getItemCountColumns(), row - 1, column)
                                val newView = makeAndSetUp(row - 1, column, position, view.left, itemScrollLastTop, view.right, itemScrollLastBottom)
                                listView[row - 1][column] = newView // 记录显示在屏幕的view
                                continue
                            }
                        }
                    }
                }
            }
        }
        if (dy == 0) { // 横向滑动
            moveX = dx
            if (moveX < 0 && scrollAllX + moveX <= 0) { // 右滑动，可有滑动距离小于手指滑动距离(特殊情况)
                if (scrollAllX == 0) { // 已经到的最左侧，无法右滑
                    return
                }
                moveX = -scrollAllX
            } else if (moveX > 0 && scrollAllX + moveX >= canScrollDisX) { // 左滑动，可有滑动距离小于手指滑动距离(特殊情况)
                if (scrollAllX == canScrollDisX) { // 已经到的最右侧，无法左滑
                    return
                }
                moveX = canScrollDisX - scrollAllX
            }
            onScrollListener(moveX, 0)
            // 每次滑动的距离---滑动的距离超过了item的宽度(一次手指的移动，可能存在好几次的循环)
            while (abs(moveX) > 0) { // 产生了滑动
                val firstItemColumnOld = firstItemColumn // 滑动前最左侧的item
                val lastItemColumnOld = lastItemColumn // 滑动前最右侧的item // 每次循环真实移动的距离
                if (moveX < 0) { // 向右滑动，moveX是负值
                    if (-moveX >= itemWidth) {  // 向右滑动超过了一行
                        scrollAllX -= itemWidth
                        moveX += itemWidth
                    } else {
                        scrollAllX += moveX // 不足一个item宽度，则取手指滑动的距离
                        moveX = 0
                    }
                } else { // 向左滑动，moveX是正值
                    if (moveX >= itemWidth) { // 向左滑动超过了一行
                        scrollAllX += itemWidth // 等于滑动了一个item的宽度
                        moveX -= itemWidth
                    } else {
                        scrollAllX += moveX // 不足一个item宽度，则取手指滑动的距离
                        moveX = 0
                    }
                }
                for (column in firstItemColumnOld .. lastItemColumnOld) {  // 遍历列
                    for (row in firstItemRow .. lastItemRow) { // 遍历行
                        Log.e("当前View", "row = $row, column = $column")
                        val view = listView[row][column]!!
                        val itemScrollLastLeft = rawWidth[column] - scrollAllX
                        val itemScrollLastRight = rawWidth[column] - scrollAllX + itemWidth
                        if (dx > 0) {
                            if (column == firstItemColumnOld && itemScrollLastRight <= 0) { // 第一列 // 移出了屏幕，回收
                                if (row == firstItemRow) {
                                    firstItemColumn++
                                }
                                removeView(view)
                                Log.e("移出", "row = $row, column = $column")
                                listView[row][column] = null
                                continue
                            }
                            view.layout(itemScrollLastLeft, view.top, itemScrollLastRight, view.bottom)
                            if (column == lastItemColumnOld && itemScrollLastRight < width) { // 最后一列, 进出了屏幕，增加
                                if (row == firstItemRow) {
                                    lastItemColumn++
                                }
                                Log.e("增加", "row = $row, column = ${column + 1}")
                                val position = Utils.pointToPosition(mAdapter!!.getItemCountColumns(), row, column + 1)
                                val newView = makeAndSetUp(row, column + 1, position, itemScrollLastRight, view.top, itemScrollLastRight + itemWidth, view.bottom)
                                listView[row][column + 1] = newView // 记录显示在屏幕的view
                                continue
                            }
                        } else {
                            if (column == lastItemColumnOld && itemScrollLastLeft >= width) { // 最后一列, 移出了屏幕，回收
                                if (row == firstItemRow) {
                                    lastItemColumn--
                                }
                                removeView(view)
                                listView[row][column] = null
                                continue
                            }
                            view.layout(itemScrollLastLeft, view.top, itemScrollLastRight, view.bottom)
                            if (column == firstItemColumnOld && itemScrollLastLeft > 0) { // 第一列 // 进出了屏幕，增加
                                if (row == firstItemRow) {
                                    firstItemColumn--
                                }
                                val position = Utils.pointToPosition(mAdapter!!.getItemCountColumns(), row, column - 1)
                                val newView = makeAndSetUp(row, column - 1, position, itemScrollLastLeft - itemWidth, view.top, itemScrollLastLeft, view.bottom)
                                listView[row][column - 1] = newView // 记录显示在屏幕的view
                                continue
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 修改item的背景颜色
     */
    fun setItemBackgroundColor(row : Int, column : Int, isHas : Boolean) {
        Log.e("setItemBackgroundColor", "row = $row, column = $column")
        val view = getChildAt(row, column)
        if (view == null)
            return
        if (isHas) {
            view.findViewById<EditText>(R.id.item_grid_edit).setBackgroundResource(R.color.purple_500)
        } else {
            view.findViewById<EditText>(R.id.item_grid_edit).setBackgroundResource(R.color.white)
        }
    }

    /**
     * 根据位置获取view
     */
    fun getChildAt(row : Int, column : Int) : View? {
        return listView[row][column]
    }

    private var onScrollListener : (dx : Int, dy : Int) -> Unit = { dx : Int, dy : Int -> }

    /**
     * 滑动监听
     */
    fun addOnScrollListener(onScrollListener : (dx : Int, dy : Int) -> Unit = { dx : Int, dy : Int -> }) {
        this.onScrollListener = onScrollListener
    }

    /**
     * 修改对应位置的值
     */
    fun changeValue(row : Int, column : Int, value : Int) {
        val view = listView[row][column] ?: return
        view.findViewById<TableInputEditText>(R.id.item_grid_edit).setText(value.toString())
    }

    /**
     * 修改对应位置的值
     */
    fun recoveryValue(row : Int, column : Int, value : Int) {
        val view = listView[row][column] ?: return
        view.findViewById<TableInputEditText>(R.id.item_grid_edit).onChanged(value.toString(), view.findViewById<TableInputEditText>(R.id.item_grid_edit).selectionStart - 1)
    }
}