
package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.utils.Utils;

/**
 * This class encapsulates everything both Axis, Legend and LimitLines have in common.
 *
 * @author Philipp Jahoda
 */
public abstract class ComponentBase {

    /**
     * 指示此轴图例是否启用的标志
     */
    protected boolean mEnabled = true;

    /**
     * 此组件在 x 轴上的偏移量（以像素为单位）
     */
    protected float mXOffset = 10f;

    /**
     * 此组件在 Y 轴上的偏移量（以像素为单位）
     */
    protected float mYOffset = 5f;

    /**
     * 用于标签的字体
     */
    protected Typeface mTypeface = null;

    /**
     * 标签的文本大小
     */
    protected float mTextSize = Utils.convertDpToPixel(10f);

    /**
     * 用于标签的文本颜色
     */
    protected int mTextColor = Color.BLACK;


    public ComponentBase() {

    }

    /**
     * 返回用于绘制轴或图例标签的 x 轴偏移量。此偏移量应用于标签之前和之后。
     *
     * @return
     */
    public float getXOffset() {
        return mXOffset;
    }

    /**
     * 为该轴上的标签设置使用的 x 轴偏移量。
     *
     * @param xOffset
     */
    public void setXOffset(float xOffset) {
        mXOffset = Utils.convertDpToPixel(xOffset);
    }

    /**
     * 返回用于绘制轴标签的 x 轴偏移量。此偏移量应用于标签之前和之后。
     *
     * @return
     */
    public float getYOffset() {
        return mYOffset;
    }

    /**
     *为该轴上的标签设置使用的 y 轴偏移量。对于图例，较高的偏移量意味着整个图例将被放置在离顶部更远的位置。
     *
     * @param yOffset
     */
    public void setYOffset(float yOffset) {
        mYOffset = Utils.convertDpToPixel(yOffset);
    }

    /**
     * 返回用于标签的字体，如果没有设置则返回 null
     *
     * @return
     */
    public Typeface getTypeface() {
        return mTypeface;
    }

    /**
     * 为标签设置特定的字体
     *
     * @param tf
     */
    public void setTypeface(Typeface tf) {
        mTypeface = tf;
    }

    /**
     * 以密度像素为单位设置标签文本的大小 min = 6f, max = 24f, 默认 10f
     * @param size the text size, in DP
     */
    public void setTextSize(float size) {

        if (size > 24f)
            size = 24f;
        if (size < 6f)
            size = 6f;

        mTextSize = Utils.convertDpToPixel(size);
    }

    /**
     * 返回当前为标签设置的文本大小，以像素为单位
     *
     * @return
     */
    public float getTextSize() {
        return mTextSize;
    }


    /**
     * Sets the text color to use for the labels. Make sure to use
     * getResources().getColor(...) when using a color from the resources.
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextColor = color;
    }

    /**
     * Returns the text color that is set for the labels.
     *
     * @return
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Set this to true if this component should be enabled (should be drawn),
     * false if not. If disabled, nothing of this component will be drawn.
     * Default: true
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    /**
     * Returns true if this comonent is enabled (should be drawn), false if not.
     *
     * @return
     */
    public boolean isEnabled() {
        return mEnabled;
    }
}
