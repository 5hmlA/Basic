package com.zuyun.blueprint;

/**
 * @author 江祖赟.
 * @date 2017/6/9
 * @des [一句话描述]
 */
public interface Constants {
    public interface ErrorCode {
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
