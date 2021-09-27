package com.cq.tabletwodemo.table

/**
 * 作者: CQ
 * 日期: 2021-09-01
 * 说明:
 */
object Utils {
    fun pointToPosition(count : Int, row : Int, column : Int) : Int {
        return row * (count - 1) + column
    }

    inline fun <reified INNER> array2d(sizeOuter : Int, sizeInner : Int, noinline innerInit : (Int) -> INNER) : Array<Array<INNER>> = Array(sizeOuter) { Array<INNER>(sizeInner, innerInit) }

    fun array2dOfInt(sizeOuter : Int, sizeInner : Int) : Array<IntArray> = Array(sizeOuter) { IntArray(sizeInner) }
    fun array2dOfLong(sizeOuter : Int, sizeInner : Int) : Array<LongArray> = Array(sizeOuter) { LongArray(sizeInner) }
    fun array2dOfByte(sizeOuter : Int, sizeInner : Int) : Array<ByteArray> = Array(sizeOuter) { ByteArray(sizeInner) }
    fun array2dOfChar(sizeOuter : Int, sizeInner : Int) : Array<CharArray> = Array(sizeOuter) { CharArray(sizeInner) }
    fun array2dOfBoolean(sizeOuter : Int, sizeInner : Int) : Array<BooleanArray> = Array(sizeOuter) { BooleanArray(sizeInner) }

}

fun array2dOfInt(sizeOuter : Int, sizeInner : Int) : Array<IntArray> = Array(sizeOuter) { IntArray(sizeInner) }

inline fun <reified INNER> array2d(rows : Int, columns : Int, noinline innerInit : (Int) -> INNER) : Array<Array<INNER>> = Array(rows) { Array(columns, innerInit) }

inline fun <reified INNER> array2d(rows : Int, columns : Int) : Array<Array<INNER?>> = Array(rows) { arrayOfNulls<INNER>(columns) }

inline fun <reified INNER> array2d() : Array<Array<INNER?>> = emptyArray()