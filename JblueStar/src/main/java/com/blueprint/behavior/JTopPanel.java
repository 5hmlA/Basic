package com.blueprint.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

import com.blueprint.R;

import static com.blueprint.helper.Damping.hide;
import static com.blueprint.helper.Damping.show;
import static com.blueprint.helper.NumHelper.null2Int;
import static com.blueprint.helper.NumHelper.null2float;

/**
 * @another 江祖赟
 * @date 2017/7/3.
 */
public class JTopPanel extends CoordinatorLayout.Behavior<View> {

    private boolean mTitleHide;
    private static final int ANIDURATION = 300;
    private int mScaledTouchSlop;
    private int mDyUnconsumeds;
    private float mDamping;

    public JTopPanel(){
        super();
    }

    public JTopPanel(Context context, AttributeSet attrs){
        super(context, attrs);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    //    理解View之间的依赖
    //    以上的所有功能都只需要一个View。但是Behaviors的强大之处在于在View之间建立依赖关系－当另一个View改变的时候，你的Behavior会得到一个callback，根据外部条件改变它的功能。
    //    Behaviors依赖于View有两种形式：当它的View锚定于另外一个View（一种隐式的依赖）或者，当你在layoutDependsOn()中明确的返回true。
    //    锚定发生于你使用了CoordinatorLayout的layout_anchor 属性之时。它和layout_anchorGravity 属性结合，可以让你有效的把两个View捆绑在一起。比如，你可以把一个FloatingActionButton锚定在一个AppBarLayout上，那么如果AppBarLayout滚动出屏幕，FloatingActionButton.Behavior将使用隐式的依赖去隐藏FAB。
    //    不管什么形式，当一个依赖的View被移除的时候你的Behavior会得到回调 onDependentViewRemoved() ，当依赖的View发生变化的时候（比如：调整大小或者重置自己的position），得到回调 onDependentViewChanged()
    //    这个把View绑定在一起的能力正是Design Library那些酷炫功能的工作原理 －以FloatingActionButton与Snackbar之间的交互为例。FAB的 Behavior依赖于被添加到CoordinatorLayout的Snackbar，然后它使用onDependentViewChanged()  callback来将FAB向上移动，以避免和Snackbar重叠。
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency){
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency){
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency){
        super.onDependentViewRemoved(parent, child, dependency);
    }

