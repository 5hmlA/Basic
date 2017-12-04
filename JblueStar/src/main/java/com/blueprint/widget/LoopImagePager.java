package com.blueprint.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blueprint.LibApp;
import com.blueprint.helper.CheckHelper;
import com.blueprint.helper.DpHelper;
import com.blueprint.helper.LogHelper;
import com.blueprint.helper.PicHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.blueprint.helper.LogHelper.slog_d;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [rxjava实现自动滚动轮播图，控件可见重启滾動，不可见关闭滚动]
 */
public class LoopImagePager extends RelativeLayout implements NestedScrollingChild {
    private static final String TAG = LoopImagePager.class.getSimpleName();
    private static final long LOOPINTERVAL = 2;
    private ViewPagerFixed mViewPager;
    private Disposable mSubscribe;
    private onLoopImageClickListener mL;
    private ImagePagerAdapter mAdapter;
    private Rect mVisibleRect = new Rect();
    private boolean mMove;
    private PointF mLastMoved = new PointF();
    private int mCurrentPosition = 20;
    private ViewPager.PageTransformer mMzTransformer = new MzTransformer();
    private boolean mAutoLoop = true;
    private PagerAdapter mCustomAdapter;
    private NestedScrollingChildHelper mChildHelper;
    private List<String> mPagerData;
    public int mImageRoundRaidus;

    public LoopImagePager(Context context){
        this(context, null);
    }

