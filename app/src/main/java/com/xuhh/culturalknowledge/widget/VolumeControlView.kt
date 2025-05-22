package com.xuhh.culturalknowledge.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.xuhh.culturalknowledge.R
import kotlin.math.max
import kotlin.math.min

/**
 * 音量调节控件
 * 包含音量调节条和加减按钮的容器
 */
class VolumeControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    /** 音量调节条 */
    private val volumeSeekBar = VolumeSeekBar(context)
    /** 音量变化监听器 */
    private var onVolumeChangeListener: ((Int) -> Unit)? = null

    init {
        // 设置背景色和圆角
        background = GradientDrawable().apply {
            setColor(ContextCompat.getColor(context, R.color.volume_container_background))
            cornerRadius = 160f
        }
        
        // 添加音量调节条
        addView(volumeSeekBar, LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            // 设置内边距
            setMargins(20, 20, 20, 20)
        })

        // 设置音量变化监听
        volumeSeekBar.setOnProgressChangeListener { progress ->
            onVolumeChangeListener?.invoke(progress)
        }
    }

    /**
     * 设置音量变化监听器
     * @param listener 音量变化回调函数
     */
    fun setOnVolumeChangeListener(listener: (Int) -> Unit) {
        onVolumeChangeListener = listener
    }

    /**
     * 设置音量进度
     * @param progress 进度值（0-100）
     */
    fun setProgress(progress: Int) {
        volumeSeekBar.setProgress(progress)
    }

    /**
     * 音量调节条
     * 使用Canvas绘制的垂直进度条
     */
    private inner class VolumeSeekBar(context: Context) : View(context) {
        /** 进度条画笔 */
        private val progressPaint = Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.volume_progress)
            style = Paint.Style.FILL
        }

        /** 背景画笔 */
        private val backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.volume_progress_background)
            style = Paint.Style.FILL
        }

        /** 文字画笔 */
        private val textPaint = Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.white)
            textSize = 80f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        /** 进度条矩形 */
        private val progressRect = RectF()
        /** 背景矩形 */
        private val backgroundRect = RectF()

        /** 当前进度（0-100） */
        private var progress = 50
        /** 圆角半径 */
        private val cornerRadius = 160f
        /** 按钮高度 */
        private val buttonHeight = 120f
        /** 按钮内边距 */
        private val buttonPadding = 30f
        /** 进度条宽度 */
        private val progressWidth = 48f

        /** 进度变化监听器 */
        private var onProgressChangeListener: ((Int) -> Unit)? = null

        /**
         * 设置进度变化监听器
         * @param listener 进度变化回调函数
         */
        fun setOnProgressChangeListener(listener: (Int) -> Unit) {
            onProgressChangeListener = listener
        }

        /**
         * 设置进度
         * @param newProgress 新的进度值（0-100）
         */
        fun setProgress(newProgress: Int) {
            progress = newProgress.coerceIn(0, 100)
            invalidate()
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val desiredWidth = 120
            val desiredHeight = 500

            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)

            val width = when (widthMode) {
                MeasureSpec.EXACTLY -> widthSize
                MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
                else -> desiredWidth
            }

            val height = when (heightMode) {
                MeasureSpec.EXACTLY -> heightSize
                MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
                else -> desiredHeight
            }

            setMeasuredDimension(width, height)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // 绘制进度条背景（未填充部分）
            backgroundRect.set(
                (width - progressWidth) / 2,
                buttonHeight,
                (width + progressWidth) / 2,
                height - buttonHeight
            )
            canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint)

            // 绘制进度条（已填充部分）
            val progressHeight = (height - 2 * buttonHeight) * progress / 100f
            progressRect.set(
                (width - progressWidth) / 2,
                height - buttonHeight - progressHeight,
                (width + progressWidth) / 2,
                height - buttonHeight
            )
            canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint)

            // 绘制加号按钮
            canvas.drawText("+", width / 2f, buttonHeight - buttonPadding, textPaint)

            // 绘制减号按钮
            canvas.drawText("-", width / 2f, height - buttonPadding, textPaint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val y = event.y
                    when {
                        // 点击加号按钮
                        y <= buttonHeight -> {
                            progress = min(100, progress + 5)
                            onProgressChangeListener?.invoke(progress)
                            invalidate()
                        }
                        // 点击减号按钮
                        y >= height - buttonHeight -> {
                            progress = max(0, progress - 5)
                            onProgressChangeListener?.invoke(progress)
                            invalidate()
                        }
                        // 点击进度条区域
                        y > buttonHeight && y < height - buttonHeight -> {
                            val availableHeight = height - 2 * buttonHeight
                            val relativeY = y - buttonHeight
                            progress = ((1 - relativeY / availableHeight) * 100).toInt()
                            progress = max(0, min(100, progress))
                            onProgressChangeListener?.invoke(progress)
                            invalidate()
                        }
                    }
                    return true
                }
            }
            return super.onTouchEvent(event)
        }
    }
} 