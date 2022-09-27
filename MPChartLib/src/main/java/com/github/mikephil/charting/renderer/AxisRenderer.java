
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Baseclass of all axis renderers.
 *
 * @author Philipp Jahoda
 */
public abstract class AxisRenderer extends Renderer {

    /**
     * 此轴渲染器使用的基轴
     */
    protected AxisBase mAxis;

    /**
     * 转换器将值转换为屏幕像素并返回
     */
    protected Transformer mTrans;

    /**
     * 绘制网格线的对象
     */
    protected Paint mGridPaint;

    /**
     * 为 x-label 值绘制
     */
    protected Paint mAxisLabelPaint;

    /**
     * paint for the line surrounding the chart
     */
    protected Paint mAxisLinePaint;

    /**
     * paint used for the limit lines
     */
    protected Paint mLimitLinePaint;

    public AxisRenderer(ViewPortHandler viewPortHandler, Transformer trans, AxisBase axis) {
        super(viewPortHandler);

        this.mTrans = trans;
        this.mAxis = axis;

        if (mViewPortHandler != null) {

            mAxisLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            mGridPaint = new Paint();
            mGridPaint.setColor(Color.GRAY);
            mGridPaint.setStrokeWidth(1f);
            mGridPaint.setStyle(Style.STROKE);
            mGridPaint.setAlpha(90);

            mAxisLinePaint = new Paint();
            mAxisLinePaint.setColor(Color.BLACK);
            mAxisLinePaint.setStrokeWidth(1f);
            mAxisLinePaint.setStyle(Style.STROKE);

            mLimitLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLimitLinePaint.setStyle(Paint.Style.STROKE);
        }
    }

    /**
     * Returns the Paint object used for drawing the axis (labels).
     *
     * @return
     */
    public Paint getPaintAxisLabels() {
        return mAxisLabelPaint;
    }

    /**
     * Returns the Paint object that is used for drawing the grid-lines of the
     * axis.
     *
     * @return
     */
    public Paint getPaintGrid() {
        return mGridPaint;
    }

    /**
     * Returns the Paint object that is used for drawing the axis-line that goes
     * alongside the axis.
     *
     * @return
     */
    public Paint getPaintAxisLine() {
        return mAxisLinePaint;
    }

    /**
     * Returns the Transformer object used for transforming the axis values.
     *
     * @return
     */
    public Transformer getTransformer() {
        return mTrans;
    }

    /**
     * 计算轴值。
     *
     * @param min - the minimum value in the data object for this axis
     * @param max - the maximum value in the data object for this axis
     */
    public void computeAxis(float min, float max, boolean inverted) {
        // 计算 y 标签的起点和入口点（取决于缩放 contentrect 边界）
        if (mViewPortHandler != null && mViewPortHandler.contentWidth() > 10 && !mViewPortHandler.isFullyZoomedOutY()) {

            MPPointD p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop());
            MPPointD p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom());

            //是否反转
            if (!inverted) {
                min = (float) p2.y;
                max = (float) p1.y;
            } else {
                min = (float) p1.y;
                max = (float) p2.y;
            }

            MPPointD.recycleInstance(p1);
            MPPointD.recycleInstance(p2);
        }

        computeAxisValues(min, max);

    }

    /**
     * 设置轴值。计算两个给定极值之间的所需标签数量.
     *
     * @return
     */
    protected void computeAxisValues(float min, float max) {

        //根据两个极点数据，计算 中间 所需要的标签数量
        float yMin = min;
        float yMax = max;

        //获取label 的数量
        int labelCount = mAxis.getLabelCount();

        // 计算区间 取绝对值
        double range = Math.abs(yMax - yMin);

        //
        if (labelCount == 0 || range <= 0 || Double.isInfinite(range)) {
            //实际的条目数组
            mAxis.mEntries = new float[]{};
            //轴标签条目仅用于居中标签
            mAxis.mCenteredEntries = new float[]{};
            //图例包含的条目数
            mAxis.mEntryCount = 0;
            return;
        }

        // 找出轴值之间的间距（在 y 值空间中）
        // 计算可以分成几个 区间
        double rawInterval = range / labelCount;
        //
        double interval = Utils.roundToNextSignificant(rawInterval);

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled())
            interval = interval < mAxis.getGranularity() ? mAxis.getGranularity() : interval;

        // 标准化间隔
        double intervalMagnitude = Utils.roundToNextSignificant(Math.pow(10, (int) Math.log10(interval)));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        int n = mAxis.isCenterAxisLabelsEnabled() ? 1 : 0;

        // 强制标签计数
        if (mAxis.isForceLabelsEnabled()) {

            interval = (float) range / (float) (labelCount - 1);
            mAxis.mEntryCount = labelCount;

            if (mAxis.mEntries.length < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[labelCount];
            }

            float v = min;

            for (int i = 0; i < labelCount; i++) {
                mAxis.mEntries[i] = v;
                v += interval;
            }

            n = labelCount;

            // 没有强制计数
        } else {

            double first = interval == 0.0 ? 0.0 : Math.ceil(yMin / interval) * interval;
            if (mAxis.isCenterAxisLabelsEnabled()) {
                first -= interval;
            }

            double last = interval == 0.0 ? 0.0 : Utils.nextUp(Math.floor(yMax / interval) * interval);

            double f;
            int i;

            if (interval != 0.0) {
                for (f = first; f <= last; f += interval) {
                    ++n;
                }
            }

            mAxis.mEntryCount = n;

            if (mAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[n];
            }

            for (f = first, i = 0; i < n; f += interval, ++i) {

                if (f == 0.0) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0.0;

                mAxis.mEntries[i] = (float) f;
            }
        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mAxis.mDecimals = 0;
        }

        if (mAxis.isCenterAxisLabelsEnabled()) {

            if (mAxis.mCenteredEntries.length < n) {
                mAxis.mCenteredEntries = new float[n];
            }

            float offset = (float) interval / 2f;

            for (int i = 0; i < n; i++) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset;
            }
        }
    }

    /**
     * Draws the axis labels to the screen.
     *
     * @param c
     */
    public abstract void renderAxisLabels(Canvas c);

    /**
     * Draws the grid lines belonging to the axis.
     *
     * @param c
     */
    public abstract void renderGridLines(Canvas c);

    /**
     * Draws the line that goes alongside the axis.
     *
     * @param c
     */
    public abstract void renderAxisLine(Canvas c);

    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    public abstract void renderLimitLines(Canvas c);
}
