package com.cq.tabletwodemo.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 作者: CQ
 * 日期: 2021-05-25
 * 说明: 通过反射的机制，从拿到ActivityMainBinding中的inflate方法，使用相对应的inflate方法去加载我们的布局
 */
inline fun <VB : ViewBinding> Any.getViewBinding(inflater: LayoutInflater): VB {
    val vbClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
    val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
    return inflate.invoke(null, inflater) as VB

//        val clazz = (javaClass.genericSuperclass as Parameterizedreified ype).actualTypeArguments[0] as Class<VB>
//        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
//        return method.invoke(null, inflater) as VB
}

inline fun <VB : ViewBinding> Any.getViewBinding(inflater: LayoutInflater, viewGroup : ViewGroup?) : VB {
    val vbClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
    val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    return inflate.invoke(null, inflater, viewGroup, false) as VB
}

inline fun <VB : ViewBinding> Any.getViewBinding(vbClass: Class<VB>, inflater: LayoutInflater, viewGroup : ViewGroup?) : VB {
    val inflate = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    return inflate.invoke(null, inflater, viewGroup, false) as VB
}



interface BaseViewBinding<VB : ViewBinding> {
    fun VB.initBinding()
}