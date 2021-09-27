package com.cq.tabletwodemo.table

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cq.tabletwodemo.R

/**
 * 作者: CQ
 * 日期: 2021-08-07
 * 说明:
 */
class LinearItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable? = null

    private var left = 0
    private var top = 0
    private var right = 0
    private var bottom = 0

    private var isOverDraw = false

    init {
        mDivider = ContextCompat.getDrawable(context, R.drawable.shap_button_select_white)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if ((parent.layoutManager as LinearLayoutManager).orientation == RecyclerView.VERTICAL) {
            outRect.left = mDivider!!.intrinsicWidth
            outRect.right = mDivider!!.intrinsicWidth
            outRect.bottom = mDivider!!.intrinsicHeight
        } else {
            outRect.top = mDivider!!.intrinsicHeight
            outRect.bottom = mDivider!!.intrinsicHeight
            outRect.right = mDivider!!.intrinsicWidth
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        isOverDraw = true
        val number = parent.adapter!!.itemCount // item总数
        val orientation = (parent.layoutManager as LinearLayoutManager).orientation
        if (orientation == RecyclerView.VERTICAL) {
            for (i in 0 until number) {
                val view = parent.getChildAt(i)
                if (view == null) continue
                drawLeft(c, view, orientation)
                drawRight(c, view, orientation)
                drawBottom(c, view, orientation)
            }
        } else {
            for (i in 0 until number) {
                val view = parent.getChildAt(i)
                if (view == null) continue
                drawTop(c, view, orientation)
                drawBottom(c, view, orientation)
                drawRight(c, view, orientation)
            }
        }
    }


    private fun drawTop(canvas: Canvas, child: View, orientation: Int) {
        canvas.save()
        left = child.left
        top = child.top - mDivider!!.intrinsicHeight
        right = if (orientation == RecyclerView.HORIZONTAL) {
            child.right + mDivider!!.intrinsicWidth
        } else {
            child.right
        }
        bottom = top + mDivider!!.intrinsicHeight
        mDivider!!.setBounds(left, top, right, bottom)
        mDivider!!.draw(canvas)
        canvas.restore()
    }

    private fun drawLeft(canvas: Canvas, child: View, orientation: Int) {
        canvas.save()
        left = child.left - mDivider!!.intrinsicWidth
        top = child.top
        right = left + mDivider!!.intrinsicWidth
        bottom = if (orientation == RecyclerView.VERTICAL) {
            child.bottom + mDivider!!.intrinsicWidth
        } else {
            child.bottom
        }
        mDivider!!.setBounds(left, top, right, bottom)
        mDivider!!.draw(canvas)
        canvas.restore()
    }

    private fun drawBottom(canvas: Canvas, child: View, orientation: Int) {
        canvas.save()
        left = child.left
        top = child.bottom
        right = if (orientation == RecyclerView.HORIZONTAL) {
            child.right + mDivider!!.intrinsicWidth
        } else {
            child.right
        }
        bottom = child.bottom + mDivider!!.intrinsicHeight
        mDivider!!.setBounds(left, top, right, bottom)
        mDivider!!.draw(canvas)
        canvas.restore()
    }

    private fun drawRight(canvas: Canvas, child: View, orientation: Int) {
        canvas.save()
        left = child.right
        top = child.top
        right = left + mDivider!!.intrinsicWidth
        bottom = if (orientation == RecyclerView.VERTICAL) {
            child.bottom + mDivider!!.intrinsicWidth
        } else {
            child.bottom
        }
        mDivider!!.setBounds(left, top, right, bottom)
        mDivider!!.draw(canvas)
        canvas.restore()
    }
}