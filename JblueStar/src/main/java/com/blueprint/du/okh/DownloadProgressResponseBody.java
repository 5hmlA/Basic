package com.blueprint.du.okh;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

import static com.blueprint.helper.StrHelper.safeObject2Str;

/**
 * @another 江祖赟
 * @date 2017/6/14.
 */
public class DownloadProgressResponseBody extends ResponseBody {

    private String mUrl;
    //实际相应体
    private ResponseBody mResponseBody;
    private ProgressListener mProgressListeners;
    //包装BufferedSource计算进度
    private BufferedSource mBufferedSource;

    public DownloadProgressResponseBody(String url, ResponseBody responseBody, ProgressListener progressListener){
        mUrl = safeObject2Str(url);
        mResponseBody = responseBody;
        mProgressListeners = progressListener;
    }

    @Override
    public MediaType contentType(){
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength(){
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source(){
        if(mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(BufferedSource source){
        return new ForwardingSource(source) {
            long mTotalDownloaded = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException{
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                //如果读取完成了bytesRead会返回-1
                mTotalDownloaded += bytesRead != -1 ? bytesRead : 0;
                if(null != mProgressListeners) {
                    mProgressListeners
                            .onProgress(mTotalDownloaded, mResponseBody.contentLength(), bytesRead == -1, mUrl);
                }
                return bytesRead;
            }
        };

    }

}
