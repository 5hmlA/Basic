package com.blueprint.du.okh;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @another 江祖赟
 * @date 2017/6/14.
 * http://blog.csdn.net/sbsujjbcy/article/details/48194701
 */
public class UploadProgressRequestBody extends RequestBody {
    //实际请求体
    private RequestBody mRequestBody;
    private ProgressListener mUploadListener;
    //包装完成的BufferedSink
    private BufferedSink bufferedSink;
    private long mContentLength;

    public UploadProgressRequestBody(RequestBody requestBody, ProgressListener uploadListener){
        mRequestBody = requestBody;
        mUploadListener = uploadListener;
    }

    @Override
    public MediaType contentType(){
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException{
        mContentLength = mRequestBody.contentLength();
        return mContentLength;
    }

    /**
     * 重写进行写入
     *
     * @param sink
     *         BufferedSink
     * @throws IOException
     *         异常
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException{
        if(bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        mRequestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();

    }

    /**
     * 写入，回调进度接口
     *
     * @param sink
     *         Sink
     * @return Sink
     */
    private Sink sink(Sink sink){
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException{
                super.write(source, byteCount);
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调
                if(mUploadListener != null) {
                    mUploadListener.onProgress(bytesWritten, mContentLength, bytesWritten == mContentLength);
                }
            }
        };
    }
}
