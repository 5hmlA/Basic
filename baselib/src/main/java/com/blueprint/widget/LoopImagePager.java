package com.blueprint.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blueprint.helper.DpHelper;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [rxjava实现自动滚动轮播图，控件可见重启，不可见关闭滚动]
 */
public class LoopImagePager extends RelativeLayout {
    private static final String TAG = LoopImagePager.class.getSimpleName();
    private static final long LOOPINTERVAL = 2;
    private ViewPager mViewPager;
    private Disposable mSubscribe;
    private onLoopImageClickListener mL;
    private ImagePagerAdapter mAdapter;
    private Rect mVisibleRect = new Rect();
    private boolean mMove;
    private PointF mLastMoved = new PointF();
    private int mTapSlop;

    public LoopImagePager(Context context){
        this(context, null);
    }

    public LoopImagePager(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public LoopImagePager(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mViewPager = new ViewPager(context);
        mTapSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        setClipChildren(false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = (int)DpHelper.dp2px(20);
        layoutParams.rightMargin = (int)DpHelper.dp2px(20);
        mViewPager.setLayoutParams(layoutParams);
        mViewPager.setPageTransformer(true, new MzTransformer());
        addView(mViewPager);

        //        indicatorView = new IndicatorView(mContext);
        //        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //        // 设置到屏幕的下边
        //        rlp.addRule(loopView.ALIGN_PARENT_BOTTOM);
        //        rlp.addRule(loopView.ALIGN_PARENT_RIGHT);
        //        // 设置点的背景图片
        //        indicatorView.setIndicatorDrawable(BaseApplication.findDrawable(R.drawable.indicator));
        //        // 设置点的间距
        //        rlp.setMargins(0, 0, 20, 10);
        //        indicatorView.setLayoutParams(rlp);


        post(new Runnable() {
            @Override
            public void run(){
                startLoop();
            }
        });
    }

    private void startLoop(){
        if(mSubscribe == null || mSubscribe.isDisposed()) {
            Log.d(TAG, "start loop ==============");
            mSubscribe = Observable.interval(LOOPINTERVAL, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception{
                            if(getLocalVisibleRect(mVisibleRect)) {
                                //可见
                                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                            }else {
                                //取消自动loop
                                stopLoop();
                            }
                        }
                    });
        }
    }

    private void stopLoop(){
        if(mSubscribe != null) {
            Log.d(TAG, "stop auto loop ==============");
            mSubscribe.dispose();
        }
    }

    public LoopImagePager setPagerAdapter(PagerAdapter pagerAdapter){
        mViewPager.setAdapter(pagerAdapter);
        return this;
    }

    public LoopImagePager setPagerData(List<String> pagerData){
        mAdapter = new ImagePagerAdapter(pagerData);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(20);
        if(mL != null) {
            mAdapter.setOnitemClickListener(mL);
        }
        return this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        //在列表中 上下滚动事件传不到 但是能够接收少量move事件
        //所以简单的down就停止不够，当上下滑动开启循环
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMoved.set(event.getX(), event.getY());
                mMove = false;
                stopLoop();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(event.getX()-mLastMoved.x)<Math.abs(event.getY()-mLastMoved.y)) {
                    //down的时候关了
                    startLoop();
                }else {
                    Log.d(TAG, "move");
                    mMove = true;
                    stopLoop();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "up");
                mMove = false;
                startLoop();
                break;

        }
        return super.dispatchTouchEvent(event);
    }

    public static class ImagePagerAdapter extends PagerAdapter {
        private List<String> mPagerData;
        private onLoopImageClickListener mL;

        public ImagePagerAdapter(List<String> pagerData){
            mPagerData = pagerData;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            container.removeView((ImageView)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position){
            ImageView imageView = new ImageView(container.getContext());
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                imageView.setElevation(DpHelper.dp2px(8));
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v){
                    if(null != mL) {
                        mL.onItemClickd(position%( mPagerData.size() ));
                    }
                }
            });
            int size = mPagerData.size();
            if(size>0) {
                Picasso.with(container.getContext()).load(mPagerData.get(position%size)).into(imageView);
                //                Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                container.addView(imageView);
            }
            return imageView;
        }

        @Override
        public int getCount(){
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1){
            return arg0 == arg1;
        }

        public void setOnitemClickListener(onLoopImageClickListener l){
            mL = l;
        }
    }

    public void setOnPagerItemClickListener(onLoopImageClickListener l){
        mL = l;
        if(mAdapter != null) {
            mAdapter.setOnitemClickListener(l);
        }
    }

    public interface onLoopImageClickListener {
        void onItemClickd(int Position);
    }


    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        //往下滚动到看不到
        stopLoop();
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        //向上滚动到可见
        startLoop();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //不可见滚动到可见会draw一次
        //可见到不可见不会触发
        //滚动轮播图的时候也会触发 左右滚动
        if(mSubscribe != null && !mMove && getLocalVisibleRect(mVisibleRect)) {
            startLoop();
        }
    }

    class MzTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.9F;

        @Override
        public void transformPage(View page, float position){

            if(position<-1) {
                page.setScaleY(MIN_SCALE);
            }else if(position<=1) {
                //
                float scale = Math.max(MIN_SCALE, 1-Math.abs(position));
                page.setScaleY(scale);
            /*page.setScaleX(scale);

            if(position<0){
                page.setTranslationX(width * (1 - scale) /2);
            }else{
                page.setTranslationX(-width * (1 - scale) /2);
            }*/

            }else {
                page.setScaleY(MIN_SCALE);
            }
        }

    }
}
