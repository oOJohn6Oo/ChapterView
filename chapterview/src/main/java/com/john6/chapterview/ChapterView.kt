package com.john6.chapterview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.os.Build
import android.text.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import kotlin.math.max

/**
 * 可定制标题/内容/着重号的内容View
 */
class ChapterView(context: Context, attr: AttributeSet?) : View(context, attr) {
    constructor(context: Context) : this(context, null)

    // 基本设置
    private var titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var contentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var bulletPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 标题区域
     */
    // 是否需要标题
    private var wantTitle: Boolean

    private var title: CharSequence
    private var titleTextSize: Float

    @ColorInt
    private var titleTextColor: Int

    // 标题与 layout-start 边界的距离
    private var titleToStart: Float

    // 标题与 layout-top 边界的距离(注意，是 layout 的 top )
    private var titleToTop: Float

    // 标题与 layout-end 边界的距离
    private var titleToEnd: Float

    // 标题与 layout-bottom 边界的距离(注意，是 layout 的 top )
    private var titleToBottom: Float

    /**
     * 内容区域
     */
    private var wantContent = true
    private var contentTextSize: Float
    private var contentSingleLine: Boolean
    private var contentLineSpacing: Float

    @ColorInt
    private var contentTextColor: Int

    // 标题与 layout-start 边界的距离
    private var contentToStart: Float

    // 标题与 layout-top 边界的距离(注意，是 layout 的 top )
    private var contentToTop: Float

    // 标题与 layout-end 边边界的距离
    private var contentToEnd: Float

    // 标题与 layout-bottom 边边界的距离
    private var contentToBottom: Float

    /**
     * Bullet设置
     */
    private var wantLine = true
    private var wantBullet = true

    // 标题前圆点的半径
    private var bulletRadius: Float

    // 标题前圆点与文字的距离
    private var bulletGap: Float
    private var bulletStrokeWidth: Float

    /**
     * 0 首个实心
     * 1 全为实心
     * 2 全为空心
     */
    @IntRange(from = 0, to = 2)
    private var bulletStyle: Int

    @ColorInt
    private var bulletColor: Int

    // 线
    private var lineWidth: Float

    @ColorInt
    private var lineColor: Int

    // 数据
    private var titleLayout: StaticLayout? = null
    private var contentLayoutList: MutableList<StaticLayout> = mutableListOf()

    private var contentList: List<CharSequence> = listOf()

