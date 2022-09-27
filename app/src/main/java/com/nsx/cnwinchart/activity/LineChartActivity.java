package com.nsx.cnwinchart.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.nsx.cnwinchart.R;
import com.nsx.cnwinchart.manager.LineChartManager;
import com.nsx.cnwinchart.utils.LocalDateUtils;
import com.nsx.cnwinchart.utils.LocalJsonAnalyzeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/24 0024.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class LineChartActivity extends AppCompatActivity {
    private Button fd, sx;

    private int x = 0;

    private LineChart lineChart;

    private LineChartManager lineChartManager1;

    private DataModule boyData;

    private DataModule gitlData;

    private int rang = 24;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        initData();
        initView();
    }

    private void initData() {
        //获取数据
        boyData = LocalJsonAnalyzeUtil.JsonToObject(this, "boy_data.json", DataModule.class);
        gitlData = LocalJsonAnalyzeUtil.JsonToObject(this, "girl_data.json", DataModule.class);
    }

    private void initView() {
        String dataStr = "20200131";
        lineChart = findViewById(R.id.lineChart);
        fd = findViewById(R.id.fd);
        sx = findViewById(R.id.sx);

        List<String> lineNames = new ArrayList<>();
        lineNames.add("3");
        lineNames.add("5");
        lineNames.add("10");
        lineNames.add("25");
        lineNames.add("50");
        lineNames.add("75");
        lineNames.add("90");
        lineNames.add("95");
        lineNames.add("98");

        List<String> times = LocalDateUtils.timeCollection(dataStr, LocalDateUtils.UNSIGNED_DATE_PATTERN);
        for (int i = 0; i < times.size(); i++) {
            Log.i("------data---times", times.get(i));
        }

        lineChartManager1 = new LineChartManager(lineChart, dataStr);

        //展示图表
        for (int i = 0; i < boyData.getData().getWeight().size(); i++) {
            List<CompositeIndexBean> beanList = LocalDateUtils.assembleDataBean(boyData.getData().getWeight().get(i), times);
            if (i == 0) {
                lineChartManager1.showLineChart(beanList, lineNames.get(i), Color.parseColor("#C1D7FF"));
            } else {
                lineChartManager1.addLine(beanList, lineNames.get(i), Color.parseColor("#C1D7FF"));
            }
        }
        List<CompositeIndexBean> list = new ArrayList<>();
        long start = 1580400000L;
        float da = 2.459312F;
        for (int i = 1; i < 700; i++) {
            CompositeIndexBean bean = new CompositeIndexBean();
            bean.setDataTime(start + "");
            bean.setDataSource(da);
            start += 86400L;
            if (i > 50 && i < 200) {
                bean.setDataSource(1.5f);
            } else if (i > 300 && i < 500) {
                bean.setDataSource(16.5f);
            } else {
                da += 0.01f;
            }
            list.add(bean);
        }
        lineChartManager1.addLines(list, "user", getResources().getColor(R.color.slateblue));
        List<Long> strings = new ArrayList<>();
        for (int t = 0; t < times.size(); t++) {
            Long str = LocalDateUtils.getDateStringToLong("yyyyMMdd", times.get(t)) / 1000;
            Log.i("------data---str", str + " ");
            strings.add(str);
        }

        lineChartManager1.setXAxisData(strings, 9);

        sx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lineChartManager1.zoomOut();
            }
        });

        fd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lineChartManager1.zoomIn();
            }
        });
    }


}
