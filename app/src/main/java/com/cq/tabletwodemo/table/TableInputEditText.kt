package com.cq.tabletwodemo.table

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.cq.tabletwodemo.R

/**
 * 作者: CQ
 * 日期: 2021-08-19
 * 说明:
 */
@SuppressLint("AppCompatCustomView")
class TableInputEditText @JvmOverloads constructor(
    context : Context, attrs : AttributeSet? = null, defStyleAttr : Int = 0,
) : EditText(context, attrs, defStyleAttr) {
    private var onInputListener : OnInputListener? = null
    private var textLast = ""
    private var onChangedText : (Double) -> Unit = {}

    // item宽度
    private var itemWidth = 100

    // item高度
    private var itemHeight = 60
    private var mDivider : Drawable? = null

    init {
        mDivider = ContextCompat.getDrawable(context, R.drawable.shap_button_select_white)
        itemWidth = DensityUtil.dip2px(context, 50f)
        itemHeight = DensityUtil.dip2px(context, 30f)
    }

    fun addChangedInputTextListener(onInputListener : OnInputListener) {
        addTextChangedListener(textWatcher)
        textLast = text.toString()
        this.onInputListener = onInputListener
    }

    fun addChangedInputTextListener(onChangedText : (Double) -> Unit) {
        addTextChangedListener(textWatcher)
        textLast = text.toString()
        this.onChangedText = onChangedText
    }

    fun removeChangedInputTextListener() {
        removeTextChangedListener(textWatcher)
    }

    override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(itemWidth, itemHeight)
    }

    private var textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0 : CharSequence?, p1 : Int, p2 : Int, p3 : Int) {
        }

        override fun onTextChanged(p0 : CharSequence?, p1 : Int, p2 : Int, p3 : Int) {
        }

        override fun afterTextChanged(p0 : Editable?) {
            var inputText = p0.toString()
            if (inputText == "") { // 全部为空，默认输入为0
                inputText = "0"
                onChanged(inputText, 1)
            } else if (inputText.startsWith(".")) { // 删除小数点前面所有的值或在第一位输入小数点(1.0变成.0或者是1变成.1，自动补充整数位0)
                inputText = "0$inputText" // 默认补充点前面的0，组成正常的小数
                if (textLast.indexOf(".") == 1) { // 上一次输入的值有小数点，说明是删除小数点前面所有的值
                    onChanged(inputText, 1)
                } else { // 第一位输入小数点
                    onChanged(inputText, 2)
                }
            } else if (inputText.contains(".")) { // 小数计算整数位0
                val strs = inputText.split(".")  // 分割整数位和小数位数据
                if (strs[0].toInt().toString() != strs[0]) {  // 如果整数位开头包括0，转正常数值（100.1变成00.1或者0.1变成00.1）
                    inputText = "${strs[0].toInt()}.${strs[1]}"
                    if (textLast.split(".")[0].toInt() == 0) { // 上一次输入的值有小数点，说明是删除小数点前面的值
                        onChanged(inputText, 1)
                    } else {
                        onChanged(inputText, 0)
                    }
                }
            } else { // 整数计算整数位0
                if (inputText.toIntOrNull() == null) {
                    onChanged(textLast, selectionStart - 1)
                    return
                }
                if (inputText.toInt().toString() != inputText) { // （100变成00，0变08）
                    inputText = inputText.toInt().toString()
                    if (textLast.toInt() == 0) {
                        onChanged(inputText, 1)
                    } else {
                        onChanged(inputText, 0)
                    }
                }
            }
            textLast = inputText
            onChangedText(inputText.toDouble())
            onInputListener?.onChanged(inputText.toDouble())
        }
    }

    /**
     * 是否监听点击edittext时的光标位置，设置后可以自己定义
     */
    var isChangedSelectAll = false

    /**
     * 焦点位置变化监听（位置不变，是不会调用的）
     */
    override fun onSelectionChanged(selStart : Int, selEnd : Int) {
        super.onSelectionChanged(selStart, selEnd)
        Log.e("onSelectionChanged", "isChangedSelectAll = $isChangedSelectAll, selStart = $selStart, selEnd = $selEnd")
        if (isChangedSelectAll) {
            isChangedSelectAll = false
            selectAll() // 全选
        }
    }

    override fun dispatchTouchEvent(event : MotionEvent?) : Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("TableInputEditText", "dispatchTouchEvent ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("TableInputEditText", "dispatchTouchEvent ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                Log.e("TableInputEditText", "dispatchTouchEvent ACTION_UP")
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event : MotionEvent?) : Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("TableInputEditText", "onTouchEvent ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("TableInputEditText", "onTouchEvent ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                Log.e("TableInputEditText", "onTouchEvent ACTION_UP")
            }
        }
        return super.onTouchEvent(event)
    }

    fun onChanged(text : String, pos : Int) {
        removeTextChangedListener(textWatcher)
        setText(text)
        if (pos == -1) {
            setSelection(text.length)
        } else {
            setSelection(pos)
        }
        addTextChangedListener(textWatcher)
    }

    override fun addTextChangedListener(watcher : TextWatcher?) {
        isHasTextChangedListener = true
        super.addTextChangedListener(watcher)
    }

    override fun removeTextChangedListener(watcher : TextWatcher?) {
        isHasTextChangedListener = false
        super.removeTextChangedListener(watcher)
    }

    var isHasTextChangedListener = false

}
