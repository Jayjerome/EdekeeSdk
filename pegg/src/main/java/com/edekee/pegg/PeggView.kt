package com.edekee.pegg

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import java.text.NumberFormat


class PeggView : View {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs)
    }

    var typedArray: TypedArray? = null
    var paint: Paint? = null
    var textPaint: Paint? = null
    var pricePaint: Paint? = null
    var mImagePaint: Paint? = null
    var peggColor = 0
    var labelColor = 0
    var expandColor = 0
    var peggText: String? = null
    var radius = 500
    var textX = 0
    var textY = 0
    var leftTopX = 25
    var leftTopY = 25
    var rightBotX = 0
    var rightBotY = 0
    var rightBotXO = 0
    var rightBotYO = 0
    var trueWidth = 0
    var width1 = 0
    var trueHeight = 0
    var height1 = 0
    var expand = false
    var value = false
    var mWidth = 120
    var mHeight = 120
    var tagNameBounds = Rect()
    var tagPriceBounds = Rect()
    var tagTitleDistanceX = 0
    var totalPeggWidth = 0
    var tagName: String = " "
    var tagName2: String = " "
    var tagDescription: String = " "
    var tagPrice: String = "0"
    var tagId: String = ""
    var isShowBottomSheet : Boolean = false
    lateinit var view: View
    lateinit var fragmentManager: FragmentManager
    var showBottom: MutableLiveData<Boolean> = MutableLiveData()

    var height: Float = 0F
    var width: Float = 0F
    var top: Float = 0F
    var left: Float = 0F

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private lateinit var bitmap: Bitmap

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.PeggView, 0, 0)
            try {
                peggColor = typedArray!!.getColor(R.styleable.PeggView_peggColor, 0)
                labelColor = typedArray!!.getColor(R.styleable.PeggView_labelColor, 0)
                expandColor = typedArray!!.getColor(R.styleable.PeggView_expandColor, 0)
                peggText = typedArray!!.getString(R.styleable.PeggView_peggLabel)
            } finally {
                typedArray!!.recycle()
            }
        }

        paint = Paint()
        textPaint = Paint()
        pricePaint = Paint()
        mImagePaint = Paint()

        paint!!.style = Paint.Style.FILL
        paint!!.isAntiAlias = true

        pricePaint!!.style = Paint.Style.FILL
        pricePaint!!.textSize = 32f
        pricePaint!!.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        pricePaint!!.isAntiAlias = true
        pricePaint!!.textAlign = Paint.Align.CENTER
        pricePaint!!.color = Color.WHITE

        textPaint!!.style = Paint.Style.FILL
        textPaint!!.textSize = 30f
        textPaint!!.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        textPaint!!.isAntiAlias = true
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.color = Color.WHITE

//        val res: Resources = resources
//        bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_right)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val format: NumberFormat = NumberFormat.getInstance()
        format.maximumFractionDigits = 0

        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        pricePaint!!.getTextBounds(
            tagPrice,
            0,
            tagPrice.length,
            tagPriceBounds
        )

        textPaint!!.getTextBounds(tagName, 0, tagName.length, tagNameBounds)
        rightBotXO = 95
        rightBotYO = 95
        tagTitleDistanceX = (tagNameBounds.width() * 0.80 + tagPriceBounds.width() + 10).toInt()
        totalPeggWidth = tagNameBounds.width() + tagPriceBounds.width() + 100

        if (!expand) {
            // State one
            rightBotX = 95
            rightBotY = 95
            paint!!.color = peggColor
        }

        canvas.drawRoundRect(
            leftTopX.toFloat(),
            leftTopY.toFloat(),
            rightBotX.toFloat(),
            rightBotY.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint!!
        )

        canvas.drawText(
            tagPrice + "      ", textX.toFloat(), textY.toFloat(), pricePaint!!
        )

        canvas.drawText(tagName, tagTitleDistanceX.toFloat(), textY.toFloat(), textPaint!!)

//        val d = resources.getDrawable(R.drawable.ic_right, null)
//        d.setBounds(tagTitleDistanceX, textY, 100, 200)
//        d.draw(canvas)


