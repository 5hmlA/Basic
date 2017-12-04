package com.blueprint.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;

import static com.blueprint.helper.CheckHelper.checkObjects;

/**
 * @author yun.
 * @date 2017/8/20
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p>根据键盘的高度 动态修改跟布局 高</p>
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class AndroidBug5497JRX {

    private ObservableEmitter<KeyboardHWraper> mE;
//    private Activity mActivity;
    private int orignButtom;
    private int dirtydata;
    private KeyboardHWraper mKeyboardHWraper = new KeyboardHWraper(0, 0);
    private Rect r = new Rect();

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout(){
            possiblyResizeChildOfContent();
        }
    };
    private WeakReference<Activity> mActivityWeakReference = new WeakReference<>(null);;

    private AndroidBug5497JRX(){
    }


    private static class Inner {
        static AndroidBug5497JRX mAndroidBug5497JRX = new AndroidBug5497JRX();
    }


    public static AndroidBug5497JRX getSingle(){
        return Inner.mAndroidBug5497JRX;
    }


    public static class KeyboardHWraper {
        public int height;
        public int topsition;


        public KeyboardHWraper(int height, int topsition){
            this.height = height;
            this.topsition = topsition;
        }


        @Override
        public String toString(){
            return "KeyboardHWraper{"+"height="+height+", topsition="+topsition+'}';
        }


        @Override
        public boolean equals(Object o){
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            KeyboardHWraper that = (KeyboardHWraper)o;

            if(height != that.height) {
                return false;
            }
            return topsition == that.topsition;
        }


        @Override
        public int hashCode(){
            int result = height;
            result = 31*result+topsition;
            return result;
        }
    }

    //监听android.R.id.content第一个子View的addOnGlobalLayoutListener方法，利用getWindowVisibleDisplayFrame获取可见面积，然后手动设置其高度来模拟adjustResize效果
    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.


    public Observable<KeyboardHWraper> watchingKeyboard(final Activity safeActivity){
        mActivityWeakReference = new WeakReference<>(safeActivity);
        return Observable.create(new ObservableOnSubscribe<KeyboardHWraper>() {

            @Override
            public void subscribe(final ObservableEmitter<KeyboardHWraper> e) throws Exception{
                mE = e;
                if(mActivityWeakReference.get() != null) {
                    if(mActivityWeakReference.get().findViewById(android.R.id.content) != null) {
                        mActivityWeakReference.get().findViewById(android.R.id.content).post(new Runnable() {
                            @Override
                            public void run(){
                                orignButtom = computeUsableButtom();
                            }
                        });
                        mActivityWeakReference.get().findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                    }
                }
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception{
                if(mActivityWeakReference.get().findViewById(android.R.id.content) != null) {
                    mActivityWeakReference.get().findViewById(android.R.id.content).getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
                    mE = null;
                }
            }
        });//distinct过滤重复数据,发送过就不发了112211只会发送12//.distinctUntilChanged()过滤连续的重复数据
    }


    private void possiblyResizeChildOfContent(){
        if(checkObjects(mE,mKeyboardHWraper,mActivityWeakReference.get())) {
            int usableButtomNow = computeUsableButtom();
            mKeyboardHWraper.height = orignButtom-usableButtomNow;
            if(dirtydata != mKeyboardHWraper.height) {
                mKeyboardHWraper.topsition = usableButtomNow;
                mE.onNext(mKeyboardHWraper);
                dirtydata = mKeyboardHWraper.height;
            }
        }
    }


    private int computeUsableButtom(){
        if(mActivityWeakReference.get() != null) {
            //可见部分
            mActivityWeakReference.get().findViewById(android.R.id.content).getWindowVisibleDisplayFrame(r);
        }
        return r.bottom;
    }
}
