package com.example.gardenwater

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet


class CustomDropView(context: Context, attributes: AttributeSet): androidx.appcompat.widget.AppCompatButton(context, attributes) {

    companion object {
        private const val DEFAULT_CIRCLE_COLOR = Color.YELLOW
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
        private const val DEFAULT_BORDER_WIDTH = 4.0f

        const val FOCUSED = 0L
        const val PRESSED = 1L
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var circleColor = DEFAULT_CIRCLE_COLOR
    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH
    private val mouthPath = Path()
    private var size = 320

    public var focusedState = FOCUSED
        set(state) {
            field = state
            invalidate()
        }

    init {
        paint.isAntiAlias = true
        setupAttributes(attributes)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomDropView,
                0, 0)

        focusedState = typedArray.getInt(R.styleable.CustomDropView_state, FOCUSED.toInt()).toLong()
        circleColor = typedArray.getColor(R.styleable.CustomDropView_CircleColor, DEFAULT_BORDER_COLOR)
        borderColor = typedArray.getColor(R.styleable.CustomDropView_BorderColor,
                DEFAULT_BORDER_COLOR)

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCirle(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        //для отступов внутри самой вьюшки, будет обращена именно к тексту
        //setPadding((measuredWidth - size / 2f).toInt(), (size / 2f).toInt(), 0, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
        size = Math.min(measuredWidth, measuredHeight) / 3
        setMeasuredDimension(measuredWidth, measuredHeight)
        setBackgroundResource(R.drawable.icon_water)
    }

    fun drawCirle(canvas: Canvas?){
        paint.style = Paint.Style.FILL
        if (focusedState == FOCUSED){
            paint.color = Color.RED
        }else{
            paint.color = Color.BLUE
        }
        val radius = size / 2f
        canvas?.drawCircle( measuredWidth - size / 2f, size / 2f, radius, paint)
        paint.color = borderColor
        paint.style = Paint.Style.STROKE

        paint.strokeWidth = borderWidth
        canvas?.drawCircle(measuredWidth - size / 2f, size / 2f, radius, paint)

        //для решения проблемы с тем, что нельзя нарисовать текст на сам круг, тк сначала как я понял
        //пишется текст, а потом рисуется круг, я пытался решить проблему путем большого кольца вокруг цифры,
        //вот примеры, как я пытался подобрать значения. Но к этому моменту задача стала больше, тк надо еще связываться
        //с размером текста и тд, решил оставить код, как я пытался это сделать

//        paint.strokeWidth = borderWidth+50f
//        canvas?.drawCircle(measuredWidth - size / 2f, size / 2f, radius - borderWidth / 2f, paint)
    }
}