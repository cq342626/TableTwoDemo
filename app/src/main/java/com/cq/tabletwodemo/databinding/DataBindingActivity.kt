package com.cq.tabletwodemo.databinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * 作者: CQ
 * 日期: 2021-05-25
 * 说明:
 */
abstract class DataBindingActivity<VB : ViewBinding> : AppCompatActivity(), BaseViewBinding<VB> {

    protected val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewBinding(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.initBinding()
        intData()
    }
    open fun intData(){}
}