    init {
        val ta = context.obtainStyledAttributes(attr, R.styleable.ChapterView)

        /**
         * 标题区域
         */
        wantTitle = ta.getBoolean(R.styleable.ChapterView_want_chapter_title, true)
        title = ta.getString(R.styleable.ChapterView_chapter_title_text) ?: ""
        titleTextSize = ta.getDimension(R.styleable.ChapterView_chapter_title_text_size, sp2px(16))
        titleTextColor = ta.getColor(R.styleable.ChapterView_chapter_title_text_color, Color.DKGRAY)
        titleToStart =
                ta.getDimension(R.styleable.ChapterView_chapter_title_to_start, dp2px(15))
        titleToTop =
                ta.getDimension(R.styleable.ChapterView_chapter_title_to_top, dp2px(15))
        titleToEnd =
                ta.getDimension(R.styleable.ChapterView_chapter_title_to_end, dp2px(15))
        titleToBottom =
                ta.getDimension(R.styleable.ChapterView_chapter_title_to_bottom, dp2px(15))

        /**
         * 内容区域
         */
        wantContent = ta.getBoolean(R.styleable.ChapterView_want_chapter_content, true)
        contentList = ta.getTextArray(R.styleable.ChapterView_chapter_content_text).toList()
        contentSingleLine = ta.getBoolean(R.styleable.ChapterView_chapter_content_single_line, true)
        contentLineSpacing = ta.getDimension(
                R.styleable.ChapterView_chapter_content_line_spacing,
                dp2px(10)
        )
        contentTextColor = ta.getColor(R.styleable.ChapterView_chapter_content_text_color, Color.DKGRAY)
        contentTextSize =
                ta.getDimension(R.styleable.ChapterView_chapter_content_text_size, sp2px(14))
        contentToStart =
                ta.getDimension(R.styleable.ChapterView_chapter_content_to_start, dp2px(31))
        contentToTop =
                ta.getDimension(R.styleable.ChapterView_chapter_content_to_top, dp2px(50))
        contentToEnd =
                ta.getDimension(R.styleable.ChapterView_chapter_content_to_End, dp2px(15))
        contentToBottom =
                ta.getDimension(R.styleable.ChapterView_chapter_content_to_bottom, dp2px(20))


        /**
         * bullet 相关
         */
        wantBullet = ta.getBoolean(R.styleable.ChapterView_want_chapter_bullet, true)
        bulletRadius = ta.getDimension(R.styleable.ChapterView_chapter_bullet_radius, dp2px(3))
        bulletColor = ta.getColor(R.styleable.ChapterView_chapter_bullet_color, Color.DKGRAY)
        bulletGap = ta.getDimension(R.styleable.ChapterView_chapter_bullet_gap, dp2px(10))
        bulletStyle = ta.getInt(R.styleable.ChapterView_chapter_bullet_style, 0)
        bulletStrokeWidth = ta.getDimension(R.styleable.ChapterView_chapter_bullet_stroke_width, dp2px(1))

        wantLine = ta.getBoolean(R.styleable.ChapterView_want_chapter_line, true)
        lineColor = ta.getColor(R.styleable.ChapterView_chapter_line_color, Color.DKGRAY)
        lineWidth = ta.getDimension(R.styleable.ChapterView_chapter_line_width, dp2px(1))
        ta.recycle()

        titlePaint.apply {
            isFakeBoldText = true
            textSize = titleTextSize
            color = titleTextColor
        }
        contentPaint.apply {
            textSize = contentTextSize
            color = contentTextColor
        }
        bulletPaint.apply {
            color = bulletColor
        }
        linePaint.apply {
            color = lineColor
            strokeWidth = lineWidth
            style = STROKE
        }
    }

    fun setTitle(newTitle: CharSequence) {
        title = newTitle
        invalidate()
    }

