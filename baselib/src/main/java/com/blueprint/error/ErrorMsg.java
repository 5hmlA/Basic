package com.blueprint.error;

public class ErrorMsg {
    public static final String DEFAULTSTR = "--";
    public static interface ErrorCode {
        int HTTP404 = -404;
        int CONNECT404 = 404;
    }
}
