
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PKXAxisRenderer extends XAxisRenderer {

    protected XAxis mXAxis;

    public PKXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);

        this.mXAxis = xAxis;

        mAxisLabelPaint.setColor(Color.BLACK);
        mAxisLabelPaint.setTextAlign(Align.CENTER);
        mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10f));
    }

    protected void setupGridPaint() {
        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());
        mGridPaint.setPathEffect(mXAxis.getGridDashPathEffect());
    }

    @Override
    public void computeAxis(float min, float max, boolean inverted) {

        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (mViewPortHandler.contentWidth() > 10 && !mViewPortHandler.isFullyZoomedOutX()) {

            MPPointD p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop());
            MPPointD p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentRight(), mViewPortHandler.contentTop());

            if (inverted) {
                min = (float) p2.x;
                max = (float) p1.x;
            } else {
                min = (float) p1.x;
                max = (float) p2.x;
            }

            MPPointD.recycleInstance(p1);
            MPPointD.recycleInstance(p2);
        }

        computeAxisValues(min, max);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void computeAxisValues(float min, float max) {
//        super.computeAxisValues(min, max);
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
        Log.i("------positions123----", " intervalMagnitude = " + intervalMagnitude);
        Log.i("------positions123----", " intervalSigDigit = " + intervalSigDigit);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }
        Log.i("------positions123----", " interval = " + interval);
        int n = mAxis.isCenterAxisLabelsEnabled() ? 1 : 0;
        // 强制标签计数
        if (mAxis.isForceLabelsEnabled()) {

            Log.i("------positions123----", " labelCount = " + labelCount);

            Log.i("------positions123----", " mAxis.mEntries = " + mAxis.mEntries.length);

            interval = (float) range / (float) (labelCount - 1);
            mAxis.mEntryCount = labelCount;

            if (mAxis.mEntries.length < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[labelCount];
            }

            float fx = mXAxis.getAxisMinimum();
            BigDecimal bDecimal = new BigDecimal(fx);
            long fxL = bDecimal.longValue();
            LocalDate local = Instant.ofEpochMilli(fxL * 1000).atZone(ZoneId.systemDefault()).toLocalDate();
            local = local.plusMonths(18);
            long end = LocalDate.from(local).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
            Log.i("------positions123----", " LocalDate end = " + end);
            if (mXAxis.getZoomNum() == 4 && min > end) {
                min = end;
            }

            Log.i("------positions123----", " LocalDate min = " + new BigDecimal(min).longValue());
            BigDecimal bigDecimal = new BigDecimal(min);
            long minValue = bigDecimal.longValue();
            Log.i("------positions123----", " timestamp  bigDecimal = " + bigDecimal.longValue());
            Log.i("------positions123----", " labelCount = " + labelCount);
            long timestamp;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                for (int i = 0; i < labelCount; i++) {
                    LocalDate localDate = Instant.ofEpochMilli(minValue * 1000).atZone(ZoneId.systemDefault()).toLocalDate();
                    if (mXAxis.getZoomNum() == 1) {
                        localDate = localDate.plusMonths(3 * i);
                    } else {
                        localDate = localDate.plusMonths(i);
                    }
                    timestamp = LocalDate.from(localDate).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
                    if (timestamp > mXAxis.getAxisMaximum()) {
                        BigDecimal bigDecimal1 = new BigDecimal(mXAxis.getAxisMaximum());
                        timestamp = bigDecimal.longValue();
                    }
                    Log.i("------positions123----", " timestamp  after = " + timestamp);
                    mAxis.mEntries[i] = timestamp;
                }
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

        computeSize();
    }

    protected void computeSize() {

        String longest = mXAxis.getLongestLabel();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        final FSize labelSize = Utils.calcTextSize(mAxisLabelPaint, longest);

        final float labelWidth = labelSize.width;
        final float labelHeight = Utils.calcTextHeight(mAxisLabelPaint, "Q");

        final FSize labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
                labelWidth,
                labelHeight,
                mXAxis.getLabelRotationAngle());


        mXAxis.mLabelWidth = Math.round(labelWidth);
        mXAxis.mLabelHeight = Math.round(labelHeight);
        mXAxis.mLabelRotatedWidth = Math.round(labelRotatedSize.width);
        mXAxis.mLabelRotatedHeight = Math.round(labelRotatedSize.height);

        FSize.recycleInstance(labelRotatedSize);
        FSize.recycleInstance(labelSize);
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float yoffset = mXAxis.getYOffset();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        MPPointF pointF = MPPointF.getInstance(0, 0);
        if (mXAxis.getPosition() == XAxisPosition.TOP) {
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabels(c, mViewPortHandler.contentTop() - yoffset, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabels(c, mViewPortHandler.contentTop() + yoffset + mXAxis.mLabelRotatedHeight, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, mViewPortHandler.contentBottom() + yoffset, pointF);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, mViewPortHandler.contentBottom() - yoffset - mXAxis.mLabelRotatedHeight, pointF);

        } else { // BOTH SIDED
            pointF.x = 0.5f;
            pointF.y = 1.0f;
            drawLabels(c, mViewPortHandler.contentTop() - yoffset, pointF);
            pointF.x = 0.5f;
            pointF.y = 0.0f;
            drawLabels(c, mViewPortHandler.contentBottom() + yoffset, pointF);
        }
        MPPointF.recycleInstance(pointF);
    }

    @Override
    public void renderAxisLine(Canvas c) {

        if (!mXAxis.isDrawAxisLineEnabled() || !mXAxis.isEnabled())
            return;

        mAxisLinePaint.setColor(mXAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mXAxis.getAxisLineWidth());
        mAxisLinePaint.setPathEffect(mXAxis.getAxisLineDashPathEffect());

        if (mXAxis.getPosition() == XAxisPosition.TOP
                || mXAxis.getPosition() == XAxisPosition.TOP_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {


            c.drawLine(mViewPortHandler.contentLeft() - 50,
                    mViewPortHandler.contentTop(), mViewPortHandler.contentRight() + 50,
                    mViewPortHandler.contentTop(), mAxisLinePaint);
        }

        if (mXAxis.getPosition() == XAxisPosition.BOTTOM
                || mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft() - 50,
                    mViewPortHandler.contentBottom(), mViewPortHandler.contentRight() + 50,
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    /**
     * 绘X轴制刻度线
     *
     * @param c
     */
    public void renderScaleLines(Canvas c) {
        if (!mXAxis.isDrawScale() || !mXAxis.isEnabled())
            return;
        if (mRenderGridLinesBuffer.length != mAxis.mEntryCount * 2) {
            mRenderGridLinesBuffer = new float[mXAxis.mEntryCount * 2];
        }
        float[] positions = mRenderGridLinesBuffer;
        for (int i = 0; i < positions.length; i += 2) {
            positions[i] = mXAxis.mEntries[i / 2];
            positions[i + 1] = mXAxis.mEntries[i / 2];
        }
        mTrans.pointValuesToPixel(positions); //获得X轴点对应的像素点位置 即坐标系位置
        for (int i = 0; i < positions.length - 2; i += 2) {
            //计算X轴两个值之间的间距/5   =  画布偏移量 即 刻度间距 还是默认5个刻度一组
            float offset = (positions[2] - positions[0]) / 5;
            drawScale(c, positions[i], offset);
        }
    }

    /**
     * 绘制线
     */
    protected void drawScale(Canvas canvas, float startX, float offset) {
        boolean isDrawShortLine = true;

        float topY = mViewPortHandler.contentTop(); //顶部X轴所在的位置
        float bottomY = mViewPortHandler.contentBottom(); //底部X轴所在的位置
        canvas.save();
        if (mXAxis.getPosition() == XAxisPosition.BOTTOM) { //X轴位置在下方时
            for (int i = 0; i <= 5; i++) {
                canvas.save();
                canvas.translate(offset * i, 0);
                if (i % 5 == 0) {
                    //刻度线在图表内部
//                    canvas.drawLine(startX, bottomY - 20, startX, bottomY, mAxisLinePaint);//画长刻度线
                    //刻度线在图表外面
                    canvas.drawLine(startX, bottomY + 20, startX, bottomY, mAxisLinePaint);//画长刻度线
                } else if (isDrawShortLine) {
                    mAxisLinePaint.setColor(Color.LTGRAY);
//                    canvas.drawLine(startX, bottomY - 10, startX, bottomY, mAxisLinePaint);//画短刻度线
                    canvas.drawLine(startX, bottomY + 10, startX, bottomY, mAxisLinePaint);//画短刻度线
                }
                canvas.restore();
            }
        } else if (mXAxis.getPosition() == XAxisPosition.TOP) { //X轴位置在上方时
            for (int i = 0; i <= 5; i++) {
                canvas.save();
                canvas.translate(offset * i, 0);
                if (i % 5 == 0) {
                    canvas.drawLine(startX, topY + 20, startX, topY, mAxisLinePaint);//画长刻度线
                } else if (isDrawShortLine) {
                    mAxisLinePaint.setColor(Color.LTGRAY);
                    canvas.drawLine(startX, topY + 10, startX, topY, mAxisLinePaint);//画短刻度线
                }
                canvas.restore();
            }
        } else if (mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) { //上下都有X轴时
            for (int i = 0; i <= 5; i++) {
                canvas.save();
                canvas.translate(offset * i, 0);
                if (i % 5 == 0) {
                    //画长刻度线
                    canvas.drawLine(startX, topY + 20, startX, topY, mAxisLinePaint);//顶部X轴的刻度
                    canvas.drawLine(startX, bottomY - 20, startX, bottomY, mAxisLinePaint);//底部X轴的刻度
                } else if (isDrawShortLine) {
                    //画短刻度线
                    canvas.drawLine(startX, topY + 10, startX, topY, mAxisLinePaint);//顶部X轴的刻度
                    canvas.drawLine(startX, bottomY - 10, startX, bottomY, mAxisLinePaint);//底部X轴的刻度
                }
                canvas.restore();
            }
        }
        canvas.restore();
    }

    protected void drawXLabelsUnt(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        mAxisLabelPaint.setColor(mXAxis.getUnitColor());
        Utils.drawXAxisValue(c, formattedLabel, x, y, mAxisLabelPaint, anchor, angleDegrees);
    }

    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {

            // only fill x values
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {
            float x;
            if (mXAxis.getZoomNum() == 2) {
                if (i == positions.length - 2)
                    x = positions[i] - 2;
                else
                    x = positions[i];
            } else if (mXAxis.getZoomNum() == 4) {
                x = positions[i] - 10;
            } else {
                x = positions[i];
            }


            Log.i("------positions123----", " x = " + x);

            if (mViewPortHandler.isInBoundsX(x)) {
                String label = mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);
                Log.i("------positions123----", " mXAxis.mEntries[i / 2] = " + mXAxis.mEntries[i / 2]);
                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && x + width > mViewPortHandler.getChartWidth())
                            x -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        x += width / 2;
                    }
                }

                if (i == 0) {
                    label = "";
                    drawLabel(c, label, x + 50, pos, anchor, labelRotationAngleDegrees);
                } else {
                    drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
                }
            }
        }

        if (positions.length > 0)
            drawXLabelsUnt(c, mXAxis.getUnit(), positions[0], pos, anchor, labelRotationAngleDegrees);
    }

    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        Utils.drawXAxisValue(c, formattedLabel, x, y, mAxisLabelPaint, anchor, angleDegrees);
    }

    protected Path mRenderGridLinesPath = new Path();
    protected float[] mRenderGridLinesBuffer = new float[2];

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        int clipRestoreCount = c.save();
        c.clipRect(getGridClippingRect());

        if (mRenderGridLinesBuffer.length != mAxis.mEntryCount * 2) {
            mRenderGridLinesBuffer = new float[mXAxis.mEntryCount * 2];
        }
        float[] positions = mRenderGridLinesBuffer;

        for (int i = 0; i < positions.length; i += 2) {
            positions[i] = mXAxis.mEntries[i / 2];
            positions[i + 1] = mXAxis.mEntries[i / 2];
        }

        mTrans.pointValuesToPixel(positions);

        setupGridPaint();

        Path gridLinePath = mRenderGridLinesPath;
        gridLinePath.reset();

        for (int i = 0; i < positions.length; i += 2) {
            if (i == 0 || i == positions.length - 2) {
                continue;
            }
            drawGridLine(c, positions[i], positions[i + 1], gridLinePath);
        }
        c.restoreToCount(clipRestoreCount);
    }

    /**
     * 计算两个时间戳相差几个月
     *
     * @param time1 开始时间戳
     * @param time2 结束时间戳
     * @return 相差月份
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int spacingTime(long time1, long time2) {
        LocalDate localDate1 = Instant.ofEpochMilli(time1).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = Instant.ofEpochMilli(time2).atZone(ZoneId.systemDefault()).toLocalDate();
        int years = localDate1.until(localDate2).getYears();
        int months = localDate1.until(localDate2).getMonths();
        return years * 12 + months;
    }

    protected RectF mGridClippingRect = new RectF();

    public RectF getGridClippingRect() {
        mGridClippingRect.set(mViewPortHandler.getContentRect());
        mGridClippingRect.inset(-mAxis.getGridLineWidth(), 0.f);
        return mGridClippingRect;
    }

    /**
     * Draws the grid line at the specified position using the provided path.
     *
     * @param c
     * @param x
     * @param y
     * @param gridLinePath
     */
    protected void drawGridLine(Canvas c, float x, float y, Path gridLinePath) {

        gridLinePath.moveTo(x, mViewPortHandler.contentBottom());
        gridLinePath.lineTo(x, mViewPortHandler.contentTop());

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, mGridPaint);

        gridLinePath.reset();
    }

    protected void drawGridLineTrans(Canvas c, float x, float y, Path gridLinePath) {

        gridLinePath.moveTo(x, mViewPortHandler.contentBottom());
        gridLinePath.lineTo(x, mViewPortHandler.contentTop());

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, mGridPaint);
        gridLinePath.reset();
    }

    protected float[] mRenderLimitLinesBuffer = new float[2];
    protected RectF mLimitLineClippingRect = new RectF();

    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = mXAxis.getLimitLines();

        if (limitLines == null || limitLines.size() <= 0)
            return;

        float[] position = mRenderLimitLinesBuffer;
        position[0] = 0;
        position[1] = 0;

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            int clipRestoreCount = c.save();
            mLimitLineClippingRect.set(mViewPortHandler.getContentRect());
            mLimitLineClippingRect.inset(-l.getLineWidth(), 0.f);
            c.clipRect(mLimitLineClippingRect);

            position[0] = l.getLimit();
            position[1] = 0.f;

            mTrans.pointValuesToPixel(position);

            renderLimitLineLine(c, l, position);
            renderLimitLineLabel(c, l, position, 2.f + l.getYOffset());

            c.restoreToCount(clipRestoreCount);
        }
    }

    float[] mLimitLineSegmentsBuffer = new float[4];
    private Path mLimitLinePath = new Path();

    public void renderLimitLineLine(Canvas c, LimitLine limitLine, float[] position) {
        mLimitLineSegmentsBuffer[0] = position[0];
        mLimitLineSegmentsBuffer[1] = mViewPortHandler.contentTop();
        mLimitLineSegmentsBuffer[2] = position[0];
        mLimitLineSegmentsBuffer[3] = mViewPortHandler.contentBottom();

        mLimitLinePath.reset();
        mLimitLinePath.moveTo(mLimitLineSegmentsBuffer[0], mLimitLineSegmentsBuffer[1]);
        mLimitLinePath.lineTo(mLimitLineSegmentsBuffer[2], mLimitLineSegmentsBuffer[3]);

        mLimitLinePaint.setStyle(Paint.Style.STROKE);
        mLimitLinePaint.setColor(limitLine.getLineColor());
        mLimitLinePaint.setStrokeWidth(limitLine.getLineWidth());
        mLimitLinePaint.setPathEffect(limitLine.getDashPathEffect());

        c.drawPath(mLimitLinePath, mLimitLinePaint);
    }

    public void renderLimitLineLabel(Canvas c, LimitLine limitLine, float[] position, float yOffset) {
        String label = limitLine.getLabel();

        // if drawing the limit-value label is enabled
        if (label != null && !label.equals("")) {

            mLimitLinePaint.setStyle(limitLine.getTextStyle());
            mLimitLinePaint.setPathEffect(null);
            mLimitLinePaint.setColor(limitLine.getTextColor());
            mLimitLinePaint.setStrokeWidth(0.5f);
            mLimitLinePaint.setTextSize(limitLine.getTextSize());


            float xOffset = limitLine.getLineWidth() + limitLine.getXOffset();

            final LimitLine.LimitLabelPosition labelPosition = limitLine.getLabelPosition();

            if (labelPosition == LimitLine.LimitLabelPosition.RIGHT_TOP) {

                final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                mLimitLinePaint.setTextAlign(Align.LEFT);
                c.drawText(label, position[0] + xOffset, mViewPortHandler.contentTop() + yOffset + labelLineHeight,
                        mLimitLinePaint);
            } else if (labelPosition == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                mLimitLinePaint.setTextAlign(Align.LEFT);
                c.drawText(label, position[0] + xOffset, mViewPortHandler.contentBottom() - yOffset, mLimitLinePaint);
            } else if (labelPosition == LimitLine.LimitLabelPosition.LEFT_TOP) {

                mLimitLinePaint.setTextAlign(Align.RIGHT);
                final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                c.drawText(label, position[0] - xOffset, mViewPortHandler.contentTop() + yOffset + labelLineHeight,
                        mLimitLinePaint);
            } else {

                mLimitLinePaint.setTextAlign(Align.RIGHT);
                c.drawText(label, position[0] - xOffset, mViewPortHandler.contentBottom() - yOffset, mLimitLinePaint);
            }
        }
    }
}
