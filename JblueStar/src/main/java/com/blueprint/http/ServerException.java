package com.blueprint.http;

/**
 * @another 江祖赟
 * @date 2017/7/14.
 */
public class ServerException extends RuntimeException {
    public int errCode;
    public String errMsg;

    public ServerException(int errCode, String errMsg, Throwable cause){
        super(errMsg, cause);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public ServerException(int errCode, String errMsg){
        super(errMsg, new RuntimeException("未指定错误原因"));
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public String toString(){
        return "ServerException{"+"errCode="+errCode+", errMsg='"+errMsg+'\''+'}';
    }
}
