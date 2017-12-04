package com.blueprint.luban;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import com.blueprint.LibApp;
import com.blueprint.helper.LogHelper;
import com.blueprint.rx.RxUtill;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class Luban {
    private static final String TAG = "Luban";
    private static final String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";

    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;

    private String mTargetDir;
    private List<String> mPaths;
    private int mLeastCompressSize;
    //  private OnCompressListener mCompressListener;

    //  private Handler mHandler;

    private Luban(Builder builder){
        this.mPaths = builder.mPaths;
        this.mTargetDir = builder.mTargetDir;
        //    this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        //    mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(){
        return new Builder();
    }

    /**
     * Returns a mFile with a cache audio name in the private cache directory.
     */
    private File getImageCacheFile(String suffix){
        if(TextUtils.isEmpty(mTargetDir)) {
            mTargetDir = getImageCacheDir().getAbsolutePath();
        }

        String cacheBuilder = mTargetDir+"/"+System.currentTimeMillis()+(int)( new Random().nextInt()*1000 )+( TextUtils
                .isEmpty(suffix) ? ".jpg" : suffix );

        return new File(cacheBuilder);
    }

    /**
     * Returns a directory with a default name in the private cache directory of the application to
     * use to store retrieved audio.
     *
     * @see #getImageCacheDir(String)
     */
    @Nullable
    private File getImageCacheDir(){
        return getImageCacheDir(DEFAULT_DISK_CACHE_DIR);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     * A context.
     *
     * @param cacheName
     *         The name of the subdirectory in which to store the cache.
     * @see #getImageCacheDir()
     */
    @Nullable
    private File getImageCacheDir(String cacheName){
        File cacheDir = LibApp.getContext().getExternalCacheDir();
        if(cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if(!result.mkdirs() && ( !result.exists() || !result.isDirectory() )) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        if(Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }

    /**
     * start asynchronous compress thread
     */
    private Observable<File> launch(){
        return Observable.<File>create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(final ObservableEmitter<File> e) throws Exception{
                if(mPaths == null || mPaths.size() == 0) {
                    e.onError(new NullPointerException("image file cannot be null"));
                    e.onComplete();
                }else {
                    Iterator<String> iterator = mPaths.iterator();
                    while(iterator.hasNext()) {
                        final String path = iterator.next();
                        if(Checker.isImage(path)) {
                            LogHelper.slog_d(TAG, "开始压缩=====");
                            File result = Checker.isNeedCompress(mLeastCompressSize, path) ? new Engine(path,
                                    getImageCacheFile(Checker.checkSuffix(path))).compress() : new File(path);
                            e.onNext(result);
                        }else {
                            Log.e(TAG, "can not read the path : "+path);
                        }
                        iterator.remove();
                    }
                    e.onComplete();
                }
            }
        }).compose(RxUtill.<File>defaultSchedulers_obser());

    }

    /**
     * start compress and return the mFile
     */
    @WorkerThread
    private Observable<File> get(final String path){
        return Observable.<File>create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(final ObservableEmitter<File> e) throws Exception{
                if(mPaths == null || mPaths.size() == 0) {
                    e.onNext(null);
                    //                    e.onError(new NullPointerException("image file cannot be null"));
                }
                e.onNext(new Engine(path, getImageCacheFile(Checker.checkSuffix(path))).compress());
                e.onComplete();
            }
        }).compose(RxUtill.<File>defaultSchedulers_obser());

    }

    private Observable<List<File>> get(){
        return Observable.<List<File>>create(new ObservableOnSubscribe<List<File>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<File>> e) throws Exception{
                if(mPaths == null || mPaths.size() == 0) {
                    e.onNext(new ArrayList<File>());
                    e.onComplete();
                }else {
                    List<File> results = new ArrayList<>();
                    Iterator<String> iterator = mPaths.iterator();

                    while(iterator.hasNext()) {
                        String path = iterator.next();
                        if(Checker.isImage(path)) {
                            results.add(new Engine(path, getImageCacheFile(Checker.checkSuffix(path))).compress());
                        }else {
                            results.add(new File(path));
                        }
                        iterator.remove();
                    }
                    e.onNext(results);
                    e.onComplete();
                }
            }
        }).compose(RxUtill.<List<File>>defaultSchedulers_obser());
    }

    public static class Builder {
        private String mTargetDir;
        private List<String> mPaths;
        private int mLeastCompressSize = 100;

        Builder(){
            this.mPaths = new ArrayList<>();
        }

        private Luban build(){
            return new Luban(this);
        }

        public Builder load(File file){
            this.mPaths.add(file.getAbsolutePath());
            return this;
        }

        public Builder load(String string){
            this.mPaths.add(string);
            return this;
        }

        public Builder load(List<String> list){
            this.mPaths.addAll(list);
            return this;
        }

        public Builder putGear(int gear){
            return this;
        }

        public Builder setTargetDir(String targetDir){
            this.mTargetDir = targetDir;
            return this;
        }

        /**
         * do not compress when the origin image file size less than one value
         *
         * @param size
         *         the value of file size, unit KB, default 100K
         */
        public Builder ignoreBy(int size){
            this.mLeastCompressSize = size;
            return this;
        }

        /**
         * 压缩单张或者多张图片 依次通知
         */
        public Observable<File> launch(){
            return build().launch();
        }

        /**
         * 一张图片 压缩
         *
         * @param path
         * @return
         */
        public Observable<File> get(String path){
            return build().get(path);
        }

        /**
         * 压缩单张或者多张图片 通知一次
         *
         * @return the thumb image file list
         */
        public Observable<List<File>> get(){
            return build().get();
        }
    }
}