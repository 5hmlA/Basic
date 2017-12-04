package com.blueprint.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.blueprint.rx.RxBus;
import com.blueprint.rx.RxUtill;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @another 江祖赟
 * @date 2017/11/2 0002.
 */
public class JWebView extends WebView implements ObservableOnSubscribe<Object> {
    private ObservableEmitter<Object> mE;
    private Disposable mSubscribe;
    //webview显示了，画完了
    public static final String WEBVIEWSHOWED = "webviewshowed_drawfinish";

    public JWebView(Context context){
        super(context);
    }

    public JWebView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public JWebView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        mSubscribe = Observable.create(this).debounce(666, TimeUnit.MILLISECONDS)
                .compose(RxUtill.defaultSchedulers_obser()).subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception{
                        RxBus.getInstance().post(WEBVIEWSHOWED);
                        mSubscribe.dispose();
                    }
                });
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(mE != null) {
            mE.onNext("onDraw");
        }
    }

    @Override
    public void subscribe(ObservableEmitter<Object> e) throws Exception{
        mE = e;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        if(mSubscribe != null) {
            mSubscribe.dispose();
        }
    }
}