    public LoopImagePager(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public LoopImagePager(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mViewPager = new ViewPagerFixed(context);
        setClipChildren(false);
        setClipToPadding(false);
        setPadding((int)DpHelper.dp2px(20), getPaddingTop(), (int)DpHelper.dp2px(20), getPaddingBottom());
        //        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //        layoutParams.leftMargin = (int)DpHelper.dp2px(20);
        //        layoutParams.rightMargin = (int)DpHelper.dp2px(20);
        //        mViewPager.setLayoutParams(layoutParams);
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

        setSliderTransformDuration(300, new DecelerateInterpolator());
        mChildHelper = new NestedScrollingChildHelper(this);
        mChildHelper.setNestedScrollingEnabled(true);

        //        post(new Runnable() {
        //            @Override
        //            public void run(){
        //                startLoop();
        //            }
        //        });
    }

    /**
     * set the duration between two slider changes.
     */
    private void setSliderTransformDuration(int period, Interpolator interpolator){
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), interpolator, period);
            mScroller.set(mViewPager, scroller);
        }catch(Exception e) {

        }
    }

    private void startLoop(){
        if(!mAutoLoop || mAdapter == null || mAdapter.getCount()<=1) {
            return;
        }
        if(mSubscribe == null || mSubscribe.isDisposed()) {
            slog_d(TAG, "start loop ==============");
            mSubscribe = Observable.interval(LOOPINTERVAL, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception{
                            //并不是立刻執行
                            if(getLocalVisibleRect(mVisibleRect)) {
                                //可见
                                mViewPager.setCurrentItem(++mCurrentPosition);
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
            slog_d(TAG, "stop auto loop ==============");
            mCurrentPosition = mViewPager.getCurrentItem();
            mSubscribe.dispose();
        }
    }

    public LoopImagePager setPagerAdapter(PagerAdapter pagerAdapter){
        mCustomAdapter = pagerAdapter;
        return this;
    }

    public LoopImagePager setPagerData(String[] pagerData, int currentPosition){
        return setPagerData(Arrays.asList(pagerData), currentPosition);
    }

    public LoopImagePager setPagerData(List<String> pagerData, int currentPosition){
        mCurrentPosition = currentPosition;
        return setPagerData(pagerData);
    }

    public List<String> getPagerData(){
        return mPagerData;
    }

    public LoopImagePager setPagerData(List<String> pagerData){
        mPagerData = pagerData;
        if(!CheckHelper.checkLists(pagerData)) {
            LogHelper.Log_e("LoopImagePager,setPagerData:pagerData null");
            setVisibility(GONE);
        }
        if(mMzTransformer != null) {
            mViewPager.setPageTransformer(true, mMzTransformer);
        }
        if(mCustomAdapter == null) {
            mAdapter = new ImagePagerAdapter(pagerData);
            if(mL != null) {
                mAdapter.setOnitemClickListener(mL);
            }
            mViewPager.setAdapter(mAdapter);
        }else {
            mViewPager.setAdapter(mCustomAdapter);
        }
        mCurrentPosition = pagerData.size()>1 ? mCurrentPosition : 0;
        mViewPager.setCurrentItem(mCurrentPosition);
        mViewPager.setOffscreenPageLimit(( 3 ));
        if(mAutoLoop) {
            startLoop();
        }
        return this;
    }

    public LoopImagePager setPageTransformer(ViewPager.PageTransformer transformer){
        mMzTransformer = transformer;
        return this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        try {
            if(mAutoLoop && mAdapter != null && mAdapter.getCount()>1) {
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
                            mChildHelper.stopNestedScroll();
                            //down的时候关了
                            startLoop();
                        }else {
                            mChildHelper.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                            slog_d(TAG, "move");
                            mMove = true;
                            stopLoop();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mChildHelper.stopNestedScroll();
                        slog_d(TAG, "up");
                        mMove = false;
                        startLoop();
                        break;

                }
            }
            return super.dispatchTouchEvent(event);
        }catch(IllegalArgumentException ex) {
            //            https://github.com/chrisbanes/PhotoView/issues/31
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled){
        super.setNestedScrollingEnabled(enabled);
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled(){
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes){
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll(){
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent(){
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow){
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow){
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed){
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY){
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }


    public LoopImagePager setAutoLoop(boolean autoLoop){
        mAutoLoop = autoLoop;
        return this;
    }

    public LoopImagePager asNormalViewpager(){
        setPadding(0, 0, 0, 0);
        return this;
    }

    public class ImagePagerAdapter extends PagerAdapter {
        private List<String> mPagerData;
        private onLoopImageClickListener mL;
//        private SparseArray<ImageView> mIvCache = new SparseArray<>();

        private ImageView getIvByPosition(int position){
            final int realPosition = getRealPosition(position);
            //            ImageView imageView = mIvCache.get(realPosition);
            //            if(imageView == null) {
            final JDimImageView imageView = new JDimImageView(LibApp.getContext());
            imageView.setImgRadius(mImageRoundRaidus);
            imageView.clipTransform = true;
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                imageView.setElevation(DpHelper.dp2px(8));
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v){
                    if(null != mL) {
                        //                            mL.onItemClickd(imageView, imageView, realPosition);
                        mL.onItemClickd(LoopImagePager.this, imageView, realPosition);
                    }
                }
            });
            PicHelper.loadImage(mPagerData.get(realPosition), imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //                mIvCache.append(position, imageView);
            //            }
            return imageView;
        }

        private int getRealPosition(int position){
            return position%( mPagerData.size() );
        }

        public ImagePagerAdapter(List<String> pagerData){
            mPagerData = pagerData;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            container.removeView((ImageView)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position){
            View imageView = getIvByPosition(( position ));
            container.addView(imageView);
            return imageView;
        }

        @Override
        public int getCount(){
            return mPagerData.size()>1 ? Integer.MAX_VALUE : 1;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1){
            return arg0 == arg1;
        }

        public void setOnitemClickListener(onLoopImageClickListener l){
            mL = l;
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
    }

    public void setOnPagerItemClickListener(onLoopImageClickListener l){
        mL = l;
        if(mAdapter != null) {
            mAdapter.setOnitemClickListener(l);
        }
    }

    public interface onLoopImageClickListener {
        void onItemClickd(LoopImagePager view, ImageView imageView, int Position);
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

    static class MzTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.9F;

        @Override
        public void transformPage(View page, float position){
            //            position == [0,1] ：当前界面位于屏幕中心的时候
            //            position == [1,Infinity] ：当前Page刚好滑出屏幕右侧
            //            position ==[-Infinity,-1] ：当前Page刚好滑出屏幕左侧
            if(position<-1) {// [-Infinity,-1)
                // This page is way off-screen to the left. 当前Page刚好滑出屏幕左侧
                page.setScaleX(MIN_SCALE+0.04f);
                page.setScaleY(MIN_SCALE);
            }else if(position<=1) {
                float scale = Math.max(MIN_SCALE, 1-Math.abs(position));
                float scalex = Math.max(MIN_SCALE+0.04f, 1-Math.abs(position));
                page.setScaleY(scale);
                page.setScaleX(scalex);
            }else {// (1,+Infinity]
                // This page is way off-screen to the right.当前Page刚好滑出屏幕右侧
                page.setScaleX(MIN_SCALE+0.04f);
                page.setScaleY(MIN_SCALE);
            }
        }

    }

    public int getCurrentPosition(){
        if(mViewPager != null) {
            return mViewPager.getCurrentItem();
        }else {
            return 0;
        }
    }

    public ViewPager getViewpager(){
        return mViewPager;
    }
}