//        canvas.drawBitmap(bitmap, 100f, 200f, textPaint)

    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(getmWidth(), getmHeight())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        value = super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (getRightBotX() == rightBotXO) {
                    setmWidth(totalPeggWidth + 150)
                    setmHeight(200)
                }
                expand()
                collapse()

                if (getRightBotX() == rightBotXO +
                    tagNameBounds.width() + tagPriceBounds.width() + 50 && isShowBottomSheet) {
                    showBottomSheet(
                        fragmentManager,
                        tagId,
                        tagName2,
                        tagDescription,
                        tagPrice.toInt()
                    )
                }
                return true
            }
        }
        return value
    }

    private fun showBottomSheet(
        fragmentManager: FragmentManager,
        tagId: String,
        tagName: String,
        tagDesc: String,
        tagPrice: Int
    ) {
//        val modalBottomSheet = PeggBottomSheet(tagId, tagName, tagDesc, tagPrice, view)
//        modalBottomSheet.show(fragmentManager, PeggBottomSheet.TAG)
    }

    private fun expand() {
        if (getRightBotX() == rightBotXO) {
            if (getRightBotX() != rightBotXO +
                tagNameBounds.width() + tagPriceBounds.width() + 50
            ) {

                val textAnimationX = ValueAnimator.ofInt(25, 65)
                textAnimationX.addUpdateListener { animatorX: ValueAnimator ->
                    textX = (animatorX.animatedValue as Int
                            + tagPriceBounds.width() * 0.5f).toInt()
                }

                val textAnimationY = ValueAnimator.ofInt(25, 65)
                textAnimationY.addUpdateListener { animatorY: ValueAnimator ->
                    textY = (animatorY.animatedValue as Int
                            + tagNameBounds.height() * 0.7f).toInt()
                }

                val colorAnimation = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    peggColor, expandColor
                )

                colorAnimation.addUpdateListener { animator22: ValueAnimator ->
                    paint!!.color = (animator22.animatedValue as Int)
                }

                val animator = ValueAnimator.ofInt(
                    getRightBotX(), getRightBotX()
                            + tagNameBounds.width() + tagPriceBounds.width() + 50
                )

                animator.addUpdateListener { animation: ValueAnimator ->
                    trueWidth = animation.animatedValue as Int
                    setRightBotX(trueWidth)
                }

                val animator1 =
                    ValueAnimator.ofInt(getRightBotY(), getRightBotY() + tagNameBounds.height())
                animator1.addUpdateListener { animation: ValueAnimator ->
                    trueHeight = animation.animatedValue as Int
                    setRightBotY(trueHeight)
                }

                val set = AnimatorSet()
                set.playTogether(
                    textAnimationX,
                    textAnimationY,
                    animator,
                    animator1,
                    colorAnimation
                )

                set.duration = 250
                set.start()
            }
        }
    }

    private fun collapse() {

        Log.d(
            "pegg", getRightBotX().toString() + " - " + (rightBotXO +
                    tagNameBounds.width() + tagPriceBounds.width())
        )

        if (getRightBotX() == rightBotXO +
            tagNameBounds.width() + tagPriceBounds.width() + 50
        ) {
            val colorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                expandColor, peggColor
            )

            colorAnimation.addUpdateListener { animator22: ValueAnimator ->
                paint!!.color = (animator22.animatedValue as Int)
            }

            val textAnimationX = ValueAnimator.ofInt(65, 25)
            textAnimationX.addUpdateListener { animatorX: ValueAnimator ->
                textX = (animatorX.animatedValue as Int * -1.5f).toInt()
            }

            val textAnimationY = ValueAnimator.ofInt(65, 25)
            textAnimationY.addUpdateListener { animatorY: ValueAnimator ->
                textY = (animatorY.animatedValue as Int * -0.5f).toInt()
            }

            val animator = ValueAnimator.ofInt(getRightBotX(), rightBotXO)
            animator.addUpdateListener { animation: ValueAnimator ->
                width1 = animation.animatedValue as Int
                setRightBotX(width1)
                if (width1 == 95) {
                    setmWidth(120)
                    setmHeight(120)
                }
            }

            val animator1 = ValueAnimator.ofInt(getRightBotY(), rightBotYO)
            animator1.addUpdateListener { animation: ValueAnimator ->
                height1 = animation.animatedValue as Int
                setRightBotY(height1)
            }

            val set = AnimatorSet()
            set.playTogether(textAnimationX, textAnimationY, animator, animator1, colorAnimation)
            set.duration = 250
            set.start()
        }
    }

    @JvmName("setRightBotX1")
    fun setRightBotX(rightBotX: Int) {
        this.rightBotX = rightBotX
        expand = true
        invalidate()
        requestLayout()
    }

    @JvmName("getRightBotX1")
    fun getRightBotX(): Int {
        return rightBotX
    }

    @JvmName("getRightBotY1")
    fun getRightBotY(): Int {
        return rightBotY
    }

    @JvmName("setRightBotY1")
    fun setRightBotY(rightBotY: Int) {
        this.rightBotY = rightBotY
        expand = true
        invalidate()
        requestLayout()
    }

    private fun getmWidth(): Int {
        return mWidth
    }

    private fun setmWidth(mWidth: Int) {
        this.mWidth = mWidth
    }

    private fun getmHeight(): Int {
        return mHeight
    }

    private fun setmHeight(mHeight: Int) {
        this.mHeight = mHeight
    }
}
