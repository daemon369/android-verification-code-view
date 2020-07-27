package me.daemon.verificationcode

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.os.SystemClock
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import me.daemon.view.common.between
import me.daemon.view.common.sp2px


/**
 * @author daemon
 * @since 2019-06-25 16:50
 * @see [GitHub](https://github.com/daemon369/VerificationCodeView)
 * @see [jcenter](https://bintray.com/beta/#/daemon336699/maven/verificationcodeview)
 */
class VerificationCodeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val BLINK = 500
    }

    private val sb = StringBuilder()
    private val textPaint = Paint()
    private val cursorPaint = Paint()
    private val auxiliaryPaint = Paint()

    private val blink by lazy { Blink() }

    val verificationCode: String
        get() = sb.toString()

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
            textPaint.textSize = field
            postInvalidate()
        }

    var textColor = Color.BLACK
        set(value) {
            if (field == value) return
            field = value
            textPaint.color = field
            postInvalidate()
        }

    var cursorEnabled = false
        set(value) {
            if (field == value) return
            field = value
            postInvalidate()
        }

    var cursorWidth = 0
        set(value) {
            if (field == value) return
            field = value
            postInvalidate()
        }

    var cursorHeight = 0
        set(value) {
            if (field == value) return
            field = value
            postInvalidate()
        }

    var cursorColor = Color.BLACK
        set(value) {
            if (field == value) return
            field = value
            postInvalidate()
        }

    var cursorBlink = true
        set(value) {
            if (field == value) return
            field = value

            if (shouldBlink()) {
                blink.makeBlink()
            }
        }

    /**
     * cursor blink interval in milliseconds
     */
    var cursorBlinkInterval = BLINK
        set(value) {
            if (field == value) return
            field = value

            removeCallbacks(blink)
            postDelayed(blink, field.toLong())

            postInvalidate()
        }

    val isFullFilled
        get() = sb.length == capacity

    private var gridWidth = 0
    private var gridHeight = 0

    var listener: Listener? = null

    var DRAW_AUXILIARY_LINE = false
    var AUXILIARY_LINE_COLOR = Color.BLUE

    private val imm by lazy {
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private val cm by lazy {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private var _onClickListener: OnClickListener? = null

    init {
        @SuppressLint("CustomViewStyleable")
        val t = context.obtainStyledAttributes(attrs, R.styleable.DaemonVcVerificationCodeView)

        capacity = t.getInteger(R.styleable.DaemonVcVerificationCodeView_daemon_vc_capacity, 4)
        gridBackground =
            t.getDrawable(R.styleable.DaemonVcVerificationCodeView_daemon_vc_gridBackground)
        gridDividerSize =
            t.getDimension(R.styleable.DaemonVcVerificationCodeView_daemon_vc_gridDividerSize, 0f)
                .toInt()
        textSize = t.getDimension(
            R.styleable.DaemonVcVerificationCodeView_daemon_vc_textSize,
            context.sp2px(14f)
        )
        textColor =
            t.getColor(R.styleable.DaemonVcVerificationCodeView_daemon_vc_textColor, Color.BLACK)

        // cursor
        cursorEnabled =
            t.getBoolean(R.styleable.DaemonVcVerificationCodeView_daemon_vc_cursorEnabled, false)
        cursorWidth =
            t.getDimension(R.styleable.DaemonVcVerificationCodeView_daemon_vc_cursorWidth, 0f)
                .toInt()
        cursorHeight =
            t.getDimension(R.styleable.DaemonVcVerificationCodeView_daemon_vc_cursorHeight, 0f)
                .toInt()
        cursorColor =
            t.getColor(R.styleable.DaemonVcVerificationCodeView_daemon_vc_cursorColor, Color.BLACK)
        cursorBlink =
            t.getBoolean(R.styleable.DaemonVcVerificationCodeView_daemon_vc_cursorBlink, true)
        cursorBlinkInterval = t.getInteger(
            R.styleable.DaemonVcVerificationCodeView_daemon_vc_cursorBlinkInterval,
            BLINK
        )

        t.recycle()

        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
        isLongClickable = true

        textPaint.apply {
            isAntiAlias = true
            color = textColor
            textSize = this@VerificationCodeView.textSize
            textAlign = Paint.Align.CENTER
        }

        cursorPaint.apply {
            isAntiAlias = true
            color = cursorColor
        }

        auxiliaryPaint.apply {
            isAntiAlias = true
        }

        super.setOnClickListener {
            requestFocus()
            requestFocusFromTouch()
            showSoftKeyboard()
            _onClickListener?.onClick(this)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        _onClickListener = l
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

        val top = paddingTop
        val bottom = measuredHeight - paddingBottom
        var start = paddingLeft

        // draw grids
        val gridCount = capacity
        gridBackground?.let {
            for (i in 0 until gridCount) {
                it.setBounds(start, top, start + gridWidth, bottom)
                it.draw(canvas)
                start += gridWidth + gridDividerSize + 1
            }
        }

        val str = sb.toString()
        val len = str.length

        val fm = textPaint.fontMetrics
        val baseLine = top + (measuredHeight - fm.bottom - fm.top) / 2

        if (DRAW_AUXILIARY_LINE) {
            val t = baseLine + fm.top
            val a = baseLine + fm.ascent
            val d = baseLine + fm.descent
            val b = baseLine + fm.bottom
            auxiliaryPaint.color = AUXILIARY_LINE_COLOR
            canvas.drawLine(0f, t, measuredWidth.toFloat(), t, auxiliaryPaint)
            canvas.drawLine(0f, a, measuredWidth.toFloat(), a, auxiliaryPaint)
            canvas.drawLine(0f, baseLine, measuredWidth.toFloat(), baseLine, auxiliaryPaint)
            canvas.drawLine(0f, d, measuredWidth.toFloat(), d, auxiliaryPaint)
            canvas.drawLine(0f, b, measuredWidth.toFloat(), b, auxiliaryPaint)
        }

        start = paddingLeft + gridWidth / 2
        for (i in 0 until len) {
            canvas.drawText(
                str,
                i,
                i + 1,
                start.toFloat(),
                baseLine,
                textPaint
            )

            start += gridWidth + gridDividerSize + 1
        }

        if (cursorEnabled && cursorWidth > 0 && cursorHeight > 0 && len < capacity) {
            if (!cursorBlink || blink.show()) {

                var cursorL =
                    paddingLeft + len * (gridWidth + gridDividerSize) + gridWidth / 2 - cursorWidth / 2
                cursorL = between(0, measuredWidth, cursorL)

                var cursorT = paddingTop + gridHeight / 2 - cursorHeight / 2
                cursorT = between(0, measuredHeight, cursorT)

                val cursorR = between(cursorL, measuredWidth, cursorL + cursorWidth)
                val cursorB = between(cursorT, measuredHeight, cursorT + cursorHeight)
                canvas.drawRect(
                    cursorL.toFloat(),
                    cursorT.toFloat(),
                    cursorR.toFloat(),
                    cursorB.toFloat(),
                    cursorPaint
                )
            }
        }
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        focusChanged()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        focusChanged()
    }

    private fun focusChanged() {
        if (hasFocus() && hasWindowFocus()) {
            showSoftKeyboard()
            blink.unCancel()
        } else {
            hideSoftKeyboard()
            blink.cancel()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        blink.unCancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        blink.cancel()
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        when (screenState) {
            SCREEN_STATE_ON -> blink.unCancel()
            SCREEN_STATE_OFF -> blink.cancel()
        }
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        event ?: return super.onTouchEvent(event)
//
//        requestFocus()
//        requestFocusFromTouch()
//
//        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
//            showSoftKeyboard()
//        }
//        return false
//    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs ?: return super.onCreateInputConnection(outAttrs)

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
                    listener?.onChanged(this, sb.toString(), isFullFilled)
                    invalidate()
                }
            }
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                if (sb.length < capacity) {
                    sb.append(keyCode - KeyEvent.KEYCODE_0)
                    listener?.onChanged(this, sb.toString(), isFullFilled)
                    invalidate()
                }
            }
            KeyEvent.KEYCODE_ENTER -> {
                hideSoftKeyboard()
            }
            else -> {
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private val onMenuItemClickListener = MenuItem.OnMenuItemClickListener { menuItem ->
        if (menuItem == null || menuItem.itemId != android.R.id.paste) return@OnMenuItemClickListener false
        val clip = cm.primaryClip ?: return@OnMenuItemClickListener true
        if (clip.itemCount == 0) return@OnMenuItemClickListener true
        val item = clip.getItemAt(0)
        val c = item.coerceToText(context) ?: return@OnMenuItemClickListener true
        sb.clear()
        for (i in c) {
            if (i in '0'..'9') sb.append(i)
            if (isFullFilled) break
        }
        listener?.onChanged(this, sb.toString(), isFullFilled)
        invalidate()

        return@OnMenuItemClickListener true
    }


    override fun onCreateContextMenu(menu: ContextMenu) {
        super.onCreateContextMenu(menu)
        menu.add(Menu.NONE, android.R.id.paste, 0, android.R.string.paste)
            .setAlphabeticShortcut('v')
            .setOnMenuItemClickListener(onMenuItemClickListener)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())

        savedState.str = sb.toString()

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)

            sb.clear()
            sb.append(state.str)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    interface Listener {

        fun onChanged(view: VerificationCodeView, content: String, isFullFilled: Boolean)

    }

    private fun shouldBlink() = cursorEnabled && cursorBlink

    private inner class Blink : Runnable {
        private var showCursorStart = 0L
        private var cancelled = false

        fun show() =
            (SystemClock.uptimeMillis() - showCursorStart) % (cursorBlinkInterval * 2) < cursorBlinkInterval

        fun makeBlink() {
            showCursorStart = SystemClock.uptimeMillis()
            this@VerificationCodeView.removeCallbacks(this)
            this@VerificationCodeView.postDelayed(this, cursorBlinkInterval.toLong())
        }

        override fun run() {
            if (cancelled) {
                return
            }

            this@VerificationCodeView.removeCallbacks(this)
            if (shouldBlink()) {
                this@VerificationCodeView.invalidate() // TODO optimize
                this@VerificationCodeView.postDelayed(this, BLINK.toLong())
            }
        }

        fun cancel() {
            if (!cancelled) {
                this@VerificationCodeView.removeCallbacks(this)
                this@VerificationCodeView.invalidate()
                cancelled = true
            }
        }

        fun unCancel() {
            cancelled = false
            makeBlink()
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun showSoftKeyboard() {
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun hideSoftKeyboard() {
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}