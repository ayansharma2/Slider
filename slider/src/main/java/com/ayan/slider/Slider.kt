package com.ayan.slider

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.Integer.min
import kotlin.math.max
import kotlin.math.roundToInt

class SlideToPay(context: Context, attrs: AttributeSet) : View(context, attrs) {
    companion object {
        private val dp = Resources.getSystem().displayMetrics.density
        private const val sp = TypedValue.COMPLEX_UNIT_SP
    }
    private var mWidth = 100 * dp.toInt()
    private var mHeight = 100 * dp.toInt()
    var frontRectangleColor: Int
    var sliderColor: Int
    var successCallback: SuccessCallback? = null
    var slidingRectCurrentX = 0
    var sliderDrawable: Drawable =
        ContextCompat.getDrawable(context, R.drawable.ic_arrow_foraward)!!
    var frontTextColor: Int
    var backTextColor: Int
    var backRectangleColor: Int
    var frontRectangleStrokeColor: Int
    var backRectangleStrokeColor: Int
    private val paint = Paint()
    var strokeWidth: Float = 0.0f
    var title: String


    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.SlideToPay, 0, 0
        ).apply {
            frontRectangleColor =
                intToHexColorParser(getColor(R.styleable.SlideToPay_frontRectangleColor, -65536))
            sliderColor =
                getColor(R.styleable.SlideToPay_sliderColor, Int.MAX_VALUE)
            getDrawable(R.styleable.SlideToPay_sliderDrawable)?.let {
                sliderDrawable = it
            }
            title = getString(R.styleable.SlideToPay_title).toString()
            frontTextColor =
                intToHexColorParser(getColor(R.styleable.SlideToPay_frontTextColor, -16776961))
            backTextColor =
                intToHexColorParser(getColor(R.styleable.SlideToPay_backTextColor, -16777216))
            backRectangleColor =
                intToHexColorParser(getColor(R.styleable.SlideToPay_backRectangleColor, -16711936))
            frontRectangleStrokeColor = intToHexColorParser(
                getColor(
                    R.styleable.SlideToPay_frontRectangleStrokeColor,
                    -65536
                )
            )
            backRectangleStrokeColor = intToHexColorParser(
                getColor(
                    R.styleable.SlideToPay_backRectangleStrokeColor,
                    -16711936
                )
            )
            strokeWidth = getDimension(R.styleable.SlideToPay_rectangleStrokeWidth, 0.0F)
        }

    }

    private fun intToHexColorParser(colorInt: Int): Int {
        return Color.parseColor("#" + Integer.toHexString(colorInt).substring(2))
    }

    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)

        canvas?.let {
            //Drawing BackRectangle
            paint.color = backRectangleColor
            it.drawRect(0F, 0F, mWidth.toFloat(), mHeight.toFloat(), paint)
            drawBackText(it)

            if (strokeWidth != 0.0F) {
                paint.style = Paint.Style.STROKE;
                paint.strokeWidth = strokeWidth
                paint.color = backRectangleStrokeColor
                it.drawRect(0F, 0F, mWidth.toFloat(), mHeight.toFloat(), paint)
            }


            paint.style = Paint.Style.FILL;
            paint.strokeWidth = 0 * dp

            //Draw Front Rectangle
            paint.color =
                frontRectangleColor
            it.drawRect(
                slidingRectCurrentX.toFloat(),
                0F,
                mWidth.toFloat(),
                mHeight.toFloat(),
                paint
            )

            if (strokeWidth != 0.0F) {
                paint.style = Paint.Style.STROKE;
                paint.color = frontRectangleStrokeColor
                paint.strokeWidth = strokeWidth
                it.drawRect(
                    slidingRectCurrentX.toFloat(),
                    0F,
                    mWidth.toFloat(),
                    mHeight.toFloat(),
                    paint
                )
            }


            paint.style = Paint.Style.FILL;
            paint.strokeWidth = 0 * dp

            drawFrontText(it)
            createSlidingRectangle(it)
        }

    }

    private fun drawFrontText(canvas: Canvas) {
        val bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        val newCanvas = Canvas(bitmap)
        val r = Rect()
        paint.color = Color.BLUE
        canvas.getClipBounds(r)
        val cHeight: Int = r.height()
        val cWidth: Int = r.width()
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.getTextBounds(title, 0, title.length, r)
        paint.textSize = 18 * sp.toFloat()
        val x = cWidth / 2f - r.width() / 2f + mHeight / 3
        val y = cHeight / 2f + r.height() / 2f
        newCanvas.drawText(title, x, y, paint);
        val newBitmap = Bitmap.createBitmap(
            bitmap,
            min(max(slidingRectCurrentX, 0), mWidth - 1),
            0,
            max(1, min(mWidth - slidingRectCurrentX, mWidth)),
            mHeight
        )
        canvas.drawBitmap(newBitmap, slidingRectCurrentX.toFloat(), 0F, paint)
    }

    private fun drawBackText(canvas: Canvas) {

        val r = Rect()
        paint.color = Color.BLACK
        canvas.getClipBounds(r)
        val cHeight: Int = r.height()
        val cWidth: Int = r.width()
        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(title, 0, title.length, r)
        paint.textSize = 18 * sp.toFloat()
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        val x = cWidth / 2f - r.width() / 2f
        val y = cHeight / 2f + r.height() / 2f
        canvas.drawText(title, x, y, paint);
    }

    private fun createSlidingRectangle(canvas: Canvas) {

        if (sliderColor != Int.MAX_VALUE) {
            paint.color = intToHexColorParser(sliderColor)
            canvas?.drawRect(
                slidingRectCurrentX.toFloat(), 0F,
                (mHeight + slidingRectCurrentX).toFloat(), mHeight.toFloat(), paint
            )
            return
        }
        sliderDrawable.setBounds(
            slidingRectCurrentX, 0, slidingRectCurrentX + mHeight, mHeight
        )
        sliderDrawable.draw(canvas)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        mWidth = when (wMode) {
            MeasureSpec.EXACTLY -> {

                MeasureSpec.getSize(widthMeasureSpec)

            }
            MeasureSpec.AT_MOST -> {
                400 * dp.toInt()
            }
            else -> 400 * dp.toInt()
        }



        mHeight = when (hMode) {
            MeasureSpec.EXACTLY -> {
                MeasureSpec.getSize(heightMeasureSpec)
            }
            MeasureSpec.AT_MOST -> {
                100 * dp.toInt()
            }
            else -> 100 * dp.toInt()
        }


        setMeasuredDimension(mWidth, mHeight)
        invalidate()
    }

    private val myListener = object : GestureDetector.OnGestureListener {
        override fun onDown(p0: MotionEvent?): Boolean = true

        override fun onShowPress(p0: MotionEvent?) {}

        override fun onSingleTapUp(p0: MotionEvent?): Boolean = true


        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            slidingRectCurrentX += (p3 - p2).roundToInt()
            invalidate()
            return true
        }

        override fun onLongPress(p0: MotionEvent?) {}

        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean =
            true
    }

    private val detector: GestureDetector = GestureDetector(context, myListener)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == ACTION_UP) {
            var valueAnimator: ValueAnimator = if (slidingRectCurrentX >= mWidth / 2) {
                successCallback?.let {
                    it.onSuccess()
                }
                ValueAnimator.ofInt(slidingRectCurrentX, mWidth - mHeight)

            } else {
                ValueAnimator.ofInt(slidingRectCurrentX, 0)

            }
            valueAnimator.duration = 200
            valueAnimator.addUpdateListener {
                slidingRectCurrentX = it.animatedValue as Int
                postInvalidate()
            }
            valueAnimator.start()
        }

        detector.onTouchEvent(event)
        return true
    }

}

public interface SuccessCallback {
    fun onSuccess()
}