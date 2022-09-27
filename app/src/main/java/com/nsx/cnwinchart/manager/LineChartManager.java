package com.nsx.cnwinchart.manager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.nsx.cnwinchart.activity.CompositeIndexBean;
import com.nsx.cnwinchart.activity.DayAxisValueFormatter;
import com.nsx.cnwinchart.utils.LocalDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loptop on 2017/6/2.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class LineChartManager implements OnChartGestureListener {

    private LineChart lineChart;
    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYAxis;           //右侧Y轴 自定义XY轴值
    private Legend legend;              //图例
    private LimitLine limitLine;        //限制线
    private String birth;
    private List<String> times;

    private float xMax, xMin;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public LineChartManager(LineChart lineChart, String birth) {
        this.lineChart = lineChart;
        lineChart.setOnChartGestureListener(this);
        leftYAxis = lineChart.getAxisLeft();
        rightYAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        this.birth = birth;
        times = LocalDateUtils.timeCollection(birth, LocalDateUtils.UNSIGNED_DATE_PATTERN);
        xMin = LocalDateUtils.getDateStringToLong("yyyyMMdd", times.get(0)) / 1000;
        xMax = LocalDateUtils.getDateStringToLong("yyyyMMdd", times.get(times.size() - 1)) / 1000;
        initChart(lineChart);
    }

    /**
     * 初始化图表
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initChart(LineChart lineChart) {
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(false);
        //是否可以拖动
        lineChart.setDragXEnabled(true);
        lineChart.setDragYEnabled(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setDragDecelerationEnabled(false);
        lineChart.setDragDecelerationFrictionCoef(0f);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setLogEnabled(true);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.getViewPortHandler().setMaximumScaleX(8f);
        lineChart.getViewPortHandler().setMaximumScaleY(8f);
        lineChart.zoom(1f, 1.0f, 0, 0);

        //是否有触摸事件
        lineChart.setTouchEnabled(true);

        //设置XY轴动画效果
//        lineChart.animateY(500);
//        lineChart.animateX(500);
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);


        /***XY轴的设置***/
        xAxis = lineChart.getXAxis();
        leftYAxis = lineChart.getAxisLeft();
        rightYAxis = lineChart.getAxisRight();

        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.parseColor("#C1D7FF"));
        xAxis.setValueFormatter(new DayAxisValueFormatter(lineChart, birth));
        //设置Y轴网格线为虚线
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        leftYAxis.setDrawGridLines(true);
        leftYAxis.setGridColor(Color.parseColor("#C1D7FF"));
        //设置Y轴网格线为虚线
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        rightYAxis.setEnabled(true);
        rightYAxis.setDrawGridLines(false);

        leftYAxis.setScaleCount(5);
        rightYAxis.setScaleCount(5);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYAxis.setAxisMinimum(0f);

        leftYAxis.setUnit("kg");
        rightYAxis.setUnit("kg");
        leftYAxis.setUnitColor(Color.parseColor("#0056F1"));
        rightYAxis.setUnitColor(Color.parseColor("#0056F1"));
        xAxis.setUnit("Birth");
        xAxis.setUnitColor(Color.parseColor("#9B9BB1"));

        leftYAxis.setDrawScale(true);
        leftYAxis.setDrawShortLine(true);
        leftYAxis.setLongScaleLineColor(Color.parseColor("#C1D7FF"));
        leftYAxis.setShortScaleLineColor(Color.parseColor("#DDE9FF"));
        leftYAxis.setAxisLineColor(Color.parseColor("#DDE9FF"));
        leftYAxis.setTextColor(Color.parseColor("#0056F1"));

        rightYAxis.setDrawScale(true);
        rightYAxis.setDrawShortLine(true);
        rightYAxis.setLongScaleLineColor(Color.parseColor("#C1D7FF"));
        rightYAxis.setShortScaleLineColor(Color.parseColor("#DDE9FF"));
        rightYAxis.setAxisLineColor(Color.parseColor("#DDE9FF"));
        rightYAxis.setTextColor(Color.parseColor("#0056F1"));



        /***折线图例 标签 设置***/
        legend = lineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);
        //是否显示
        legend.setEnabled(false);
    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);

        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(false);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        lineDataSet.setCubicIntensity(0.01f);
        lineDataSet.setDrawTags(true);
        lineDataSet.setTagBgColor(Color.parseColor("#FFFFFF"));
        lineDataSet.setTagColor(Color.parseColor("#9B9BB1"));
        lineDataSet.setTagSize(sp2px(lineChart.getContext(), 9));

        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }

    /**
     * 配置用户数据点
     *
     * @param lineDataSet
     * @param color
     * @param mode
     */
    private void initLineDataSets(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(false);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        lineDataSet.setCubicIntensity(0.01f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }

    /**
     * 自定义的 X轴显示内容
     *
     * @param xAxisStr
     * @param labelCount x轴的分割数量
     */

    public void setXAxisData(final List<Long> xAxisStr, int labelCount) {
        float[] xAxisFloat = new float[xAxisStr.size()];
        for (int i = 0; i < xAxisStr.size(); i++) {
            xAxisFloat[i] = xAxisStr.get(i);
        }
        xAxis.setAxis(xAxisFloat);
        xAxis.setLabelCount(labelCount, true);
        xAxis.setValueFormatter(new DayAxisValueFormatter(lineChart, birth));
        lineChart.invalidate();
    }

    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxisData(float max, float min, int labelCount) {
        xAxis.setDrawScale(false);
        leftYAxis.setAxisMaximum(max);
        leftYAxis.setAxisMinimum(min);
        leftYAxis.setLabelCount(labelCount, true);
        leftYAxis.setAxisLineWidth(1f);

        rightYAxis.setAxisMaximum(max);
        rightYAxis.setAxisMinimum(min);
        rightYAxis.setLabelCount(labelCount, true);
        rightYAxis.setAxisLineWidth(1f);
        lineChart.invalidate();
    }


    /**
     * 展示曲线
     *
     * @param dataList 数据集合
     * @param name     曲线名称
     * @param color    曲线颜色
     */
    public void showLineChart(final List<CompositeIndexBean> dataList, String name, int color) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            CompositeIndexBean data = dataList.get(i);
            Log.i("------data---DataT", data.getDataTime());
            /**
             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
             */
            Entry entry = new Entry(Float.parseFloat(data.getDataTime()), (float) data.getDataSource());
            entries.add(entry);
        }

        /******根据需求的不同 在此在次设置X Y轴的显示内容******/
        //设置是否绘制刻度
        xAxis.setDrawScale(false);
        xAxis.setAxisLineColor(Color.parseColor("#C1D7FF"));
        //是否绘制X轴线
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.parseColor("#9B9BB1"));

        leftYAxis.setLabelCount(17, true);
        leftYAxis.setAxisMaximum(17f);
        leftYAxis.setAxisMinimum(1f);
        leftYAxis.setAxisLineWidth(1f);


        rightYAxis.setLabelCount(17, true);
        rightYAxis.setAxisMaximum(17f);
        rightYAxis.setAxisMinimum(1f);
        rightYAxis.setAxisLineWidth(1f);


        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        //LINEAR 折线图  CUBIC_BEZIER 圆滑曲线
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }

    /**
     * 添加曲线
     */
    public void addLine(List<CompositeIndexBean> dataList, String name, int color) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            CompositeIndexBean data = dataList.get(i);
            Log.i("------data---DataTime", data.getDataTime());
            long date = Long.parseLong(data.getDataTime());
            Entry entry = new Entry((float) date, (float) data.getDataSource());
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.invalidate();
    }

    /**
     * 设置用户数据线
     *
     * @param dataList
     * @param name
     * @param color
     */
    public void addLines(List<CompositeIndexBean> dataList, String name, int color) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            CompositeIndexBean data = dataList.get(i);
            Log.i("------data---Data", data.getDataTime());
            long date = Long.parseLong(data.getDataTime());
            Entry entry = new Entry((float) date, (float) data.getDataSource());
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSets(lineDataSet, color, LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.invalidate();
    }

    /**
     * 重置某条曲线 position 从 0 开始
     */
    public void resetLine(int position, List<CompositeIndexBean> dataList, String name, int color) {
        LineData lineData = lineChart.getData();
        List<ILineDataSet> list = lineData.getDataSets();
        if (list.size() <= position) {
            return;
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            CompositeIndexBean data = dataList.get(i);
            Entry entry = new Entry(i, (float) data.getDataSource());
            entries.add(entry);
        }

        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);

        lineData.getDataSets().set(position, lineDataSet);
        lineChart.invalidate();
    }

    public boolean zoomIn() {
        float zoom = lineChart.getViewPortHandler().getScaleX();
        float wigth = lineChart.getViewPortHandler().getChartWidth();
        Log.i("------data---wigth", wigth + "");
        float gra = wigth / 24;
        Log.i("------data---gra", gra + "");
        Log.i("------data---zoom", zoom + "");
        if (zoom == 1.0) {
            xAxis.setZoomNum(2);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(8000000);
            xAxis.setLabelCount(13, true);
            lineChart.zoom(0, 1f, 0, 0);
            lineChart.zoom(2.0f, 1f, 0, 0);
            getMinMax();
            return false;
        } else if (zoom == 2.0) {
            xAxis.setZoomNum(4);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(8000000);
            xAxis.setLabelCount(7, true);
            lineChart.zoom(0, 1f, 0, 0);
            lineChart.zoom(4.0f, 1f, 0, 0);
            getMinMax();
            return false;
        } else if (zoom == 4.0) {
            xAxis.setZoomNum(8);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(8000000);
            xAxis.setLabelCount(4, true);
            lineChart.zoom(0, 1f, 0, 0);
            lineChart.zoom(8.0f, 1f, 0, 0);
            getMinMax();
            return false;
        } else if (zoom == 8.0) {
            xAxis.setZoomNum(8);
            xAxis.setLabelCount(4, true);
            lineChart.zoom(0, 1f, 0, 0);
            lineChart.zoom(8.0f, 1f, 0, 0);
            getMinMax();
            return true;
        } else {
            return false;
        }
    }

    public boolean zoomOut() {
        float zoom = lineChart.getViewPortHandler().getScaleX();
        float wigth = lineChart.getViewPortHandler().getChartWidth();
        Log.i("------data---wigth", wigth + "");
        float gra = wigth / 24;
        Log.i("------data---gra", gra + "");
        Log.i("------data---zoom", zoom + "");
        if (zoom == 1.0) {
            xAxis.setZoomNum(1);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(8000000);
            xAxis.setLabelCount(9, true);
            lineChart.zoom(0, 1f, 0, 0);
            lineChart.zoom(1.0f, 1f, 0, 0);
            getMinMax();
            return true;
        } else if (zoom == 2.0) {
            xAxis.setZoomNum(1);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(8000000);
            xAxis.setLabelCount(9, true);
            lineChart.zoom(0, 1f, 0, 0);
            lineChart.zoom(1.0f, 1f, 0, 0);
            getMinMax();
            return true;
        } else if (zoom == 4.0) {
            xAxis.setZoomNum(2);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(3000000);
            xAxis.setLabelCount(13, true);
            lineChart.zoom(0, 1f, 0, 0);
            lineChart.zoom(2.0f, 1f, 0, 0);
            getMinMax();
            return false;
        } else if (zoom == 8.0) {
            xAxis.setZoomNum(4);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(3000000);
            xAxis.setLabelCount(7, true);
            lineChart.zoom(0, 1f, 0, 0);
            lineChart.zoom(4.0f, 1f, 0, 0);
            getMinMax();
            return true;
        } else {
            return false;
        }
    }

    public int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void getMinMax() {
        float startX = lineChart.getLowestVisibleX();
        float endX = lineChart.getHighestVisibleX();
        float minY = lineChart.getAxisLeft().mAxisMinimum;
        float maxY = lineChart.getAxisLeft().mAxisMaximum;
        //计算标准线的最小值
        LineDataSet lineDataSetMin = (LineDataSet) lineChart.getLineData().getDataSetByIndex(0);
        List<Entry> entriesMin = lineDataSetMin.getValues();
        for (int i = 0; i < entriesMin.size(); i++) {
            Entry entry = entriesMin.get(i);
            float x = entry.getX();
            if (x == startX) {
                minY = entriesMin.get(i).getY();
                break;
            }
            if (x > startX && i > 0) {
                minY = entriesMin.get(i - 1).getY();
                break;
            }
        }

        //计算标准线的最大值
        LineDataSet lineDataSetMax = (LineDataSet) lineChart.getLineData().getDataSetByIndex(8);
        List<Entry> entriesMax = lineDataSetMax.getValues();
        for (int i = 0; i < entriesMax.size(); i++) {
            Entry entry = entriesMax.get(i);
            float x = entry.getX();
            if (x == endX) {
                maxY = entriesMax.get(i).getY();
                break;
            }
            if (x > endX && (i + 1) < entriesMax.size()) {
                maxY = entriesMax.get(i + 1).getY();
                break;
            }
        }

        //计算用户数据线区间中的最小值
        int beginIndex = 0;
        int endIndex = 0;
        LineDataSet lineDataSetUser = (LineDataSet) lineChart.getLineData().getDataSetByIndex(lineChart.getLineData().getDataSets().size() - 1);
        List<Entry> entriesUser = lineDataSetUser.getValues();
        for (int i = 0; i < entriesUser.size(); i++) {
            Entry entry = entriesUser.get(i);
            float x = entry.getX();
            float y = entry.getY();
            if (x >= startX && x <= endX) {
                if (minY > y) {
                    minY = y;
                }
                if (maxY < y) {
                    maxY = y;
                }
            } else {
                if (x < startX) {
                    beginIndex = i;
                }
                if (x > endX) {
                    endIndex = i;
                }
            }
        }

        if (beginIndex > 0 && endIndex > 0) {
            float beainValue = entriesUser.get(beginIndex).getY();
            float endValue = entriesUser.get(endIndex).getY();

            float minValue = Math.min(beainValue, endValue);
            float maxValue = Math.max(beainValue, endValue);
            minY = Math.min(minY, minValue);
            maxY = Math.max(maxY, maxValue);
        }

        int maxLable;
        int minLable;
        int count;
        minLable = (int) (Math.floor(Double.parseDouble(minY + "")) - 1);
        maxLable = (int) (Math.ceil(Double.parseDouble(maxY + "")));

        count = maxLable - minLable + 1;
        setYAxisData(maxLable, minLable, count);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        getMinMax();
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
