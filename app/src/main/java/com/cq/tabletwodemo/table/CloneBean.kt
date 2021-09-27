package com.cq.tabletwodemo.table

/**
 * 作者: CQ
 * 日期: 2021-08-11
 * 说明: 复制类（属性拥有新的空间）
 */
open class CloneBean : Cloneable {
    fun onCopy(): CloneBean {
        return super.clone() as CloneBean
    }
}