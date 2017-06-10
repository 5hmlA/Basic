package com.blueprint.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;


import com.blueprint.LibApp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 只要把要保持原样的内容 放在<pre>  </pre>标签之内，就会保持原样输出。
 */
public class SpanHelper {

    /**
     * 整数
     */
    public static final String NUM_INTEGER = "\\d";
    /**
     * 小数 包括 整数
     */
    public static final String NUM_FLOAT = "\\d+.\\d+|\\d+";

    /**
     * 国际化 不同国家排序不同
     * <pre>
     *      <string name="time_formart">%1$s\'%2$s\"</string>
     *      <string name="days">%1$s%% days</string>
     *
     * </pre>
     * <li>%s   字符串占位符
     * <li>%d   int占位符
     * <li>%f   float占位符
     *
     * @param view  为textview的时候 直接设置给textview
     * @param idRes
     * @param o
     * @return
     */
    public static String formatString(@NonNull View view, int idRes, Object... o) {
        String string = String.format(view.getContext().getResources().getString(idRes), o);
        if (!TextUtils.isEmpty(string) && view instanceof TextView) {
            ((TextView) view).setText(string);
        }
        return string;
    }

    public static String getFString(int idRes, Object... objects) {
        return String.format(LibApp.getContext().getString(idRes), objects);
    }
//
//    public static String getPString(Context context,int idRes, Object... objects) {
//        return String.format(context.getString(idRes), objects);
//    }

    /**
     * 替换 关键字 为某图片
     *
     * @param view
     *         为textview的时候 直接设置给textview
     * @param orignmsg
     * @param from
     *         关键字
     * @param what
     * @return
     */
    public static SpannableString getSynchysis(View view, String orignmsg, String from, Object what){
        SpannableString spannableString = new SpannableString(orignmsg);
        Pattern pattern = Pattern.compile(from);
        Matcher matcher = pattern.matcher(orignmsg);
        while(matcher.find()) {
            spannableString.setSpan(what, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if(view instanceof TextView) {
            ( (TextView)view ).setText(spannableString);
        }
        return spannableString;
    }

    /**
     * 替换 关键字 为某图片
     *
     * @param view
     *         为textview的时候 直接设置给textview
     * @param orignmsg
     * @param from
     *         关键字
     * @param what
     * @return
     */
    public static SpannableString getSynchysis(View view, SpannableString orignmsg, String from, Object what){
        Pattern pattern = Pattern.compile(from);
        Matcher matcher = pattern.matcher(orignmsg);
        while(matcher.find()) {
            orignmsg.setSpan(what, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if(view instanceof TextView) {
            ( (TextView)view ).setText(orignmsg);
        }
        return orignmsg;
    }


    /**
     * 通过国际化 转 成字符串 然后 修改关键字样式
     * @param view      为textview的时候 直接设置给textview
     * @param keys      关键字/匹配规则
     * @param idRes     国际化资源
     * @param o         内容
     * @param kstyleRes 关键字样式
     * @param nstyRes   其他内容样式
     * @return
     */
    public static SpannableString getSpanModify(View view, String keys, int idRes, Object o, int nstyRes, int kstyleRes) {
        return getSpanModify(view, keys, formatString(view, idRes, o), nstyRes, kstyleRes);
    }

    /**
     * 通过spannable修饰字符串
     *
     * @param view      为textview的时候 直接设置给textview
     * @param keys      关键字
     * @param orign     源字符串
     * @param kstyleRes 关键字样式
     * @param nstyRes   非关键字样式
     * @return
     */
    public static SpannableString getSpanModify(View view, String keys, String orign, int nstyRes, int kstyleRes) {
        SpannableString spannableString = new SpannableString(orign);
        String s = spannableString.toString();
        Context context = view.getContext().getApplicationContext();
        if (nstyRes != 0) {
            spannableString.setSpan(new TextAppearanceSpan(context, nstyRes), 0, s.length(), Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //关键字高亮
        Pattern p = Pattern.compile(keys, Pattern.CASE_INSENSITIVE);//启用不区分大小写的匹配。
        Matcher m = p.matcher(spannableString);
        while (m.find()) {
            spannableString.setSpan(new TextAppearanceSpan(context, kstyleRes), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (view instanceof TextView) {
            ((TextView) view).setText(spannableString);
        }
        return spannableString;
    }
    /**
     * 通过spannable修饰字符串
     * <h1 color="red">在原有spannable样式的基础上 添加新关键字样式
     *
     * @param view      为textview的时候 直接设置给textview
     * @param keys      关键字
     * @param orign     源字符串
     * @param kstyleRes 关键字样式
     * @param nstyRes   非关键字样式
     * @return
     */
    public static SpannableString getSpanModify(View view, String keys, SpannableString orign, int nstyRes, int kstyleRes) {
        String s = orign.toString();
        Context context = view.getContext().getApplicationContext();
        if (nstyRes != 0) {
            orign.setSpan(new TextAppearanceSpan(context, nstyRes), 0, s.length(), Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //关键字高亮
        Pattern p = Pattern.compile(keys, Pattern.CASE_INSENSITIVE);//启用不区分大小写的匹配。
        Matcher m = p.matcher(orign);
        while (m.find()) {
            orign.setSpan(new TextAppearanceSpan(context, kstyleRes), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (view instanceof TextView) {
            ((TextView) view).setText(orign);
        }
        return orign;
    }


    /**
     * 通过spannable修饰字符串
     * <h1> 多个关键字 对应多个 关键样式</h1>
     * @param view      为textview的时候 直接设置给textview
     * @param keys      关键字/匹配规则
     * @param orign     源字符串
     * @param kstyleRes 关键字样式
     * @param nstyRes   非关键字样式
     * @return
     */
    public static SpannableString getSpanModify(View view, String[] keys, String orign, int nstyRes, int... kstyleRes) {
        SpannableString spannableString = new SpannableString(orign);
        Context context = view.getContext().getApplicationContext();
        if (nstyRes != 0) {
            spannableString.setSpan(new TextAppearanceSpan(context, nstyRes), 0, spannableString.length(), Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            //关键字高亮
            int kstyle = 0;
            if (i < kstyleRes.length) {
                kstyle = kstyleRes[i];
            } else {
                kstyle = kstyleRes[i - 1];
            }

            //关键字高亮
            Pattern p = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(spannableString);
            while (m.find()) {
                spannableString.setSpan(new TextAppearanceSpan(context, kstyle), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (view instanceof TextView) {
            ((TextView) view).setText(spannableString);
        }
        return spannableString;
    }

    public static SpannableString getSpanModify2(View view, String[] keys, String orign, int nstyRes, int... kstyleRes) {
        SpannableString spannableString = new SpannableString(orign);
        Context context = view.getContext().getApplicationContext();
        if (nstyRes != 0) {
            spannableString.setSpan(new TextAppearanceSpan(context, nstyRes), 0, spannableString.length(), Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            //关键字高亮
            int kstyle = 0;
            if (i < kstyleRes.length) {
                kstyle = kstyleRes[i];
            } else {
                kstyle = kstyleRes[i - 1];
            }

            getSpanModify(view, key, spannableString, nstyRes, kstyle);
        }
        if (view instanceof TextView) {
            ((TextView) view).setText(spannableString);
        }
        return spannableString;
    }

}
