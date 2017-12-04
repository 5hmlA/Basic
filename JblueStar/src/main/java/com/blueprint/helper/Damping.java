package com.blueprint.helper;

import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

/**
 * @author yun.
 * @date 2017/7/15
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class Damping implements View.OnTouchListener {
    private static final long ANIDURATION = 250;
    private static final int NODIRECTION = -110;
    private static final int TAG_ANIMATOR = 0x199101aa;
    private static int sHeightPixels;
    private static int sWidthPixels;
    private View mView;
    float mScale = 1;
    private PointF mTdown = new PointF(0, 0);
    private float mDistance;
    private int direction = NODIRECTION;
    private int fixDirection = NODIRECTION;
    private ValueAnimator mRestoreAnimator;

    {
        mRestoreAnimator = ValueAnimator.ofFloat(1f, 1f);
        mRestoreAnimator.setDuration(250);
        mRestoreAnimator.setInterpolator(new OvershootInterpolator(1.6f));
        //        animator.setInterpolator(new DecelerateInterpolator(2f));
        mRestoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                mScale = (float)animation.getAnimatedValue();
                mView.setScaleY(mScale);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        return dampOnTouch(event);
    }

    public static Damping wrapper(View view){
        view.setClickable(true);
        return new Damping(view);
    }

    public Damping(@NonNull View view){
        mView = view;
        sHeightPixels = view.getContext().getResources().getDisplayMetrics().heightPixels;
        sWidthPixels = view.getContext().getResources().getDisplayMetrics().widthPixels;
        mView.setOnTouchListener(this);
    }

    public Damping configDirection(int direction){
        this.direction = fixDirection = direction;
        return this;
    }

    private void animateRestore(){
        if(mScale != 1 && !mRestoreAnimator.isRunning()) {
            mRestoreAnimator.cancel();
            mRestoreAnimator.setFloatValues(mScale, 1f);
            mRestoreAnimator.start();
        }
    }

    /**
     * 处于顶部/左边 往下/右拉动
     */
    private void pull(float mScale){
        if(direction == LinearLayout.VERTICAL) {
            mView.setPivotY(mView.getPaddingTop());
            mView.setScaleY(mScale);
        }else {
            mView.setPivotX(mView.getPaddingLeft());
            mView.setScaleX(mScale);
        }
    }

    /**
     * 处于底部/右边 往上/左拉动
     */
    private void push(float mScale){
        if(direction == LinearLayout.VERTICAL) {
            mView.setPivotY(mView.getHeight());
            mView.setScaleY(mScale);
        }else {
            mView.setPivotX(mView.getRight());
            mView.setScaleX(mScale);
        }

    }

    /**
     * 处于顶部 往下拉动
     *
     * @param miui
     * @param scale
     */
    public static void pull(View miui, float scale){
        miui.setPivotY(miui.getPaddingTop());
        miui.setScaleY(scale);
    }

    /**
     * 处于底部 往上拉动
     *
     * @param miui
     * @param scale
     */
    public static void push(View miui, float scale){
        miui.setPivotY(miui.getHeight());
        miui.setScaleY(scale);
    }

    public static void hide(final View view){
        view.animate().cancel();
        view.animate().translationY(-view.getHeight()).setInterpolator(new LinearOutSlowInInterpolator()).withLayer()
                .setDuration(ANIDURATION).start();
    }

    public static void hide(final View view, int height){
        view.animate().cancel();
        view.animate().translationY(-height).setInterpolator(new LinearOutSlowInInterpolator()).withLayer()
                .setDuration(ANIDURATION).start();
    }

    public static void show(final View view){
        view.animate().cancel();
        view.animate().translationY(0).setInterpolator(new LinearOutSlowInInterpolator()).withLayer()
                .setDuration(ANIDURATION).start();
    }

    /**
     * 根据滑动距离计算 横向缩放值
     *
     * @param distance
     * @return
     */
    public static float calculateHorizontalDamping(float distance){
        float dragRadio = distance/sWidthPixels;
        float dragPercent = Math.min(1f, dragRadio);
        float rate = 2f*dragPercent-(float)Math.pow(dragPercent, 2f);
        return 1+rate/6f;
    }

    /**
     * 根据滑动距离计算 竖向缩放值
     *
     * @param distance
     * @return
     */
    public static float calculateVerticalDamping(float distance){
        float dragRadio = distance/sHeightPixels;
        float dragPercent = Math.min(1f, dragRadio);
        float rate = 2f*dragPercent-(float)Math.pow(dragPercent, 2f);
        return 1+rate/6f;
    }

    public static void animateRestore(final View miui, float mScale){
        Object tag = miui.getTag(TAG_ANIMATOR);
        ValueAnimator animator = null;
        if(tag == null) {
            animator = ValueAnimator.ofFloat(mScale, 1f);
            animator.setDuration(300);
            animator.setInterpolator(new OvershootInterpolator(1.6f));
            //        animator.setInterpolator(new DecelerateInterpolator(2f));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation){
                    float scale = (float)animation.getAnimatedValue();
                    miui.setScaleY(scale);
                }
            });
            miui.setTag(TAG_ANIMATOR, animator);
        }else {
            animator = ( (ValueAnimator)tag );
            animator.cancel();
            animator.setFloatValues(mScale, 1f);
        }
        animator.start();
    }

    public static boolean isScrollToLeft(View view){
        return !view.canScrollHorizontally(-1);
    }

    public static boolean isScrollToRight(View view){
        return !view.canScrollHorizontally(1);
    }

    public static boolean isScrollToTop(View view){
        return !view.canScrollVertically(-1);
    }

    public static boolean isScrollToBottom(View view){
        return !view.canScrollVertically(1);
    }

    public boolean isScrollToTop(){
        return !mView.canScrollVertically(-1);
    }

    public boolean isScrollToBottom(){
        return !mView.canScrollVertically(1);
    }

    public boolean dampOnTouch(MotionEvent event){
        if(mView != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    direction = fixDirection;
                    mTdown.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(isScrollToTop() || isScrollToBottom()) {
                        if(mTdown.equals(0, 0)) {
                            direction = fixDirection;
                            mTdown.set(event.getX(), event.getY());
                        }
                        float c = event.getY();
                        float l = mTdown.y;
                        checkDragDirection(event);
                        if(direction == LinearLayout.HORIZONTAL) {
                            c = event.getX();
                            l = mTdown.x;
                        }
                        if(direction != NODIRECTION) {
                            calcuteMove(c, l);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(!mTdown.equals(0, 0) && direction != NODIRECTION) {
                        animateRestore();
                    }
                    mTdown.set(0, 0);
                    break;

            }
        }
        return false;
    }

    /**
     * 滚动时以最开始的方向为准 滑动过程中换方向 忽略
     *
     * @param event
     */
    private void checkDragDirection(MotionEvent event){
        if(direction == NODIRECTION && Math.abs(event.getY()-mTdown.y) != Math.abs(event.getX()-mTdown.x))
        //滑动的时候 不能改变方向 否则恢复只能往一个方向恢复
        {
            if(Math.abs(event.getY()-mTdown.y)>Math.abs(event.getX()-mTdown.x)) {
                direction = LinearLayout.VERTICAL;
            }else {
                direction = LinearLayout.HORIZONTAL;
            }
        }
    }

    private void calcuteMove(float y, float ly){
        if(direction == LinearLayout.VERTICAL) {
            calcureVerticalMove(y, ly);
        }else {
            calcureHorizontalMove(y, ly);
        }
    }

    private void calcureHorizontalMove(float y, float ly){
        if(isScrollToLeft(mView) && !isScrollToRight(mView)) {
            // 在左边不在右边
            mDistance = y-ly;
            mScale = Damping.calculateHorizontalDamping(mDistance);
            pull(mScale);
        }else if(!isScrollToLeft(mView) && isScrollToRight(mView)) {
            // 在右边不在左边
            mDistance = ly-y;
            mScale = calculateHorizontalDamping(mDistance);
            push(mScale);
        }else if(isScrollToLeft(mView) && isScrollToRight(mView)) {
            // 在右边也在左边
            mDistance = y-ly;
            if(mDistance>0) {
                mScale = calculateHorizontalDamping(mDistance);
                pull(mScale);
            }else {
                mScale = calculateHorizontalDamping(-mDistance);
                push(mScale);
            }
        }
    }

    private void calcureVerticalMove(float y, float ly){
        if(isScrollToTop() && !isScrollToBottom()) {
            // 在顶部不在底部
            mDistance = y-ly;
            mScale = Damping.calculateVerticalDamping(mDistance);
            pull(mScale);
        }else if(!isScrollToTop() && isScrollToBottom()) {
            // 在底部不在顶部
            mDistance = ly-y;
            mScale = calculateVerticalDamping(mDistance);
            push(mScale);
        }else if(isScrollToTop() && isScrollToBottom()) {
            // 在底部也在顶部
            mDistance = y-ly;
            if(mDistance>0) {
                mScale = calculateVerticalDamping(mDistance);
                pull(mScale);
            }else {
                mScale = calculateVerticalDamping(-mDistance);
                push(mScale);
            }
        }
    }
}

