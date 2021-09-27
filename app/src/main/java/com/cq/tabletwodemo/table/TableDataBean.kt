package com.cq.tabletwodemo.table

/**
 * 作者: CQ
 * 日期: 2021-08-11
 * 说明:
 */
class TableDataBean : CloneBean() {
    var position = 0
    var row = 0
    var column = 0
    var type = 0 // 类型：0、横向头部；1、纵向头部；2、中间数据；3、合计数据
    var gg1id = -1
    var gg2id = -1
    var gg1dm = ""
    var gg1mc = ""
    var gg2dm = ""
    var gg2mc = ""
    var value = 0
    var isEdit = true

}