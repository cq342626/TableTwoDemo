package com.cq.tabletwodemo.table

import com.cq.tabletwodemo.databinding.ActivityTableBinding
import com.cq.tabletwodemo.databinding.DataBindingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 作者: CQ
 * 日期: 2021-08-31
 * 说明:
 */
class TableActivity : DataBindingActivity<ActivityTableBinding>() {
    override fun ActivityTableBinding.initBinding() {

        GlobalScope.launch(Dispatchers.Main) {
            init()
            mBinding.tableRecyclerView.onRefreshRV()
        }

        GlobalScope.launch(Dispatchers.Main) {
            init2()
            mBinding.tableRecyclerView2.onRefreshRV()
        }
        mBinding.tableRecyclerView.setOnTableScrollListener(mBinding.tableRecyclerView2)
        mBinding.tableRecyclerView2.setOnTableScrollListener(mBinding.tableRecyclerView)

    }

    suspend fun init() = withContext(Dispatchers.IO) {
        val listTop = mutableListOf<TableDataBean>() //                for (i in mutableListOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R")) {
        for (i in mutableListOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L")) {
            val twoDimenData = TableDataBean()
            twoDimenData.type = 0
            twoDimenData.gg1dm = i
            listTop.add(twoDimenData)
        }
        val listLeft = mutableListOf<TableDataBean>()
        for (i in 1 .. 30) {
            val twoDimenData = TableDataBean()
            twoDimenData.type = 1
            twoDimenData.gg2dm = i.toString()
            listLeft.add(twoDimenData)
        }

        val listContent = array2d<TableDataBean>(listLeft.size + 1, listTop.size + 1)
        for (rows in 0 until listLeft.size) {
            for (columns in 0 until listTop.size) {
                val twoDimenData = TableDataBean()
                twoDimenData.value = 1
                twoDimenData.type = 2
                twoDimenData.gg1dm = listTop[columns].gg1dm
                twoDimenData.gg2dm = listLeft[rows].gg2dm
                listContent[rows][columns] = twoDimenData
            }
        }
        mBinding.tableRecyclerView.setDataTop(listTop)
        mBinding.tableRecyclerView.setDataLeft(listLeft)
        mBinding.tableRecyclerView.setDataContent(listContent)
    }

    suspend fun init2() = withContext(Dispatchers.IO) {
        val listTop = mutableListOf<TableDataBean>() //                for (i in mutableListOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R")) {
        for (i in mutableListOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L")) {
            val twoDimenData = TableDataBean()
            twoDimenData.type = 0
            twoDimenData.gg1dm = i
            listTop.add(twoDimenData)
        }
        val listLeft = mutableListOf<TableDataBean>()
        for (i in 1 .. 30) {
            val twoDimenData = TableDataBean()
            twoDimenData.type = 1
            twoDimenData.gg2dm = i.toString()
            listLeft.add(twoDimenData)
        }

        val listContent = array2d<TableDataBean>(listLeft.size + 1, listTop.size + 1)
        for (rows in 0 until listLeft.size) {
            for (columns in 0 until listTop.size) {
                val twoDimenData = TableDataBean()
                twoDimenData.isEdit = false
                twoDimenData.value = 1
                twoDimenData.type = 2
                twoDimenData.gg1dm = listTop[columns].gg1dm
                twoDimenData.gg2dm = listLeft[rows].gg2dm
                listContent[rows][columns] = twoDimenData
            }
        }
        mBinding.tableRecyclerView2.setDataTop(listTop)
        mBinding.tableRecyclerView2.setDataLeft(listLeft)
        mBinding.tableRecyclerView2.setDataContent(listContent)
    }

}