package com.blueprint.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumHelper {

    /**
     * @param num
     * @return 最接近num的数 同时这个数是5的倍数
     */
    public static int getRound5(float num) {
        return ((int) (num + 2.5f)) / 5 * 5;
    }

    /**
     * @param num
     * @return 根据num向上取数 同时这个数是5的倍数
     */
    public static int getCeuk5(float num) {
        return ((int) (num + 5f)) / 5 * 5;
    }


    /**
     * @param num
     * @return 最接近num的数 同时这个数是10的倍数
     */
    public static int getRound10(float num) {
        return ((int) (num + 5f)) / 10 * 10;
    }

    /**
     * @param num
     * @return 根据num向上取数 这个数是10的倍数
     */
    public static int getCeil10(float num) {
        return ((int) (num + 9.99f)) / 10 * 10;
    }

    /**
     * 数字格式化  去末尾0
     *
     * @param num
     * @param nScale
     * @return
     */
    public static String getNumString(double num, int nScale) {
//        String.format(".3f", num); //四舍五入
        StringBuffer sb = new StringBuffer("#.");
        for (int i = 0; i < nScale; i++) {
            sb.append("#");
        }
        DecimalFormat format = new DecimalFormat(sb.toString());
        return format.format(num);
    }

    /**
     * 数字 百分比 格式化  去末尾0
     *
     * @param num
     * @param nScale
     * @return
     */
    public static String getNumPercent(double num, int nScale) {
        StringBuffer sb = new StringBuffer("#.");
        for (int i = 0; i < nScale; i++) {
            sb.append("#");
        }
        sb.append("%");
        DecimalFormat format = new DecimalFormat(sb.toString());
        return format.format(num);
    }

    /**
     * 小数位数不足补0
     * @param num
     * @param nScale
     * @param type
     * @return
     */
    public static String getNumString(double num, int nScale, int type) {
        NumberFormat numberFormat;
        if (type == 0) {
            numberFormat = NumberFormat.getNumberInstance();
        } else {
            numberFormat = NumberFormat.getPercentInstance();
        }
        numberFormat.setMaximumFractionDigits(nScale);
        numberFormat.setMinimumFractionDigits(nScale);
        return numberFormat.format(num);
    }

    /**
     * 返回指定小数位 的浮点型数据
     * @param num
     * @param nScale
     * @param RoundingMode
     * </p>
     *    <li>HALF_UP    四舍五入，
     *    <li>HALF_DOWN  向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向下舍入。如果被舍弃部分 > 0.5，则舍入行为同 RoundingMode.UP；否则舍入行为同RoundingMode.DOWN。</li>
     *    <li>HALF_EVEN  向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向相邻的偶数舍入。如果舍弃部分左边的数字为奇数，则舍入行为同 RoundingMode.HALF_UP；如果为偶数，则舍入行为同RoundingMode.HALF_DOWN。注意，在重复进行一系列计算时，此舍入模式可以在统计上将累加错误减到最小。此舍入模式也称为“银行家舍入法”
     *    <li>FLOOR      向负无限大方向舍入的舍入模式。如果结果为正，则舍入行为类似于 RoundingMode.DOWN；如果结果为负，则舍入行为类似于RoundingMode.UP。注意，此舍入模式始终不会增加计算值。
     *    <li>CEILING    向正无限大方向舍入的舍入模式。如果结果为正，则舍入行为类似于 RoundingMode.UP；如果结果为负，则舍入行为类似于 RoundingMode.DOWN。注意，此舍入模式始终不会减少计算值。
     *    <li>DOWN       向零方向舍入的舍入模式。从不对舍弃部分前面的数字加 1（即截尾）。注意，此舍入模式始终不会增加计算值的绝对值。
     *    <li>UP         远离零方向舍入的舍入模式。始终对非零舍弃部分前面的数字加 1。注意，此舍入模式始终不会减少计算值的绝对值。
     * @return
     */
    public static float getNumFixScale(double num, int nScale, RoundingMode RoundingMode) {
        BigDecimal bigDecimal = new BigDecimal(num).setScale(nScale, RoundingMode);
        return bigDecimal.floatValue();
    }

    /**
     * 返回指定小数位 的浮点型数据 四舍五入
     * http://www.cnblogs.com/chenssy/p/3366632.html
     * @param num
     * @param nScale 保留小数位数
     * @return
     */
    public static float getNumFixScale(double num,int nScale) {
        return getNumFixScale(num, nScale, RoundingMode.HALF_UP);
    }
}
