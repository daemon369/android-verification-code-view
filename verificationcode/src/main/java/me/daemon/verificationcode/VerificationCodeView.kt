package me.daemon.verificationcode

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager


/**
 * @author daemon
 * @since 2019-06-25 16:50
 */
class VerificationCodeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sb = StringBuilder()
    private val paint = Paint()

    var size = 4

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        paint.apply {
            color = Color.BLACK
            textSize = 50f
            strokeWidth = 3f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return super.onDraw(canvas)

        val len = sb.length
        val str = sb.toString()

        paint.color = Color.BLACK
        for (i in 0 until len) {
            canvas.drawText(str, i, i + 1, i * 50.toFloat(), 100f, paint)
        }

        paint.color = Color.BLUE
        canvas.drawLine(0f, 100f, 1000f, 100f, paint)
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

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs ?: return super.onCreateInputConnection(outAttrs)

        // 声明一个数字数字键盘
        val fic = BaseInputConnection(this, false)
        outAttrs.actionLabel = null
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NONE
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
                if (sb.length < size) {
                    sb.append(keyCode - KeyEvent.KEYCODE_0)
                    invalidate()
                }
            }
            KeyEvent.KEYCODE_ENTER -> {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
            else -> {
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}