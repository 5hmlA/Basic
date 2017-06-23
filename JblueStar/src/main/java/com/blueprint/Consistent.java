package com.blueprint;

public class Consistent {
    public static final String DEFAULTSTR = "--";
    public static final int DEFAULTERROR = -12306;
    public static interface ErrorCode {
        int HTTP404 = -404;
        int CONNECT404 = 404;
        /**
         * 数据为空
         */
        int ERROR_EMPTY = 0;
        /**
         * 没有网络
         */
        int ERROR_NONET = 1;
        /**
         * 网络错误
         */
        int ERROR_NETERROR = 2;
        /**
         * 数据异常
         */
        int ERROR_DATA = 3;
    }
}
