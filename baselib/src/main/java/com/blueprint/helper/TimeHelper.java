package com.blueprint.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeHelper {

    private static final String DateFormat_YMD = "yyyy-MM-dd";
    private static final String DateFormat_hms = "HH:mm''ss\"";//44:20'30"
    private static final String SECONDFORMAT_SMS = "%02d\'%02d\"";//20'30"

    /**
     * 指定时间长度 输出 指定格式时间长度
     * @param sec
     * @param ymd
     * @return
     */
    public static String sec2fotmat(int sec, String ymd) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(ymd, Locale.getDefault());
        return timeFormat.format(new Date(sec*1000 - TimeZone.getDefault().getRawOffset()));
    }

    public static String secfotmat(int sec) {
        return String.format(SECONDFORMAT_SMS, sec % 3600 / 60, sec % 60);
    }

    /**
     * 指定时间长度 转为 hh:mm'ss"格式
     * <h1> >>>1800--00:30'00"
     * @param sec
     * @return
     */
    public static String sec2fotmat(int sec) {
        return sec2fotmat(sec,DateFormat_hms);
    }


    /**
     * 获取两日期之间相隔的天数
     * <p color="red">要获取两个日期之间的 总天数需要 +1 包头包尾</p>
     *
     * @param startDate
     * @param endDate
     * @param ymd       startDate和endDate日期格式
     * @return
     */
    public static int getDaysBetween(String startDate, String endDate, String ymd) {
        SimpleDateFormat format = new SimpleDateFormat(ymd);
        try {
            if (ymd.equals(DateFormat_YMD)) {
                return (int) ((format.parse(endDate).getTime() - format.parse(startDate).getTime()) / (1000 * 3600 * 24));
            } else {
                //ymd 中包含时分秒
                return getDaysBetween(format.parse(startDate), format.parse(endDate));
            }
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * 获取两日期之间相隔的天数
     * <p color="red"><strong>要获取两个日期之间的 总天数需要 +1 包头包尾</strong></p>
     * @return
     */
    public static int getDaysBetween(Date start, Date end) {
        SimpleDateFormat format = new SimpleDateFormat(DateFormat_YMD);
        try {
            Date parse_end = format.parse(format.format(end));
            Date parse_start = format.parse(format.format(start));
            return (int) ((parse_end.getTime() - parse_start.getTime()) / (1000 * 3600 * 24));
        } catch (ParseException e) {
            return 0;
        }
    }
}
