package com.blueprint.helper;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by _SOLID
 * Date:2016/5/10
 * Time:10:36
 */
public class RegexHelper {

    public static final String SPLIT_DOT = "\\.";
    public static final String SPLIT_FLAG = "\\|";
    public static final String REGEX_NUM = "[0-9]*";

    /**
     * java.util.regex.PatternSyntaxExceptionDangling异常
     * Pattern.compile(key)key如果为转义符会出现异常
     * 异常，原因是*为转义字符，注意在截取自断时加‘/’或者‘[]’
     * 如：
     * String[] rs=txt.split("[*]");支持拆分匹配
     * 或：
     * String[] rs=txt.split("/*");
     * 即可；
     * @param key
     * @return
     */
    public static String safeSearchRegex(String key){
        //拆分
        return new StringBuilder("[").append(key).append("]").toString();
    }

    /**
     * 安全匹配key 完整匹配
     * @param key
     * @return
     */
    public static String safeRegex(String key){
        //完整
        return new StringBuilder("(").append(key).append(")").toString();
    }
    /**
     * 判断email格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断是不是合法手机号码
     */
    public static boolean isPhoneNumber(String handset){
        try {
            //todo
            if(TextUtils.isEmpty(handset) || handset.length() != 11 || !handset.startsWith("1")) {
                return false;
            }
            String check = "^[0123456789]+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(handset);
            boolean isMatched = matcher.matches();
            if(isMatched) {
                return true;
            }else {
                return false;
            }
        }catch(RuntimeException e) {
            return false;
        }
    }

    /**
     * 判断输入的字符串是否为纯汉字
     *
     * @param str
     *         传入的字符窜
     * @return 如果是纯汉字返回true, 否则返回false
     */
    public static boolean isChinese(String str){
        Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if(isNum.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为整数
     *
     * @param str
     *         传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str){
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否为浮点数，包括double和float
     *
     * @param str
     *         传入的字符串
     * @return 是浮点数返回true, 否则返回false
     */
    public static boolean isDouble(String str){
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
}
