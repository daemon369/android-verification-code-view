package me.daemon.verificationcode

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import me.daemon.view.common.sp2px


/**
 * @author daemon
 * @since 2019-06-25 16:50
 */
class VerificationCodeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sb = StringBuilder()
    private val textPaint = Paint()

    var capacity = 4
        set(value) {
            if (field == value) return
            field = value
            postInvalidate()
        }

    var gridBackground: Drawable? = null
        set(value) {
            if (field == value) return
            field = value
            postInvalidate()
        }

    var gridDividerSize = 0
        set(value) {
            if (field == value) return
            field = value
            postInvalidate()
        }

    var textSize = context.sp2px(14f)
        set(value) {
            if (field == value) return
            field = value
            postInvalidate()
        }

    var textColor = Color.BLACK
        set(value) {
            if (field == value) return
            field = value
            textPaint.color = textColor
            postInvalidate()
        }

    private var gridWidth = 0
    private var gridHeight = 0

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        textPaint.apply {
            isAntiAlias = true
            color = textColor
            textSize = this@VerificationCodeView.textSize
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val gridCount = capacity
        gridWidth = when (gridCount) {
            0 -> 0
            else -> (measuredWidth - gridDividerSize * (gridCount - 1)) / gridCount
        }
        gridHeight = measuredHeight - paddingTop - paddingBottom
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return super.onDraw(canvas)

        // draw grids
        val gridCount = capacity
        gridBackground?.let {
            var left = paddingLeft
            val top = paddingTop
            val bottom = top + gridHeight
            for (i in 0 until gridCount) {
                it.setBounds(left, top, left + gridWidth, bottom)
                it.draw(canvas)
                left += gridWidth + gridDividerSize + 1
            }
        }

        val len = sb.length
        val str = sb.toString()

        for (i in 0 until len) {
            canvas.drawText(str, i, i + 1, i * 50.toFloat(), 100f, textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        requestFocus()
        requestFocusFromTouch()

        // 触摸控件时显示 键盘
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            val imm = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        }
        return true
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs ?: return super.onCreateInputConnection(outAttrs)

        // 声明一个数字数字键盘
        val fic = BaseInputConnection(this, false)
        outAttrs.actionLabel = null
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NONE or
                EditorInfo.IME_FLAG_NO_FULLSCREEN or
                EditorInfo.IME_FLAG_NO_EXTRACT_UI
        return fic
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                if (sb.isNotEmpty()) {
                    sb.deleteCharAt(sb.length - 1)
                    invalidate()
                }
            }
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                if (sb.length < capacity) {
                    sb.append(keyCode - KeyEvent.KEYCODE_0)
                    invalidate()
                }
            }
            KeyEvent.KEYCODE_ENTER -> {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
            else -> {
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}