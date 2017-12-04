package com.blueprint.http;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

import static com.blueprint.Consistent.ErrorCode.CONNECT404;
import static com.blueprint.Consistent.ErrorCode.ERROR_DATA;
import static com.blueprint.Consistent.ErrorCode.ERROR_NETERROR;

/**
 * @another 江祖赟
 * @date 2017/7/14.
 */
public class ExceptionEngine {
    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ServerException handleException(Throwable e){
        ServerException ex;
        if(e instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException)e;
            return new ServerException(httpException.code(), "网络错误", e.getCause());
        }else if(e instanceof ServerException) {    //服务器返回的错误
            return (ServerException)e;
        }else if(e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException || e instanceof MalformedJsonException) {
            return new ServerException(ERROR_DATA, "数据解析异常，查看json数据");
        }else if(e instanceof ConnectException) {
            return new ServerException(CONNECT404, "连接失败");
        }else {
            return new ServerException(ERROR_NETERROR, "未知错误", e.getCause());
        }
    }
}
