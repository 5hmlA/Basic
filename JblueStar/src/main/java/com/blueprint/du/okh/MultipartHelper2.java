package com.blueprint.du.okh;

import android.widget.Toast;

import com.blueprint.LibApp;
import com.blueprint.du.DownloadCell;
import com.blueprint.helper.LogHelper;
import com.blueprint.rx.RxUtill;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @another 江祖赟
 * @date 2017/6/14.
 */
public class MultipartHelper2 {
    private static final String TAG = "MultipartHelper2";
    private static final long DEFAULT_TIMEOUT = 15;
    private String mDownloadUrl;
    private String mSaveName;
    private DownloadCell mDownloadCell;
    private ProgressListener mProgressListener;
    private long mStartsPoint;
    private File mDestFile;
    private Disposable mSubscribe;
    private final Retrofit mRetrofit;

    public MultipartHelper2(DownloadCell downloadCell, ProgressListener progressListener){
        mDownloadUrl = downloadCell.getDownUrl();
        mSaveName = downloadCell.getSaveName();
        mDownloadCell = downloadCell;
        mDestFile = mDownloadCell.getDestFile();
        if(mDestFile.exists()) {
            LogHelper.slog_e(TAG, "存储的目标文件存在，清除重新下载");
            mDestFile.delete();
        }
        mRetrofit = getRetrofit();
    }

    public MultipartHelper2(){
        mRetrofit = getRetrofit();
        LinkedBlockingDeque blockingDeque = new LinkedBlockingDeque();
    }

    private static class Inner {
        static MultipartHelper2 sInstance = new MultipartHelper2();
    }

    public static MultipartHelper2 getInstance(){
        return Inner.sInstance;
    }

    public void download(final DownloadCell downloadCell){
        mSubscribe = mRetrofit.create(MultipartService.class).download(downloadCell.getDownUrl())
                .compose(RxUtill.<ResponseBody>all_io_flow())
                .flatMap(new Function<ResponseBody,Publisher<DownloadCell>>() {
                    @Override
                    public Publisher<DownloadCell> apply(@NonNull final ResponseBody responseBody) throws Exception{
                        return Flowable.create(new FlowableOnSubscribe<DownloadCell>() {
                            @Override
                            public void subscribe(
                                    @NonNull FlowableEmitter<DownloadCell> flowableEmitter) throws Exception{
                                save(responseBody, flowableEmitter, downloadCell);

                            }
                        }, BackpressureStrategy.BUFFER);
                    }
                }).subscribe(new Consumer<DownloadCell>() {
                    @Override
                    public void accept(@NonNull DownloadCell responseBody) throws Exception{

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception{
                        mProgressListener.onFailure();
                    }
                });
    }

    public void downloadFrom(long startPoing){
        mStartsPoint = startPoing;
        mSubscribe = mRetrofit.create(MultipartService.class).downloadRange(mDownloadUrl, String.valueOf(startPoing))
                .compose(RxUtill.<ResponseBody>all_io_flow()).subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(@NonNull ResponseBody responseBody) throws Exception{
                        //                        save(responseBody, flowableEmitter, mStartsPoint);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception{
                        mProgressListener.onFailure();
                    }
                });
    }

    public void pause(DownloadCell downloadCell){
        cancel();
    }

    public void cancel(){
        if(mSubscribe != null) {
            mSubscribe.dispose();
        }
    }

    private void save(ResponseBody body, FlowableEmitter<DownloadCell> flowableEmitter, DownloadCell downloadCell){
        //想几个线程就几个线程 写入
        InputStream in = body.byteStream();
        downloadCell.setFileSize(body.contentLength());
        FileChannel channelOut = null;
        // 随机访问文件，可以指定断点续传的起始位置
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(mDestFile, "rwd");
            //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
            channelOut = randomAccessFile.getChannel();
            // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
            long startsPoint = downloadCell.getStartPoint();
            MappedByteBuffer mappedBuffer = channelOut
                    .map(FileChannel.MapMode.READ_WRITE, startsPoint, body.contentLength());
            byte[] buffer = new byte[1024];
            int len;
            while(!flowableEmitter.isCancelled() && ( len = in.read(buffer) ) != -1) {
                mappedBuffer.put(buffer, 0, len);
                startsPoint += len;
            }
            downloadCell.setDownloaded(startsPoint).setStartPoint(startsPoint);
        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
                if(channelOut != null) {
                    channelOut.close();
                }
                if(randomAccessFile != null) {
                    randomAccessFile.close();
                }
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Flowable getUplodFileSingle(String upUrl, File file){
        if(file != null && file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);

            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            return getRetrofit().create(MultipartService.class).upload(upUrl, body).compose(RxUtill.all_io_flow());

        }

        return Flowable.error(new RuntimeException("文件不存在"));
    }

    public void uploadFiles(String upUrl, File file){
        getUplodFileSingle(upUrl, file).subscribe(new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception{

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception{
                mProgressListener.onFailure();
            }
        });
    }

    public void uploadFiles(String ulr, List<String> imagesList){
        if(imagesList.size() == 0) {
            Toast.makeText(LibApp.getContext(), "不能不选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,RequestBody> files = new HashMap<>();

        MultipartService multipartService = getRetrofit().create(MultipartService.class);

        for(int i = 0; i<imagesList.size(); i++) {
            File file = new File(imagesList.get(i));
            files.put("file"+i+"\"; filename=\""+file.getName(),
                    RequestBody.create(MediaType.parse("application/otcet-stream"), file));
        }
        multipartService.upload(ulr, files).compose(RxUtill.defaultSchedulers_flow()).subscribe(new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception{

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception{
                mProgressListener.onFailure();
            }
        });
    }

    public Retrofit getRetrofit(){
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);

        return new Retrofit.Builder().baseUrl("http://www.gank.io/api/").client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
    }

}