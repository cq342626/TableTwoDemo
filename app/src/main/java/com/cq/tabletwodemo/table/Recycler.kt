package com.cq.tabletwodemo.table

import android.view.View
import java.util.*

/**
 * 作者: CQ
 * 日期: 2021-09-01
 * 说明:
 */
class Recycler constructor(count: Int) {

    private var views: Array<Stack<View>?> = arrayOfNulls(count)

    init {
        for (i in views.indices) {
            views[i] = Stack<View>()
        }
    }

    fun get(raw: Int): View? {
        return try {
            views[raw]!!.pop()
        } catch (e: Exception) {
            null
        }
    }

    fun set(raw : Int, view : View) {
        views[raw]!!.push(view)
    }

}