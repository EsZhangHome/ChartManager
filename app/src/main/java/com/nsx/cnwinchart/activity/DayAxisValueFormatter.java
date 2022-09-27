package com.nsx.cnwinchart.activity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by philipp on 02/06/16.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class DayAxisValueFormatter implements IAxisValueFormatter {
    private final BarLineChartBase<?> chart;
    private final String mBirth;

    public DayAxisValueFormatter(BarLineChartBase<?> chart, String birth) {
        this.chart = chart;
        mBirth = birth;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        long endTime = (long) value;
        Log.i("------positions123---", "endTime = " + endTime);
        long startTime = getDateStringToLong("yyyyMMdd", mBirth);
        int month = spacingTime(startTime, endTime * 1000);

        Log.i("------positions123---", "month = " + month);

        if (month == 25) month = 24;
        return month + "";
    }

    /**
     * 计算两个时间戳相差几个月
     *
     * @param time1 开始时间戳
     * @param time2 结束时间戳
     * @return 相差月份
     */
//    public int spacingTime(long time1, long time2) {
//        LocalDate localDate1 = Instant.ofEpochMilli(time1).atZone(ZoneId.systemDefault()).toLocalDate();
//        LocalDate localDate2 = Instant.ofEpochMilli(time2).atZone(ZoneId.systemDefault()).toLocalDate();
//        int years = localDate1.until(localDate2).getYears();
//        int months = localDate1.until(localDate2).getMonths();
//        return years * 12 + months;
//    }

    public static int spacingTime(long time1, long time2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String timeStr1 = format.format(new Date(time1));
        String timeStr2 = format.format(new Date(time2));
        Calendar timeCalendar1 = Calendar.getInstance();
        Calendar timeCalendar2 = Calendar.getInstance();
        try {
            timeCalendar1.setTime(format.parse(timeStr1));
            timeCalendar2.setTime(format.parse(timeStr2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int result = timeCalendar2.get(Calendar.MONTH) - timeCalendar1.get(Calendar.MONTH);
        int month = (timeCalendar2.get(Calendar.YEAR) - timeCalendar1.get(Calendar.YEAR)) * 12;
        return result + month;
    }


    /**
     * 将 字符串 日期 转换成 时间戳
     *
     * @param formatString 当前格式的 字符串日期
     * @param dateString   字符串日期
     * @return 时间戳
     */
    public static long getDateStringToLong(String formatString, String dateString) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(formatString);
        LocalDate localDate = LocalDate.parse(dateString, fmt);
        return LocalDate.from(localDate).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
