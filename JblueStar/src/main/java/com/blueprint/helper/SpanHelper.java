package com.blueprint.helper;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blueprint.LibApp;
import com.blueprint.helper.spannable.ClickMovementMethod;
import com.blueprint.helper.spannable.OnSpanClickListener;
import com.blueprint.helper.spannable.SpannableClickable;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.blueprint.helper.RegexHelper.safeRegex;
import static com.blueprint.helper.RegexHelper.safeSearchRegex;

/**
 * 只要把要保持原样的内容 放在<pre>  </pre>标签之内，就会保持原样输出。
 */
public class SpanHelper {
    public static final int TAG_CLICK_SPAN = 0x13013313;

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
     * </pre>
     * <li>%s   字符串占位符
     * <li>%d   int占位符
     * <li>%f   float占位符
     *
     * @param view
     *         为textview的时候 直接设置给textview
     * @param idRes
     * @param o
     * @return
     */
    public static String formatString(@NonNull View view, int idRes, Object... o){
        String string = String.format(view.getContext().getResources().getString(idRes), o);
        if(!TextUtils.isEmpty(string) && view instanceof TextView) {
            ( (TextView)view ).setText(string);
        }
        return string;
    }

    public static String getFString(int idRes, Object... objects){
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
        return getSynchysis(view, new SpannableString(orignmsg), from, what);
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
        try {
            Pattern pattern = Pattern.compile(from);
            Matcher matcher = pattern.matcher(orignmsg);
            while(matcher.find()) {
                orignmsg.setSpan(what, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }catch(PatternSyntaxException e) {
            LogHelper.Log_e(Log.getStackTraceString(e));
        }
        if(view instanceof TextView) {
            ( (TextView)view ).setText(orignmsg);
        }
        return orignmsg;
    }


    /**
     * 通过国际化 转 成字符串 然后 修改关键字样式
     *
     * @param view
     *         为textview的时候 直接设置给textview
     * @param keys
     *         关键字/匹配规则
     * @param idRes
     *         国际化资源
     * @param o
     *         内容
     * @param kstyleRes
     *         关键字样式
     * @param nstyRes
     *         其他内容样式
     * @return
     */
    public static SpannableString getSpanModify(View view, String keys, int nstyRes, int kstyleRes, int idRes, Object... o){
        return getSpanModify(view, keys, formatString(view, idRes, o), nstyRes, kstyleRes);
    }

    /**
     * @param view
     * @param keys
     * @param kstyleRes
     *         关键字显示样式
     * @param idRes
     *         字符串格式
     * @param o
     *         被格式的字符串
     * @return
     */
    public static SpannableString getSpanModify2(View view, Object keys, int kstyleRes, int idRes, Object... o){
        return getSpanModify2(view, keys.toString(), formatString(view, idRes, o), kstyleRes);
    }

    public static SpannableString getSpanModify2(String keys, String orign, int kstyleRes){
        return getSpanModify2(keys, new SpannableString(orign), kstyleRes);
    }

    /**
     * 通过spannable修饰字符串
     *
     * @param keys
     *         关键字
     * @param spannableString
     *         源字符串
     * @param kstyleRes
     *         关键字样式
     * @return
     */
    public static SpannableString getSpanModify2(String keys, SpannableString spannableString, int kstyleRes){
        return getSpanModify(null, keys, spannableString, 0, kstyleRes);
    }

    /**
     * 通过spannable修饰字符串
     *
     * @param textview
     *         为textview的时候 直接设置给textview
     * @param keys
     *         关键字
     * @param orign
     *         源字符串
     * @param kstyleRes
     *         关键字样式
     * @return
     */
    public static SpannableString getSpanModify2(View textview, String keys, String orign, int kstyleRes){
        SpannableString spannableString = getSpanModify2(keys, orign, kstyleRes);
        if(textview instanceof TextView) {
            ( (TextView)textview ).setText(spannableString);
        }
        return spannableString;
    }

    /**
     * 通过spannable修饰字符串
     *
     * @param view
     *         为textview的时候 直接设置给textview
     * @param keys
     *         关键字
     * @param orign
     *         源字符串
     * @param kstyleRes
     *         关键字样式
     * @param nstyRes
     *         非关键字样式
     * @return
     */
    public static SpannableString getSpanModify(View view, String keys, String orign, int nstyRes, int kstyleRes){
        return getSpanModify(view, keys, new SpannableString(orign), nstyRes, kstyleRes);
    }

    /**
     * 通过spannable修饰字符串
     * <h1 color="red">在原有spannable样式的基础上 添加新关键字样式
     *
     * @param view
     *         为textview的时候 直接设置给textview
     * @param keys
     *         关键字
     * @param orign
     *         源字符串
     * @param kstyleRes
     *         关键字样式
     * @param nstyRes
     *         非关键字样式
     * @return
     */
    public static SpannableString getSpanModify(View view, String keys, SpannableString orign, int nstyRes, int kstyleRes){
        Context context = LibApp.getContext();
        if(nstyRes != 0) {
            orign.setSpan(new TextAppearanceSpan(context, nstyRes), 0, orign.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        try {
            //关键字高亮
            Pattern p = Pattern.compile(keys, Pattern.CASE_INSENSITIVE);//启用不区分大小写的匹配。
            Matcher m = p.matcher(orign);
            while(m.find()) {
                orign.setSpan(new TextAppearanceSpan(context, kstyleRes), m.start(), m.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }catch(PatternSyntaxException e) {
            LogHelper.Log_e(Log.getStackTraceString(e));
        }
        if(view instanceof TextView) {
            ( (TextView)view ).setText(orign);
        }
        return orign;
    }


    /**
     * 通过spannable修饰字符串
     * <h1> 多个关键字 对应多个 关键样式</h1>
     *
     * @param view
     *         为textview的时候 直接设置给textview
     * @param keys
     *         关键字/匹配规则
     * @param orign
     *         源字符串
     * @param kstyleRes
     *         关键字样式
     * @param nstyRes
     *         非关键字样式
     * @return
     */
    public static SpannableString getSpanModify(View view, String[] keys, String orign, int nstyRes, int... kstyleRes){
        SpannableString spannableString = new SpannableString(orign);
        Context context = view.getContext().getApplicationContext();
        if(nstyRes != 0) {
            spannableString.setSpan(new TextAppearanceSpan(context, nstyRes), 0, spannableString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        try {
            for(int i = 0; i<keys.length; i++) {
                String key = keys[i];
                //关键字高亮
                int kstyle = 0;
                if(i<kstyleRes.length) {
                    kstyle = kstyleRes[i];
                }else {
                    kstyle = kstyleRes[i-1];
                }
                spannableString = getSpanModify2(key, spannableString, kstyle);
            }
        }catch(PatternSyntaxException e) {
            LogHelper.Log_e(Log.getStackTraceString(e));
        }

        if(view instanceof TextView) {
            ( (TextView)view ).setText(spannableString);
        }
        return spannableString;
    }

    //==================================================================================================
    public static CharSequence getSearchHightLightStr(String orign, String key, int keyColor){
        if(!TextUtils.isEmpty(orign)) {
            return searchHightLightStrParser(new SpannableString(orign), key, keyColor);
        }else {
            return StrHelper.str2CharSequence(orign);
        }
    }

    /**
     * 完整匹配
     *
     * @param orign
     * @param key
     * @param keyColor
     * @return
     */
    public static CharSequence getHightLightStr(String orign, String key, int keyColor){
        if(!TextUtils.isEmpty(orign)) {
            return hightLightStrParser(new SpannableString(orign), safeRegex(key), keyColor);
        }else {
            return StrHelper.str2CharSequence(orign);
        }
    }

    private CharSequence setRedKeyword(String name, String keyword){
        String[] split = Arrays
                .copyOfRange(( keyword.toLowerCase()+keyword.toUpperCase() ).split(""), 1, 2*keyword.length()+1);
        String colorText[] = new String[split.length];
        for(int i = 0; i<colorText.length; i++) {
            colorText[i] = ""+(char)( 0xF000+i );
        }

        for(int i = 0; i<split.length; i++) {
            name = name.replace(split[i], colorText[i]);
        }

        for(String s : colorText) {
            name = name.replace(s, "<font color='red'>"+s+"</font>");
        }

        for(int i = 0; i<colorText.length; i++) {
            name = name.replace(colorText[i], split[i]);
        }

        Spanned spanned = Html.fromHtml(name);

        return spanned;
    }

    /**
     * 搜索用关键字高亮  支持关键字拆分匹配
     *
     * @param orign
     * @param key
     * @param keyColor
     * @return
     */
    public static SpannableString searchHightLightStrParser(@NonNull SpannableString orign, String key, int keyColor){
        return hightLightStrParser(orign, safeSearchRegex(key), keyColor);
    }

    /**
     * 严格匹配关键字 不拆分
     *
     * @param orign
     * @param key
     *         关键字如果是 转义符 * [ 会出异常 {@link RegexHelper#safeSearchRegex(key)} {@link RegexHelper#safeRegex(key)} 建议 包装安全key
     * @param keyColor
     * @return
     */
    public static SpannableString hightLightStrParser(@NonNull SpannableString orign, String key, int keyColor){
        if(CheckHelper.checkStrings(orign, key)) {
            try {
                Pattern p = Pattern.compile(key);
                Matcher m = p.matcher(orign);
                while(m.find()) {
                    int start = m.start();
                    int end = m.end();
                    if(!TextUtils.isEmpty(m.group())) {
                        orign.setSpan(new ForegroundColorSpan(keyColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }catch(PatternSyntaxException e) {
                LogHelper.Log_e(Log.getStackTraceString(e));
            }
        }
        return orign;
    }

    public static void setSearchHightLightStrTextView(TextView textView, String orign,
                                                      @Nullable String key, int keyColor){
        textView.setText(getSearchHightLightStr(orign, key, keyColor));
    }

    public static void setHightLightStrTextView(TextView textView, String orign, @Nullable String key, int keyColor){
        textView.setText(getHightLightStr(orign, key, keyColor));
    }

    /**
     * @param keyColor
     *         关键字颜色
     * @param orign
     *         文本
     * @param keys
     *         多个关键字
     * @return
     */
    public static CharSequence getSearchHightLightStr(@NonNull String orign, int keyColor, @NonNull String... keys){
        SpannableString s = new SpannableString(orign);
        for(String key : keys) {
            searchHightLightStrParser(s, key, keyColor);
        }
        return s;
    }

    public static TextView wrapperClickMovement(TextView textView,
                                                @ColorInt int clickableSpanBgClor, @ColorInt int textViewBgColor){
        final ClickMovementMethod circleMovementMethod = new ClickMovementMethod(clickableSpanBgClor, textViewBgColor);
        textView.setMovementMethod(circleMovementMethod);
        return textView;
    }

    public static void configTextViewClickSpan(
            @NonNull final TextView textView,
            @NonNull final OnSpanClickListener spanClickListener,
            @NonNull final String clickText, @ColorInt int clickTextColor, @ColorInt int clickableSpanBgClor){
        configTextViewClickSpan(textView, spanClickListener, clickText, clickTextColor, clickableSpanBgClor,
                clickableSpanBgClor);
    }

    public static void configTextViewClickSpan(
            @NonNull final TextView textView,
            @NonNull final OnSpanClickListener spanClickListener,
            @NonNull final String clickText,
            @ColorInt int clickTextColor, @ColorInt int clickableSpanBgClor, @ColorInt int textViewBgColor){
        if(textView != null && !TextUtils.isEmpty(clickText)) {
            textView.setVisibility(View.VISIBLE);
            SpannableString clickpanText = clickSpanTextParse(textView, spanClickListener, clickText, clickTextColor);
            wrapperClickMovement(textView, clickableSpanBgClor, textViewBgColor).setText(clickpanText);
        }else {
            textView.setVisibility(View.GONE);
        }
    }

    @NonNull
    public static SpannableString clickSpanTextParse(
            @NonNull final TextView textView,
            @NonNull final OnSpanClickListener spanClickListener,
            @NonNull final String clickText, @ColorInt final int clickTextColor){
        SpannableString clickpanText = new SpannableString(clickText);
        if(!TextUtils.isEmpty(clickText)) {
            clickpanText.setSpan(new SpannableClickable(clickTextColor) {
                @Override
                public void onClick(View widget){
                    //textview的clicklistener出需要把TAG_CLICK_SPAN设置为false
                    textView.setTag(TAG_CLICK_SPAN, true);
                    if(spanClickListener != null) {
                        spanClickListener.onSpanClick(clickText);
                    }
                }
            }, 0, clickpanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return clickpanText;
    }

}
