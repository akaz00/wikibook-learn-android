package wikibook.learnandroid.pomodoro

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

// (1)
class ProgressView : View {
    // (2)
    var srcResId: Int = 0
    val bgColor: Int
    val showBackgroundImage: Boolean

    // (3)
    var progress: Double = 0.0
        set(value) {
            field = value
            if (value >= 100.0) field = 100.0
            if (value <= 0.0) field = 0.0

            invalidate()
        }

    // (4)
    lateinit var backgroundBitmap: Bitmap
    lateinit var srcRect: Rect
    lateinit var destRect: Rect

    // (5)
    var backgroundPaint = Paint().apply {
        isAntiAlias = true
        alpha = 127
    }

    // (6)
    var backgroundCirclePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    var progressCirclePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // (7)
        var array = context.obtainStyledAttributes(attrs, R.styleable.ProgressViewAttrs)

        // (8)
        bgColor = array.getColor(R.styleable.ProgressViewAttrs_progressBackgroundColor, 0)

        // (9)
        showBackgroundImage = array.getBoolean(R.styleable.ProgressViewAttrs_showBackgroundImage, false)

        // (10)
        srcResId = array.getResourceId(R.styleable.ProgressViewAttrs_progressBackgroundImage, 0)

        // (11)
        array.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(showBackgroundImage) {
            // (12)
            val bitmap = BitmapFactory.decodeResource(context.resources, srcResId)

            // (13)
            srcRect = Rect(0, 0, bitmap.width, bitmap.height)
            destRect = Rect(0, 0, w, h)
            backgroundBitmap = bitmap
        }

        // (14)
        backgroundCirclePaint.color = Color.argb(25, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor))
        progressCirclePaint.color = Color.argb(127, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor))
    }

    // (15)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            // (16)
            if (showBackgroundImage) {
                drawBitmap(backgroundBitmap, srcRect, destRect, backgroundPaint)
            }

            // (17)
            val x = canvas.width / 2
            val y = canvas.height / 2
            val radius = canvas.width / 2
            drawCircle(x.toFloat(), y.toFloat(), radius.toFloat(), backgroundCirclePaint)
            drawCircle(x.toFloat(), y.toFloat(), (radius * (progress / 100)).toFloat(), progressCirclePaint)
        }
    }
}
