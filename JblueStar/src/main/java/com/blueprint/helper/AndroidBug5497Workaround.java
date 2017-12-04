package com.blueprint.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;

import static com.blueprint.helper.CheckHelper.checkObjects;

/**
 * @author yun.
 * @date 2017/8/20
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p>根据键盘的高度 动态修改跟布局 高</p>
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class AndroidBug5497Workaround {

    private static WeakReference<Activity> mActivityWeakReference;
    private WeakReference<View> mResizeViewReference;
    //监听android.R.id.content第一个子View的addOnGlobalLayoutListener方法，利用getWindowVisibleDisplayFrame获取可见面积，然后手动设置其高度来模拟adjustResize效果
    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.


    public static void assistActivity(Activity activity){
        mActivityWeakReference = new WeakReference<Activity>(activity);
        new AndroidBug5497Workaround(activity);
    }


    private ViewGroup.LayoutParams frameLayoutParams;
    Rect r = new Rect();

    ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout(){
            possiblyResizeChildOfContent();
        }
    };


    private AndroidBug5497Workaround(Activity safeActivity){
        this(safeActivity, ( (ViewGroup)safeActivity.findViewById(android.R.id.content) ).getChildAt(0));
    }


    private AndroidBug5497Workaround(Activity safeActivity, View resizeView){
        mResizeViewReference = new WeakReference<>(resizeView);
        if(checkObjects(mResizeViewReference, mActivityWeakReference) && checkObjects(mResizeViewReference.get(),
                mActivityWeakReference.get())) {
            //Rect rect = new Rect();
            //mContent.getGlobalVisibleRect(rect);
            //System.out.println("========getGlobalVisibleRect======="+rect.toString());
            //mContent.getLocalVisibleRect(rect);
            //System.out.println("========getLocalVisibleRect======="+rect.toString());
            //mContent.getWindowVisibleDisplayFrame(rect);
            //System.out.println(StatusBarHelper.getStatusBarHeight()
            //        +"========getWindowVisibleDisplayFrame======="+rect.toString());

            frameLayoutParams = mResizeViewReference.get().getLayoutParams();
            mActivityWeakReference.get().findViewById(android.R.id.content).getViewTreeObserver()
                    .addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
    }


    private void possiblyResizeChildOfContent(){
        if(checkObjects(mResizeViewReference, mActivityWeakReference) && checkObjects(mResizeViewReference.get(),
                mActivityWeakReference.get())) {
            int usableHeightNow = computeUsableHeight();
            if(usableHeightNow != frameLayoutParams.height) {
                frameLayoutParams.height = usableHeightNow;
                mResizeViewReference.get().requestLayout();
            }
        }
    }


    private int computeUsableHeight(){
        if(checkObjects(mResizeViewReference, mActivityWeakReference) && checkObjects(mResizeViewReference.get(),
                mActivityWeakReference.get())) {
            //可见部分
            mActivityWeakReference.get().findViewById(android.R.id.content).getWindowVisibleDisplayFrame(r);
        }
        return ( r.bottom-r.top );
    }


    public void release(){
        mActivityWeakReference.get().findViewById(android.R.id.content).getViewTreeObserver()
                .removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }
}
