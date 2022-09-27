package com.nsx.cnwinchart.utils;

import android.util.Log;

import com.nsx.cnwinchart.activity.CompositeIndexBean;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by xhu_ww on 2018/6/1.
 * description:
 */
public class DateUtil {

    public static String formatDateToMD(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sf2 = new SimpleDateFormat("MM-dd");
        String formatStr = "";
        try {
            formatStr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatStr;
    }

    public static String formatDateToYMD(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
        String formatStr = "";
        try {
            formatStr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatStr;
    }

    /**
     * 20150602格式的时间转成时间戳
     *
     * @param formatString
     * @param dateString
     * @return
     */
    public static long getDateStringToLong(String formatString, String dateString) {

        SimpleDateFormat format = new SimpleDateFormat(formatString);
        long a = 0;
        try {
            Date date = format.parse(dateString);
            a = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return a/1000;
    }

    /**
     * 将 long 毫秒 转化为 date 类
     *
     * @param time
     * @return
     */
    public static Date getDate(long time) {
        Date date = new Date();
        date.setTime(time);
        return date;
    }

    /**
     * 计算加几个月之后的时间
     *
     * @param inputDate
     * @param number
     * @return
     */
    public static Date getAfterMonth(String inputDate, int number) {
        Calendar c = Calendar.getInstance();//获得一个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(inputDate);//初始日期
        } catch (Exception e) {

        }
        c.setTime(date);//设置日历时间
        c.add(Calendar.MONTH, number);//在日历的月份上增加多少月
        String strDate = sdf.format(c.getTime());//的到你想要得多少个月后的日期
        date = sdf.parse(strDate, new ParsePosition(0));
        return date;
    }

    public static List<String> timeCollection(String startTime) {
        List<String> times = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            Date date = getAfterMonth(startTime, i);
            String dateTime = dateToString(date);
            times.add(dateTime);
        }

        return times;
    }

    /**
     * Date转为String(yyyyMMdd)
     *
     * @param time
     * @return
     */
    public static String dateToString(Date time) {
        String dateStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            dateStr = dateFormat.format(time);
            System.out.println(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {   //同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {    //闰年
                    timeDistance += 366;
                } else {    //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {   //不同年
            return day2 - day1;
        }
    }

    /**
     * time2 - time1(计算两个时间戳之间间隔的月份数)
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int spacingTime(long time1, long time2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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

    public static List<CompositeIndexBean> assembleDataBean(List<Double> data, List<String> times) {
        List<CompositeIndexBean> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            CompositeIndexBean bean = new CompositeIndexBean();
            bean.setDataSource(data.get(i));
            bean.setDataTime(getDateStringToLong("yyyyMMdd", times.get(i)) + "");
            Log.i("------data---time", getDateStringToLong("yyyyMMdd", times.get(i)) + "");
            list.add(bean);
        }
        return list;
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param milliseconds 精确到豪秒的字符串
     * @return
     */
    public static String timeStamp2Date(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(milliseconds));
    }

    public static long timestampToDate(String time) {
        long temp = getDateStringToLong("yyyyMMdd", time);
        Timestamp ts = new Timestamp(temp);
        Date date = new Date();
        try {
            date = ts;
            //System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
}