    //嵌套滚动，但其实它包含滚动（scrolling）和划动（flinging）两种。
    //    那么让我们使用onStartNestedScroll()来定义你所感兴趣的嵌套滚动（方向）。你将收到滚动的轴（比如横向或者纵向－让它可以轻易的忽略某个方向上的滚动）并且为了接收那个方向上的后续滚动事件必须返回true。
    //    当你在onStartNestedScroll()中返回了true之后，嵌套滚动进入两个阶段：
    //    onNestedPreScroll() 会在scrolling View获得滚动事件前调用，它允许你消费部分或者全部的事件信息。
    //    onNestedScroll() 会在scrolling View做完滚动后调用，通过回调可以知道scrolling view滚动了多少和它没有消耗的滚动事件。
    //    同样，fling操作也有与之相对应的方法（虽然e pre-fling callback 必须消费完或者完全不消费fling － 没有消费部分的情况）。
    //    当嵌套滚动（或者flinging）结束，你将得到一个onStopNestedScroll()回调。这标志着滚动的结束 － 迎接在下一个滚动之前的onStartNestedScroll() 调用。
    //    比如，当向下滚动的时候隐藏FloatingActionButton，向上滚动的时候显示FloatingActionButton－ 这只牵涉到重写onStartNestedScroll() 和 onNestedScroll()，就如在ScrollAwareFABBehavior中所看到的那样。
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes){
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes){
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    //    computeVerticalScrollExtent()是当前屏幕显示的区域高度，
    // computeVerticalScrollOffset() 是当前屏幕之前滑过的距离，//屏幕之外的距离
    // 而computeVerticalScrollRange()是整个View控件的高度。
    //开始会调 stop
    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target){
        //        coordinatorLayout.getTag() recycleview滑动速度  上滑动>0
        float velocityY = null2float(coordinatorLayout.getTag(R.id.jblue_damp_scroll_tag));
        int dyConsumed = null2Int(target.getTag(R.id.jblue_damp_scroll_tag));
        int paddingTop = target.getPaddingTop();
        int verticalScrollOffset = ( (RecyclerView)target ).computeVerticalScrollOffset();
        //是当前屏幕显示的区域高度
        int verticalScrollExtent = ( (RecyclerView)target ).computeVerticalScrollExtent();
        //是整个View控件的高度
        int verticalScrollRange = ( (RecyclerView)target ).computeVerticalScrollRange();
        // target.getTag(R.id.jblue_damp_scroll_tag) recycleview滑动的距离 上滑动>0
        if(mDyUnconsumeds == 0) {
            //targetview 可滑动的时候

            //无法通过 速度计算recycleview的滑动距离
            //当此时recycleview滑出屏幕的距离小于pading的时候 自动显示recycleview
            //当此时 recycleview滑出屏幕的距离大于pading的时候  自动隐藏
            if(mTitleHide) {
                //隐藏title
                if(dyConsumed>=mScaledTouchSlop*2||verticalScrollOffset>paddingTop) {
                    //滑动太快 动画隐藏 //滑出距离大于pading整个屏幕都可以看见target
                    hide(child);
                }
            }else {
                //滑动太快就不跟着手指滑动//快滑到顶部的时候才滑出topbar //下滑很快的时候 动画滑出topbar
                //处理跟着 自动动画滑动
                //可能出现当滑动速度比较小但是滑动结束之后 剩下的verticalScrollOffset会小于padingTop
                if(verticalScrollOffset<=paddingTop*4f/3 || Math.abs(velocityY)>2000) {
                    //todo 根据velocityY滑动速度 计算滑动距离 当计算的滑动距离剩下的verticalScrollOffset<=paddingTop就自动滑出toolbar
                    //快滑倒底的时候 | 下滑速度很快的时候
                    show(child);
                }
            }
            target.setTag(R.id.jblue_damp_scroll_tag,null);
        }
        mDyUnconsumeds = 0;
        coordinatorLayout.setTag(R.id.jblue_damp_scroll_tag,0f);
        //根据滑动速度 判断是否显示/隐藏
        if(Math.abs(velocityY)>2000 && verticalScrollRange-verticalScrollExtent>paddingTop) {
            if(velocityY>0) {
                //说明可能target上滑动滑动距离>=padingtop
                hide(child);
            }else {
                show(child);
            }
        }
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    //    onNestedScroll() 会在scrolling View做完滚动后调用，通过回调可以知道scrolling view滚动了多少和它没有消耗的滚动事件。
    //target是当前滑动的可滚动的view [NestedScrollingChild子view]
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed){
        mDyUnconsumeds += dyUnconsumed;
        if(mDyUnconsumeds == 0) {
            mDyUnconsumeds = 0;
            target.setTag(R.id.jblue_damp_scroll_tag,dyConsumed);
            coordinatorLayout.setTag(R.id.jblue_damp_scroll_tag,0f);
            int paddingTop = target.getPaddingTop();
            int verticalScrollOffset = ( (RecyclerView)target ).computeVerticalScrollOffset();

            //处理跟着手指滑动
            if(dyConsumed>0) {
                //上滑动 隐藏topbar
                if(Math.abs(dyConsumed)<mScaledTouchSlop*2) {
                    //滑动太快就不跟着手指滑动
                    child.setTranslationY(Math.min(child.getTranslationY()-dyConsumed, 0));
                }
            }else {
                //dyConsumed<0 下滑 显示topbar                  快滑到顶部的时候才滑出topbar
                if(Math.abs(dyConsumed)<mScaledTouchSlop*3 && verticalScrollOffset<=paddingTop*4f/3) {
                    //滑动太快就不跟着手指滑动//快滑到顶部的时候才滑出topbar //下滑很快的时候 动画滑出topbar
                    child.setTranslationY(Math.min(child.getTranslationY()-dyConsumed, 0));
                }
            }

            //        dyConsumed>0rv上滑  滑倒底部或顶部为0
            if(dyConsumed != 0) {
                mTitleHide = dyConsumed>0;
            }
        }
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    //    onNestedPreScroll() 会在scrolling View获得滚动事件前调用，它允许你消费部分或者全部的事件信息。
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed){
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, boolean consumed){
        coordinatorLayout.setTag(R.id.jblue_damp_scroll_tag,velocityY);//向上滚动 velocityY > 0
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY){
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }


}
