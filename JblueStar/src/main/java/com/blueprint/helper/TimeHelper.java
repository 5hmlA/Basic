package com.blueprint.helper;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.FORMAT_NUMERIC_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static com.blueprint.helper.RegexHelper.isNumeric;

public class TimeHelper {

    public static final String DateFormat_YMD = "yyyy-MM-dd";
    public static final String DateFormat_hms = "HH:mm''ss\"";//44:20'30"
    public static final String SECONDFORMAT_SMS = "%02d\'%02d\"";//20'30"

    /**
     * 指定时间长度 输出 指定格式时间长度
     *
     * @param sec
     * @param ymd
     * @return
     */
    public static String sec2fotmat(int sec, String ymd){
        SimpleDateFormat timeFormat = new SimpleDateFormat(ymd, Locale.getDefault());
        return timeFormat.format(new Date(sec*1000-TimeZone.getDefault().getRawOffset()));
    }

    public static String secfotmat(int sec){
        return String.format(SECONDFORMAT_SMS, sec%3600/60, sec%60);
    }

    /**
     * 指定时间长度 转为 hh:mm'ss"格式
     * <h1> >>>1800--00:30'00"
     *
     * @param sec
     * @return
     */
    public static String sec2fotmat(int sec){
        return sec2fotmat(sec, DateFormat_hms);
    }


    /**
     * 获取两日期之间相隔的天数
     * <p color="red">要获取两个日期之间的 总天数需要 +1 包头包尾</p>
     *
     * @param startDate
     * @param endDate
     * @param ymd
     *         startDate和endDate日期格式
     * @return
     */
    public static int getDaysBetween(String startDate, String endDate, String ymd){
        SimpleDateFormat format = new SimpleDateFormat(ymd);
        try {
            if(ymd.equals(DateFormat_YMD)) {
                return (int)( ( format.parse(endDate).getTime()-format.parse(startDate).getTime() )/( 1000*3600*24 ) );
            }else {
                //ymd 中包含时分秒
                return getDaysBetween(format.parse(startDate), format.parse(endDate));
            }
        }catch(ParseException e) {
            return 0;
        }
    }

    /**
     * 获取两日期之间相隔的天数
     * <p color="red"><strong>要获取两个日期之间的 总天数需要 +1 包头包尾</strong></p>
     *
     * @return
     */
    public static int getDaysBetween(Date start, Date end){
        SimpleDateFormat format = new SimpleDateFormat(DateFormat_YMD);
        try {
            Date parse_end = format.parse(format.format(end));
            Date parse_start = format.parse(format.format(start));
            return (int)( ( parse_end.getTime()-parse_start.getTime() )/( 1000*3600*24 ) );
        }catch(ParseException e) {
            return 0;
        }
    }

    /**
     * 计算与当前的时间差相差多少时间
     * strTimestamp单位秒
     */
    public static String dateDistance2(String strTimestamp){
        if(TextUtils.isEmpty(strTimestamp) || strTimestamp.equals("null") && !isNumeric(strTimestamp)) {
            return "时间错误："+strTimestamp;
        }
        long timestamp = Long.parseLong(strTimestamp)*1000;
        long timeLong = System.currentTimeMillis()-timestamp;
        if(timeLong<60*1000) {
            return "刚刚";
        }else if(timeLong<60*60*24*1000*3) {
            return DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), SECOND_IN_MILLIS)
                    .toString();
        }else {
            Time time = new Time();
            time.set(timestamp);
            Time ctime = new Time();
            ctime.set(System.currentTimeMillis());
            if(time.year == ctime.year) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                return sdf.format(new Date(timestamp));
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(new Date(timestamp));
            }
        }
    }

    /**
     * 计算与当前的时间差相差多少时间
     * strTimestamp单位秒
     */
    public static String dateDistance(String strTimestamp){
        if(TextUtils.isEmpty(strTimestamp) || strTimestamp.equals("null") && !isNumeric(strTimestamp)) {
            return "时间错误："+strTimestamp;
        }
        long timestamp = Long.parseLong(strTimestamp)*1000;
        long timeLong = System.currentTimeMillis()-timestamp;
        if(timeLong<1*1000) {
            return "刚刚";
        }else {
            //不是同年会自动显示年
            return DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), 0,
                    FORMAT_SHOW_DATE|FORMAT_ABBREV_ALL|FORMAT_NUMERIC_DATE).toString();
        }
    }

    public static String dateYMD(String strTimestamp){
        if(TextUtils.isEmpty(strTimestamp) || strTimestamp.equals("null") && !isNumeric(strTimestamp)) {
            return "";
        }
        long timestamp = Long.parseLong(strTimestamp);
        Date startDate = new Date(timestamp*1000L); //时间戳转化为日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdf.format(startDate);
    }
}
