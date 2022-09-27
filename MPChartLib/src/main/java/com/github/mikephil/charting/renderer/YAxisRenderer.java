package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class YAxisRenderer extends AxisRenderer {

    protected YAxis mYAxis;

    protected Paint mZeroLinePaint;

    public YAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, trans, yAxis);

        this.mYAxis = yAxis;

        if (mViewPortHandler != null) {

            mAxisLabelPaint.setColor(Color.BLACK);
            mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

            mZeroLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mZeroLinePaint.setColor(Color.GRAY);
            mZeroLinePaint.setStrokeWidth(1f);
            mZeroLinePaint.setStyle(Paint.Style.STROKE);
        }
    }

    /**
     * 将 y 轴标签绘制到屏幕上
     */
    @Override
    public void renderAxisLabels(Canvas c) {
        if (!mYAxis.isEnabled() || !mYAxis.isDrawLabelsEnabled())
            return;

        //获得 转换 位置
        //将轴条目中包含的值转换为屏幕像素，并以 x 和 y 坐标的浮点数组的形式返回它们。
        float[] positions = getTransformedPositions();

        mAxisLabelPaint.setTypeface(mYAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mYAxis.getTextSize());
        mAxisLabelPaint.setColor(mYAxis.getTextColor());

        float xoffset = mYAxis.getXOffset();
        float yoffset = Utils.calcTextHeight(mAxisLabelPaint, "A") / 2.5f + mYAxis.getYOffset();

        AxisDependency dependency = mYAxis.getAxisDependency();
        YAxisLabelPosition labelPosition = mYAxis.getLabelPosition();

        float xPos = 0f;

        if (dependency == AxisDependency.LEFT) {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                mAxisLabelPaint.setTextAlign(Align.RIGHT);
                xPos = mViewPortHandler.offsetLeft() - xoffset;
            } else {
                mAxisLabelPaint.setTextAlign(Align.LEFT);
                xPos = mViewPortHandler.offsetLeft() + xoffset;
            }
        } else {

            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                mAxisLabelPaint.setTextAlign(Align.LEFT);
                xPos = mViewPortHandler.contentRight() + xoffset;
            } else {
                mAxisLabelPaint.setTextAlign(Align.RIGHT);
                xPos = mViewPortHandler.contentRight() - xoffset;
            }
        }
        drawYLabels(c, xPos, positions, yoffset);
        drawYLabelsUnt(c, xPos, positions);
    }

    @Override
    public void renderAxisLine(Canvas c) {

        if (!mYAxis.isEnabled() || !mYAxis.isDrawAxisLineEnabled())
            return;

        mAxisLinePaint.setColor(mYAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mYAxis.getAxisLineWidth());

        float[] positions = getTransformedPositions();
        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        float yoffset = Utils.calcTextHeight(mAxisLabelPaint, "A") / 2.5f + mYAxis.getYOffset();

        float topFirst = positions[(to - 1) * 2 + 1] + yoffset;
        float topSecond = positions[(to - 2) * 2 + 1] + yoffset;
        float bottomFirst = positions[(from) * 2 + 1] + yoffset;
        float bottomSecond = positions[(from + 1) * 2 + 1] + yoffset;
        float bottomUp = bottomFirst + ((bottomSecond - bottomFirst) / 3f) * 2;
        float bottomDown = bottomFirst + (bottomSecond - bottomFirst) / 3f;
        float spaceBottom = (bottomUp - bottomDown) / 10f;
        float topUp = topSecond + ((topFirst - topSecond) / 3f) * 2;
        float topDown = topSecond + (topFirst - topSecond) / 3f;
        float spaceTop = (topUp - topDown) / 10f;

        if (mYAxis.getAxisDependency() == AxisDependency.LEFT) {
//            c.drawLine(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), mViewPortHandler.contentLeft(),
//                    mViewPortHandler.contentBottom(), mAxisLinePaint);

            Path path = new Path();
            //绘制底部的折线
            path.moveTo(mViewPortHandler.contentLeft(), bottomFirst);
            path.lineTo(mViewPortHandler.contentLeft(), bottomDown);
            path.lineTo(mViewPortHandler.contentLeft() + 12, bottomDown + spaceBottom);
            path.lineTo(mViewPortHandler.contentLeft(), bottomDown + spaceBottom * 2);
            path.lineTo(mViewPortHandler.contentLeft() - 12, bottomDown + spaceBottom * 3);
            path.lineTo(mViewPortHandler.contentLeft(), bottomDown + spaceBottom * 4);
            path.lineTo(mViewPortHandler.contentLeft() + 12, bottomDown + spaceBottom * 5);
            path.lineTo(mViewPortHandler.contentLeft(), bottomDown + spaceBottom * 6);
            path.lineTo(mViewPortHandler.contentLeft() - 12, bottomDown + spaceBottom * 7);
            path.lineTo(mViewPortHandler.contentLeft(), bottomDown + spaceBottom * 8);
            path.lineTo(mViewPortHandler.contentLeft() + 12, bottomDown + spaceBottom * 9);
            path.lineTo(mViewPortHandler.contentLeft(), bottomUp);
            path.lineTo(mViewPortHandler.contentLeft(), bottomSecond);

            //绘制顶部的折线
            path.lineTo(mViewPortHandler.contentLeft(), topSecond);
            path.lineTo(mViewPortHandler.contentLeft(), topDown);
            path.lineTo(mViewPortHandler.contentLeft() + 12, topDown + spaceTop);
            path.lineTo(mViewPortHandler.contentLeft(), topDown + spaceTop * 2);
            path.lineTo(mViewPortHandler.contentLeft() - 12, topDown + spaceTop * 3);
            path.lineTo(mViewPortHandler.contentLeft(), topDown + spaceTop * 4);
            path.lineTo(mViewPortHandler.contentLeft() + 12, topDown + spaceTop * 5);
            path.lineTo(mViewPortHandler.contentLeft(), topDown + spaceTop * 6);
            path.lineTo(mViewPortHandler.contentLeft() - 12, topDown + spaceTop * 7);
            path.lineTo(mViewPortHandler.contentLeft(), topDown + spaceTop * 8);
            path.lineTo(mViewPortHandler.contentLeft() + 12, topDown + spaceTop * 9);
            path.lineTo(mViewPortHandler.contentLeft(), topUp);
            path.lineTo(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop());

            c.drawPath(path, mAxisLinePaint);
        } else {
//            c.drawLine(mViewPortHandler.contentRight(), mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
//                    mViewPortHandler.contentBottom(), mAxisLinePaint);
            Path path = new Path();
            //绘制底部的折线
            path.moveTo(mViewPortHandler.contentRight(), bottomFirst);
            path.lineTo(mViewPortHandler.contentRight(), bottomDown);
            path.lineTo(mViewPortHandler.contentRight() + 12, bottomDown + spaceBottom);
            path.lineTo(mViewPortHandler.contentRight(), bottomDown + spaceBottom * 2);
            path.lineTo(mViewPortHandler.contentRight() - 12, bottomDown + spaceBottom * 3);
            path.lineTo(mViewPortHandler.contentRight(), bottomDown + spaceBottom * 4);
            path.lineTo(mViewPortHandler.contentRight() + 12, bottomDown + spaceBottom * 5);
            path.lineTo(mViewPortHandler.contentRight(), bottomDown + spaceBottom * 6);
            path.lineTo(mViewPortHandler.contentRight() - 12, bottomDown + spaceBottom * 7);
            path.lineTo(mViewPortHandler.contentRight(), bottomDown + spaceBottom * 8);
            path.lineTo(mViewPortHandler.contentRight() + 12, bottomDown + spaceBottom * 9);
            path.lineTo(mViewPortHandler.contentRight(), bottomUp);
            path.lineTo(mViewPortHandler.contentRight(), bottomSecond);

            //绘制顶部的折线
            path.lineTo(mViewPortHandler.contentRight(), topSecond);
            path.lineTo(mViewPortHandler.contentRight(), topDown);
            path.lineTo(mViewPortHandler.contentRight() + 12, topDown + spaceTop);
            path.lineTo(mViewPortHandler.contentRight(), topDown + spaceTop * 2);
            path.lineTo(mViewPortHandler.contentRight() - 12, topDown + spaceTop * 3);
            path.lineTo(mViewPortHandler.contentRight(), topDown + spaceTop * 4);
            path.lineTo(mViewPortHandler.contentRight() + 12, topDown + spaceTop * 5);
            path.lineTo(mViewPortHandler.contentRight(), topDown + spaceTop * 6);
            path.lineTo(mViewPortHandler.contentRight() - 12, topDown + spaceTop * 7);
            path.lineTo(mViewPortHandler.contentRight(), topDown + spaceTop * 8);
            path.lineTo(mViewPortHandler.contentRight() + 12, topDown + spaceTop * 9);
            path.lineTo(mViewPortHandler.contentRight(), topUp);
            path.lineTo(mViewPortHandler.contentRight(), mViewPortHandler.contentTop());
            c.drawPath(path, mAxisLinePaint);
        }
    }

    /**
     * 绘制Y轴刻度线
     */
    public void renderScaleLines(Canvas c) {
        if (!mYAxis.isDrawScale() || !mYAxis.isEnabled())
            return;
        float[] positions = getTransformedPositions();
        //因为 正常情况下 图表坐标轴在 左下方，所以此处 倒序 由下至上 绘制刻度
        for (int i = positions.length; i > 2; i -= 2) {
            if (i == 4 || i == positions.length) continue;
            float offset = (positions[i - 1] - positions[i - 3]) / mYAxis.getScaleCount(); //偏移量
            drawScale(c, positions[i - 3], offset);
        }
    }

    protected void drawScale(Canvas canvas, float startY, float offset) {
        float leftX = mViewPortHandler.contentLeft(); //Y轴在左边的位置
        float rightX = mViewPortHandler.contentRight();//Y轴在右边的位置
        canvas.save();
        if (mYAxis.getAxisDependency() == AxisDependency.LEFT) { //Y轴位置在左边时
            for (int i = 0; i <= mYAxis.getScaleCount(); i++) {
                canvas.save();
                canvas.translate(0, offset * i);
                if (i % mYAxis.getScaleCount() == 0) {
                    mAxisLinePaint.setColor(mYAxis.getLongScaleLineColor());
                    //刻度线在图表外面
                    canvas.drawLine(leftX, startY, leftX - 30, startY, mAxisLinePaint);//画长刻度线
                } else if (mYAxis.isDrawShortLine()) {
                    mAxisLinePaint.setColor(mYAxis.getShortScaleLineColor());
                    //刻度线在图表外面
                    canvas.drawLine(leftX, startY, leftX - 20, startY, mAxisLinePaint);//画短刻度线
                }
                canvas.restore();
            }
        }
        if (mYAxis.getAxisDependency() == AxisDependency.RIGHT) { //Y轴位置在右边时
            for (int i = 0; i <= mYAxis.getScaleCount(); i++) {
                canvas.save();
                canvas.translate(0, offset * i);
                if (i % mYAxis.getScaleCount() == 0) {
                    mAxisLinePaint.setColor(mYAxis.getLongScaleLineColor());
                    //刻度线在图表外面
                    canvas.drawLine(rightX, startY, rightX + 30, startY, mAxisLinePaint);//画长刻度线
                } else if (mYAxis.isDrawShortLine()) {
                    mAxisLinePaint.setColor(mYAxis.getShortScaleLineColor());
                    //刻度线在图表外面
                    canvas.drawLine(rightX, startY, rightX + 20, startY, mAxisLinePaint);//画短刻度线
                }
                canvas.restore();
            }
        }
        canvas.restore();
    }

    /**
     * draws the y-labels on the specified x-position
     *
     * @param fixedPosition
     * @param positions
     */
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {

        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        // draw
        for (int i = from; i < to; i++) {
            String text = "";
            if (i == 0) {
                text = "";
            } else if (i == to - 1) {
                text = "";
            } else {
                text = mYAxis.getFormattedLabel(i);
            }

            c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
        }
    }

    protected void drawYLabelsUnt(Canvas c, float fixedPosition, float[] positions) {
        mAxisLabelPaint.setColor(mYAxis.getUnitColor());
        c.drawText(mYAxis.getUnit(), fixedPosition, positions[1] - 20, mAxisLabelPaint);
    }


    protected Path mRenderGridLinesPath = new Path();

    @Override
    public void renderGridLines(Canvas c) {

        if (!mYAxis.isEnabled())
            return;

        if (mYAxis.isDrawGridLinesEnabled()) {

            int clipRestoreCount = c.save();
            c.clipRect(getGridClippingRect());

            float[] positions = getTransformedPositions();

            mGridPaint.setColor(mYAxis.getGridColor());
            mGridPaint.setStrokeWidth(mYAxis.getGridLineWidth());
            mGridPaint.setPathEffect(mYAxis.getGridDashPathEffect());

            Path gridLinePath = mRenderGridLinesPath;
            gridLinePath.reset();

            // draw the grid
            for (int i = 0; i < positions.length; i += 2) {
                if (i == 0) continue;
                // draw a path because lines don't support dashing on lower android versions
                c.drawPath(linePath(gridLinePath, i, positions), mGridPaint);
                gridLinePath.reset();
            }
            c.restoreToCount(clipRestoreCount);
        }

        if (mYAxis.isDrawZeroLineEnabled()) {
            drawZeroLine(c);
        }
    }

    protected RectF mGridClippingRect = new RectF();

    public RectF getGridClippingRect() {
        mGridClippingRect.set(mViewPortHandler.getContentRect());
        mGridClippingRect.inset(0.f, -mAxis.getGridLineWidth());
        return mGridClippingRect;
    }

    /**
     * Calculates the path for a grid line.
     *
     * @param p
     * @param i
     * @param positions
     * @return
     */
    protected Path linePath(Path p, int i, float[] positions) {

        p.moveTo(mViewPortHandler.offsetLeft(), positions[i + 1]);
        p.lineTo(mViewPortHandler.contentRight(), positions[i + 1]);

        return p;
    }

    protected float[] mGetTransformedPositionsBuffer = new float[2];

    /**
     * 将轴条目中包含的值转换为屏幕像素，并以 x 和 y 坐标的浮点数组的形式返回它们。
     *
     * @return
     */
    protected float[] getTransformedPositions() {

        if (mGetTransformedPositionsBuffer.length != mYAxis.mEntryCount * 2) {
            mGetTransformedPositionsBuffer = new float[mYAxis.mEntryCount * 2];
        }
        float[] positions = mGetTransformedPositionsBuffer;

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed for y-labels
            // 只填充y值，y标签不需要x值
            positions[i + 1] = mYAxis.mEntries[i / 2];
        }

        mTrans.pointValuesToPixel(positions);
        return positions;
    }

    protected Path mDrawZeroLinePath = new Path();
    protected RectF mZeroLineClippingRect = new RectF();

    /**
     * Draws the zero line.
     */
    protected void drawZeroLine(Canvas c) {

        int clipRestoreCount = c.save();
        mZeroLineClippingRect.set(mViewPortHandler.getContentRect());
        mZeroLineClippingRect.inset(0.f, -mYAxis.getZeroLineWidth());
//        mZeroLineClippingRect.left = mZeroLineClippingRect.left - 200;
//        mZeroLineClippingRect.right = mZeroLineClippingRect.right + 200;
        c.clipRect(mZeroLineClippingRect);

        // draw zero line
        MPPointD pos = mTrans.getPixelForValues(0f, 0f);

        mZeroLinePaint.setColor(mYAxis.getZeroLineColor());
        mZeroLinePaint.setStrokeWidth(mYAxis.getZeroLineWidth());

        Path zeroLinePath = mDrawZeroLinePath;
        zeroLinePath.reset();

        zeroLinePath.moveTo(mViewPortHandler.contentLeft() - 50, (float) pos.y);
        zeroLinePath.lineTo(mViewPortHandler.contentRight() + 50, (float) pos.y);
        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(zeroLinePath, mZeroLinePaint);
        c.restoreToCount(clipRestoreCount);
    }

    /**
     * 绘制原点线的刻度线
     *
     * @param c
     * @param mXAxis
     */
    public void drawZeroLineScale(Canvas c, XAxis mXAxis) {
        if (!mYAxis.isDrawZeroLineEnabled()) {
            return;
        }
        MPPointD pos = mTrans.getPixelForValues(0f, 0f);
        if (mYAxis != null) {
            float[] positions = new float[mXAxis.mEntryCount * 2];
            for (int i = 0; i < positions.length; i += 2) {
                positions[i] = mXAxis.mEntries[i / 2];
                positions[i + 1] = mXAxis.mEntries[i / 2];
            }
            mTrans.pointValuesToPixel(positions); //获得X轴点对应的像素点位置 即坐标系位置
            for (int i = 0; i < positions.length - 2; i += 2) {
                //计算X轴两个值之间的间距/5   =  画布偏移量 即 刻度间距 还是默认5个刻度一组
                float offset = (positions[2] - positions[0]) / 5;
                drawZeroScale(c, (float) pos.y, positions[i], offset);
            }
        }
    }

    /**
     * 绘制原点线的刻度线
     *
     * @param zeroPosition 原点位置
     * @param startX       X轴起始位置
     * @param offset       偏移量
     */
    protected void drawZeroScale(Canvas canvas, float zeroPosition, float startX, float offset) {
        canvas.save();
        for (int i = 0; i <= 5; i++) {
            canvas.save();
            canvas.translate(offset * i, 0);
            boolean isDrawShortLine = false;
            if (i % 5 == 0) {
                //刻度线在图表内部
//                    canvas.drawLine(startX, zeroPosition - 20, startX, zeroPosition, mAxisLinePaint);//画长刻度线
                //刻度线在图表外面
                canvas.drawLine(startX, zeroPosition + 20, startX, zeroPosition, mAxisLinePaint);//画长刻度线
            } else if (isDrawShortLine) {
//                    canvas.drawLine(startX, zeroPosition - 10, startX, zeroPosition, mAxisLinePaint);//画短刻度线
                canvas.drawLine(startX, zeroPosition + 10, startX, zeroPosition, mAxisLinePaint);//画短刻度线
            }
            canvas.restore();
        }
        canvas.restore();
    }


    protected Path mRenderLimitLines = new Path();
    protected float[] mRenderLimitLinesBuffer = new float[2];
    protected RectF mLimitLineClippingRect = new RectF();

    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines == null || limitLines.size() <= 0)
            return;

        float[] pts = mRenderLimitLinesBuffer;
        pts[0] = 0;
        pts[1] = 0;
        Path limitLinePath = mRenderLimitLines;
        limitLinePath.reset();

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            int clipRestoreCount = c.save();
            mLimitLineClippingRect.set(mViewPortHandler.getContentRect());
            mLimitLineClippingRect.inset(0.f, -l.getLineWidth());
            c.clipRect(mLimitLineClippingRect);

            mLimitLinePaint.setStyle(Paint.Style.STROKE);
            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());

            pts[1] = l.getLimit();

            mTrans.pointValuesToPixel(pts);

            limitLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1]);
            limitLinePath.lineTo(mViewPortHandler.contentRight(), pts[1]);

            c.drawPath(limitLinePath, mLimitLinePaint);
            limitLinePath.reset();
            // c.drawLines(pts, mLimitLinePaint);

            String label = l.getLabel();

            // if drawing the limit-value label is enabled
            if (label != null && !label.equals("")) {

                mLimitLinePaint.setStyle(l.getTextStyle());
                mLimitLinePaint.setPathEffect(null);
                mLimitLinePaint.setColor(l.getTextColor());
                mLimitLinePaint.setTypeface(l.getTypeface());
                mLimitLinePaint.setStrokeWidth(0.5f);
                mLimitLinePaint.setTextSize(l.getTextSize());

                final float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                float xOffset = Utils.convertDpToPixel(4f) + l.getXOffset();
                float yOffset = l.getLineWidth() + labelLineHeight + l.getYOffset();

                final LimitLine.LimitLabelPosition position = l.getLabelPosition();

                if (position == LimitLine.LimitLabelPosition.RIGHT_TOP) {

                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    c.drawText(label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] - yOffset + labelLineHeight, mLimitLinePaint);

                } else if (position == LimitLine.LimitLabelPosition.RIGHT_BOTTOM) {

                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    c.drawText(label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] + yOffset, mLimitLinePaint);

                } else if (position == LimitLine.LimitLabelPosition.LEFT_TOP) {

                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label,
                            mViewPortHandler.contentLeft() + xOffset,
                            pts[1] - yOffset + labelLineHeight, mLimitLinePaint);

                } else {

                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label,
                            mViewPortHandler.offsetLeft() + xOffset,
                            pts[1] + yOffset, mLimitLinePaint);
                }
            }

            c.restoreToCount(clipRestoreCount);
        }
    }
}
