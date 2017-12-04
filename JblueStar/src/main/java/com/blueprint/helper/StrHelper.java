package com.blueprint.helper;

import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.blueprint.Consistent.SPLIT_DOS;

/**
 * String Utils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class StrHelper {

    private StrHelper(){
        throw new AssertionError();
    }


    /**
     * is null or its length is 0 or it is made by space
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     *
     * @return if string is null or its size is 0 or it is made by space, return true, else return false.
     */
    public static boolean isBlank(String str){
        return ( str == null || str.trim().length() == 0 );
    }


    /**
     * is null or its length is 0
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     *
     * @return if string is null or its size is 0, return true, else return false.
     */
    public static boolean isEmpty(CharSequence str){
        return ( str == null || str.length() == 0 );
    }


    /**
     * compare two string
     */
    public static boolean isEquals(String actual, String expected){
        return CheckHelper.checkObjects(actual, expected) && expected.equals(actual);
    }


    /**
     * get length of CharSequence
     * <pre>
     * length(null) = 0;
     * length(\"\") = 0;
     * length(\"abc\") = 3;
     * </pre>
     *
     * @return if str is null or empty, return 0, else return {@link CharSequence#length()}.
     */
    public static int length(CharSequence str){
        return str == null ? 0 : str.length();
    }


    /**
     * null Object to empty string
     * <pre>
     * safeObject2Str(null) = &quot;&quot;;
     * safeObject2Str(&quot;&quot;) = &quot;&quot;;
     * safeObject2Str(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     */
    public static String safeObject2Str(Object str){
        return ( str == null ? "" : str.toString() );
    }


    /**
     * capitalize first letter
     * <pre>
     * capitalizeFirstLetter(null)     =   null;
     * capitalizeFirstLetter("")       =   "";
     * capitalizeFirstLetter("2ab")    =   "2ab"
     * capitalizeFirstLetter("a")      =   "A"
     * capitalizeFirstLetter("ab")     =   "Ab"
     * capitalizeFirstLetter("Abc")    =   "Abc"
     * </pre>
     */
    public static String capitalizeFirstLetter(String str){
        if(isEmpty(str)) {
            return str;
        }
        char c = str.charAt(0);
        return ( !Character.isLetter(c) || Character.isUpperCase(c) ) ? str : new StringBuilder(str.length())
                .append(Character.toUpperCase(c)).append(str.substring(1)).toString();
    }


    /**
     * encoded in utf-8
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     *
     * @throws UnsupportedEncodingException
     *         if an error occurs
     */
    public static String utf8Encode(String str){
        if(!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            }catch(UnsupportedEncodingException e) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }


    /**
     * encoded in utf-8, if exception, return defultReturn
     */
    public static String utf8Encode(String str, String defultReturn){
        if(!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            }catch(UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }


    /**
     * get innerHtml from href
     * <pre>
     * getHrefInnerHtml(null)                                  = ""
     * getHrefInnerHtml("")                                    = ""
     * getHrefInnerHtml("mp3")                                 = "mp3";
     * getHrefInnerHtml("&lt;a innerHtml&lt;/a&gt;")                    = "&lt;a innerHtml&lt;/a&gt;";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com"&gt;innerHtml&lt;/a&gt;")               = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com" title="baidu"&gt;innerHtml&lt;/a&gt;") = "innerHtml";
     * getHrefInnerHtml("   &lt;a&gt;innerHtml&lt;/a&gt;  ")                           = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                      = "innerHtml";
     * getHrefInnerHtml("jack&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                  = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml1&lt;/a&gt;&lt;a&gt;innerHtml2&lt;/a&gt;")        = "innerHtml2";
     * </pre>
     *
     * @return <ul>
     * <li>if href is null, return ""</li>
     * <li>if not match regx, return source</li>
     * <li>return the last string that match regx</li>
     * </ul>
     */
    public static String getHrefInnerHtml(String href){
        if(isEmpty(href)) {
            return "";
        }

        String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
        Pattern hrefPattern = Pattern.compile(hrefReg, Pattern.CASE_INSENSITIVE);
        Matcher hrefMatcher = hrefPattern.matcher(href);
        if(hrefMatcher.matches()) {
            return hrefMatcher.group(1);
        }
        return href;
    }


    /**
     * process special char in HTML
     * <pre>
     * htmlEscapeCharsToString(null) = null;
     * htmlEscapeCharsToString("") = "";
     * htmlEscapeCharsToString("mp3") = "mp3";
     * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
     * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
     * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
     * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
     * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
     * </pre>
     */
    public static String htmlEscapeCharsToString(String source){
        return StrHelper.isEmpty(source) ? source : source.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
    }


    /**
     * 转化为半角字符
     * <pre>
     * fullWidthToHalfWidth(null) = null;
     * fullWidthToHalfWidth("") = "";
     * fullWidthToHalfWidth(new String(new char[] {12288})) = " ";
     * fullWidthToHalfWidth("！＂＃＄％＆) = "!\"#$%&";
     * </pre>
     */
    public static String fullWidthToHalfWidth(String s){
        if(isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for(int i = 0; i<source.length; i++) {
            if(source[i] == 12288) {
                source[i] = ' ';
                // } else if (source[i] == 12290) {
                // source[i] = '.';
            }else if(source[i]>=65281 && source[i]<=65374) {
                source[i] = (char)( source[i]-65248 );
            }else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }


    /**
     * 转化为全角字符
     * <pre>
     * halfWidthToFullWidth(null) = null;
     * halfWidthToFullWidth("") = "";
     * halfWidthToFullWidth(" ") = new String(new char[] {12288});
     * halfWidthToFullWidth("!\"#$%&) = "！＂＃＄％＆";
     * </pre>
     */
    public static String halfWidthToFullWidth(String s){
        if(isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for(int i = 0; i<source.length; i++) {
            if(source[i] == ' ') {
                source[i] = (char)12288;
                // } else if (source[i] == '.') {
                // source[i] = (char)12290;
            }else if(source[i]>=33 && source[i]<=126) {
                source[i] = (char)( source[i]+65248 );
            }else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    @Nullable
    public static CharSequence str2CharSequence(String orign){
        if(TextUtils.isEmpty(orign)) {
            return null;
        }else {
            return new SpannableString(orign);
        }
    }

    public static String buildStrArrays2Str(String[] clickText){
        StringBuilder stringBuilder = new StringBuilder();
        if(CheckHelper.checkArrays(clickText)) {
            for(String s : clickText) {
                stringBuilder.append(s).append(SPLIT_DOS);
            }
            return stringBuilder.subSequence(0, stringBuilder.length()-SPLIT_DOS.length()).toString();
        }else {
            return "啥都没有";
        }
    }
}
