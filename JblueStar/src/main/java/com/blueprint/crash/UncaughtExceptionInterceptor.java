package com.blueprint.crash;

/**
 * @another 江祖赟
 * @date 2017/8/26 0026.
 */
public interface UncaughtExceptionInterceptor {
    boolean onhandlerException(Thread thread, Throwable throwable);
}
