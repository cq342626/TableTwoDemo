package com.cq.tabletwodemo.recycleview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者: CQ
 * 日期: 2021-03-16
 * 说明:recycleView适配器的基础封装，可直接调用
 */
abstract class BaseRecycleViewDiffAdapter<T> constructor(var list: MutableList<T>, var context: Context) :
        RecyclerView.Adapter<BaseViewHolder>() {

    /**
     * 刷新数据 : data必须是新的list
     */
    fun setData(data: MutableList<T>?) {
        data?.let {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return list.size
                }

                override fun getNewListSize(): Int {
                    return it.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldData: T = list[oldItemPosition]
                    val newData: T = it[newItemPosition]
                    return areItemsTheSame(oldData, newData)
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldData: T = list[oldItemPosition]
                    val newData: T = it[newItemPosition]
                    return areItemContentsTheSame(oldData, newData, oldItemPosition, newItemPosition)
                }
            })
            list = data
            result.dispatchUpdatesTo(this)
        } ?: let {
            list = mutableListOf()
            notifyItemRangeChanged(0, list.size)
        }

    }

    @JvmOverloads
    fun addData(data: List<T>?, position: Int? = null) {
        if (!data.isNullOrEmpty()) {
            val listNew = mutableListOf<T>()
            listNew.addAll(list)
            with(listNew) {
                position?.let {
                    val startPosition = when {
                        it < 0 -> 0
                        it >= size -> size
                        else -> it
                    }
                    addAll(startPosition, data)
                } ?: addAll(data)
                setData(this)
            }
        }
    }

    open fun areItemContentsTheSame(oldItem: T, newItem: T, oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem == newItem
    }


    /**
     * 在BaseAdapter中实现了这2个方法，因为我们不知道子类在实现的时候是否需要改变对比的方式。比如我在使用areItemsTheSame的时候，
     * 泛型T如果泛型T不是一个基本数据类型，通常只需要对比泛型T中的唯一key就可以
     */
    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view: View = LayoutInflater.from(context).inflate(getInflate(), null)
        val viewHolder = BaseViewHolder(view)
        view.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClickListener(view, viewHolder.adapterPosition)
            }
        }
        view.setOnLongClickListener {
            if (onItemLongClickListener != null) {
                onItemLongClickListener!!.onItemLongClickListener(view, viewHolder.adapterPosition)
            }
            true
        }
        return viewHolder
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindData(holder, list[position], position)
    }

    /**
     * 布局
     */
    abstract fun getInflate(): Int

    /**
     * 赋值
     */
    protected abstract fun bindData(holder: BaseViewHolder, data: T, position: Int)


}