    fun setContent(newContent: List<CharSequence>) {
        contentList = newContent
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd

        // 需要标题
        if (wantTitle)
            titleLayout = getStaticLayout(title, true, (availableWidth - titleToStart).toInt())

        contentLayoutList.clear()
        // 需要内容
        if (wantContent) {
            contentList.forEach { s ->
                contentLayoutList.add(
                        getStaticLayout(
                                s,
                                false,
                                (availableWidth - contentToStart - contentToEnd).toInt()
                        )
                )
            }
        }

        // 我想要的宽
        var desireWidth = paddingStart + paddingEnd + 0f
        // 我想要的高
        var desireHeight = paddingTop + paddingBottom + 0f
        when {
            // 什么都不想要
            !wantTitle and !wantContent -> {
                desireWidth = 0f
                desireHeight = 0f
            }
            // 要标题 and (不要列表 or 列表为空)
            wantTitle and (!wantContent or contentLayoutList.isEmpty()) -> {
                desireWidth += titleToStart + titleToEnd + (titleLayout?.width ?: 0)
                desireHeight += titleToTop + titleToBottom + (titleLayout?.height ?: 0)
            }
            // 不要标题 and 要列表
            !wantTitle and wantContent -> {
                desireWidth += contentToStart + contentToEnd
                desireHeight += contentToTop + contentToBottom
                if (contentLayoutList.isNotEmpty()) {
                    desireWidth += contentLayoutList.maxOf { it.width }
                    desireHeight += contentLayoutList.sumOf { it.height }
                    desireHeight += (contentLayoutList.size - 1) * contentLineSpacing
                }
            }
            // 都要
            else -> {
                desireWidth += max(
                        titleLayout?.let { it.width + titleToStart + titleToEnd } ?: 0f,
                        contentLayoutList.maxOf { it.width } + contentToStart + contentToEnd)
                desireHeight += contentToTop + contentToBottom + contentLayoutList.sumOf { it.height }
                desireHeight += (contentLayoutList.size - 1) * contentLineSpacing
            }
        }
        setMeasuredDimension(
                resolveSize(desireWidth.toInt(), widthMeasureSpec),
                resolveSize(desireHeight.toInt(), heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            if (wantTitle) {
                save()
                translate(paddingStart + titleToStart, paddingTop + titleToTop)
                titleLayout?.draw(this)
                restore()
            }
            if (wantContent) {
                save()
                translate(paddingStart + contentToStart, paddingTop + contentToTop)
                contentLayoutList.forEachIndexed { i, l ->
                    l.draw(this)
                    if (wantBullet) {

                        bulletPaint.style = if ((bulletStyle == 2) or ((i == 0) and (bulletStyle == 0))) FILL else STROKE
                        val halfLine = 0.5f * l.height / l.lineCount
                        drawCircle(0 - bulletGap - bulletRadius, halfLine, bulletRadius, bulletPaint)
                        if (wantLine && i != 0)
                            drawLine(0 - bulletGap - bulletRadius, halfLine - bulletRadius, 0 - bulletGap - bulletRadius, bulletRadius - contentLayoutList[i - 1].height - contentLineSpacing + halfLine, bulletPaint)
                    }
                    translate(0f, l.height + contentLineSpacing)
                }
                restore()
            }
        }
    }

    private fun dp2px(dp: Int): Float {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                context.resources.displayMetrics
        )
    }

    private fun sp2px(sp: Int): Float {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp.toFloat(),
                context.resources.displayMetrics
        )
    }

//    CharSequence source, int bufstart, int bufend,
//    TextPaint paint, int outerwidth,
//    Alignment align, TextDirectionHeuristic textDir,
//    float spacingmult, float spacingadd,
//    boolean includepad,
//    TextUtils.TruncateAt ellipsize, int ellipsizedWidth, int maxLines

    private fun getStaticLayout(
            text: CharSequence,
            isTitle: Boolean,
            maxWidth: Int
    ): StaticLayout {
        // 反射
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            StaticLayout::class.java.getConstructor(
                    CharSequence::class.java,
                    Int::class.java,
                    Int::class.java,
                    TextPaint::class.java,
                    Int::class.java,
                    Layout.Alignment::class.java,
                    TextDirectionHeuristic::class.java,
                    Float::class.java,
                    Float::class.java,
                    Boolean::class.java,
                    TextUtils.TruncateAt::class.java,
                    Int::class.java,
                    Int::class.java
            ).run {
                this.newInstance(
                        text,
                        0,
                        text.length,
                        if (isTitle) titlePaint else contentPaint,
                        maxWidth,
                        Layout.Alignment.ALIGN_NORMAL,
                        object : TextDirectionHeuristic {
                            override fun isRtl(array: CharArray?, start: Int, count: Int): Boolean =
                                    false

                            override fun isRtl(cs: CharSequence?, start: Int, count: Int): Boolean =
                                    false
                        },
                        1f,
                        0f,
                        true,
                        TextUtils.TruncateAt.END,
                        maxWidth,
                        if (isTitle or contentSingleLine) 1 else Int.MAX_VALUE
                )
            }
        else
            StaticLayout.Builder.obtain(
                    text,
                    0,
                    text.length,
                    if (isTitle) titlePaint else contentPaint,
                    maxWidth
            ).setMaxLines(if (isTitle or this.contentSingleLine) 1 else Int.MAX_VALUE)
                    .setEllipsize(TextUtils.TruncateAt.END).build()
    }
}






