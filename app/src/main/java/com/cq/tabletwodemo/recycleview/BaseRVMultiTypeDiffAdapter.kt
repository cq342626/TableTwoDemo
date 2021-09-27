package com.cq.tabletwodemo.recycleview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者: CQ
 * 日期: 2021-03-16
 * 说明:recycleView适配器的基础封装，可直接调用
 */
abstract class BaseRVMultiTypeDiffAdapter<T : Cloneable> :
        RecyclerView.Adapter<BaseRVMultiTypeDiffAdapter.MultiTypeViewHolder>() {

    //定义的数据，默认初始化数据并加载
    private var list: List<T> = mutableListOf()

    var realListData : MutableList<T>? = null

    fun onAutoData(data: List<T>?){
        list = data!!
        notifyDataSetChanged()
    }

    fun getData(pos : Int) : T {
        return list[pos]
    }

    fun getData() = list

    /**
     * 获取 T
     */
    fun getItem(position: Int): T {
        return list[position]
    }


    /**
     * 引用自定义的Holder：MultiTypeViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiTypeViewHolder {
        return MultiTypeViewHolder(onCreateMultiViewHolder(parent, viewType))
    }


    /**
     * 绑定数据
     */
    override fun onBindViewHolder(holder: MultiTypeViewHolder, position: Int) {
        onBindViewHolder(holder, getItem(position), position)
    }

    /**
     * 获取数据的总数量
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * 可重写：在BaseAdapter中实现了这2个方法，因为我们不知道子类在实现的时候是否需要改变对比的方式。比如我在使用areItemsTheSame的时候，
     * 泛型T如果泛型T不是一个基本数据类型，通常只需要对比泛型T中的唯一key就可以
     */
    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    /**
     * 可重写
     */
    open fun areContentsTheSame(oldItem: T, newItem: T, oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem == newItem
    }

    /**
     * 创建一个公共的ViewHolder，避免重复创建
     */
    class MultiTypeViewHolder(var view: View) : BaseViewHolder(view)

    /**
     * 方法调动：加载（不同的）ViewDataBinding
     */
    protected fun loadLayout(layout: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    /**
     * 子类重写：返回多类型的布局
     */
    abstract fun onCreateMultiViewHolder(parent: ViewGroup, viewType: Int): View

    /**
     * 子类重写：绑定数据
     */
    abstract fun onBindViewHolder(holder: MultiTypeViewHolder, item: T, position: Int)
}