package com.blueprint.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.blueprint.R;
import com.blueprint.helper.DpHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yun.
 * @date 2016/12/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JToolsLayout extends RelativeLayout {
    private static final int DURATION = 300;
    private static final String TAG = JToolsLayout.class.getSimpleName();
    private float VISI_AREA = 2*68;

    private int mVisibility;
    private int mW;
    private int mH;
    private Handler mHandler;

    public JToolsLayout(Context context) {
        super(context);
    }

    public JToolsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JToolsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
        Log.d(TAG,mH+ "-height--------------------------------------------width " + mW);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHandler = new Handler();
    }

    @Override
    public void setVisibility(int visibility) {
        Log.d(TAG, "---------------------------------------------visibility " + visibility);
        outerGroups.clear();
//        setViewVisibilityTogeter(this, visibility);
        setVisibility(this, visibility);
        mVisibility = visibility;
//        int childCount = getChildCount();
//        if (childCount > 0) {
////            int delay = 0;
//            for (int i = 0; i < getChildCount(); i++) {
//                View childAt = getChildAt(i);
////                if (childAt instanceof TimeProgressPlus) {
////                    float end = 0;
////                    if (visibility == VISIBLE) {
////                        end = 0;
////                    }
////                    end += 1;
////                    if (visibility != VISIBLE) {
////                        end -= 1;
////                    }
////                    alpha(childAt, end, 0);
////                } else {
////                    ViewVisibility(visibility, childAt,0);
//                childAt.setTag(0);
//                if (childAt instanceof ViewGroup) {
//                    childAt.setTag(R.id.tag_1_father,childAt);
//                }
//                setVisibility(childAt, visibility);
////                }
//            }
//        }
    }

    private List<ViewGroup> outerGroups = new ArrayList<>();

    public void setVisibility(View mView, int visibility) {
        if (mView.getTag() == null) {
            outerGroups.clear();
            mView.setTag(0);//设置动画延时时间
        }
        if (mView instanceof ViewGroup && ((ViewGroup) mView).getChildCount() > 0) {
            //如果是 viewgroup的话 继续遍历 一直到找到最深处的view
            View supview_father = mView;//第一层 就是自己
            if (mView.getTag(R.id.tag_super_father) != null) {
                supview_father = (View) mView.getTag(R.id.tag_super_father);//获取最外层父布局
            }
            int tagDelay = (int) mView.getTag();
            ViewGroup viewGroup = (ViewGroup) mView;
            outerGroups.add(0, viewGroup);//将viewgroup添加到集合 最后处理
            boolean findviewfinish = true;//是否找到最深处的view
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                childAt.setTag(tagDelay += DURATION / 5);
                if (childAt instanceof ViewGroup && ((ViewGroup) mView).getChildCount() > 0) {
                    findviewfinish = false;//还需要继续遍历
                    childAt.setTag(R.id.tag_super_father, supview_father);//将最外层父布局 设置给需要遍历的viewgroup
                    setVisibility(childAt, visibility);
                } else if (!(childAt instanceof Space)) {
                    ViewVisibilityInScreen(visibility, childAt, tagDelay);
                }
            }
            if (findviewfinish) {
                //找到最深处的view 隐藏/显示 各层次外部父布局
                for (ViewGroup outerGroup : outerGroups) {
                    if (outerGroup.getBackground() != null) {
                        Log.d(TAG, "ViewGroup have Background " + outerGroup.getClass().getSimpleName());
                        //viewgroup有背景的时候
                        visibilityAniAlpha(visibility, outerGroup, tagDelay += DURATION / 6);
                    }
                }
            }
        } else {
            ViewVisibilityInScreen(visibility, mView, (int) mView.getTag());
        }
    }

    private void setViewVisibilityTogeter(View mView, int visibility){
        if (mView instanceof ViewGroup && ((ViewGroup) mView).getChildCount() > 0) {
            ViewGroup viewGroup = (ViewGroup) mView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                ViewVisibility(visibility, childAt, 0);
            }
        } else {
            ViewVisibility(visibility, mView, 0);
        }

    }

    private void ViewVisibility(int visibility, View childAt, long delay) {
        int top = childAt.getTop();
        int bottom = childAt.getBottom();
        int left = childAt.getLeft();
        int right = childAt.getRight();
        float end = 0;
        if (visibility == VISIBLE) {
            end = 0;
        }
        if (bottom < DpHelper.dp2px(VISI_AREA)) {
            if (visibility != VISIBLE) {
                end = -bottom;
            }
            translateY(childAt, end, delay);
        } else if (mH - top < DpHelper.dp2px(VISI_AREA)) {
            //下
            if (visibility != VISIBLE) {
                end = mH - top;
            }
            translateY(childAt, end, delay);
        } else if (right < DpHelper.dp2px(VISI_AREA)) {
            //左
            if (visibility != VISIBLE) {
                end = -right;
            }
            translateX(childAt, end, delay);
        } else if (mW - left < DpHelper.dp2px(VISI_AREA)) {
            if (visibility != VISIBLE) {
                end = mW - left;
            }
            translateX(childAt, end, delay);
        } else {
            end += 1;
            if (visibility != VISIBLE) {
                end -= 1;
            }
            alpha(childAt, end, delay);
        }
    }

    private void ViewVisibilityInScreen(int visibility, View childAt, long delay) {
        int[] location = new int[2];
        childAt.getLocationOnScreen(location);
        Log.d(TAG, childAt.getClass().getSimpleName() + "___location[" + location[0] + ":" + location[1] + "]___VISI_AREA：" + VISI_AREA);
        int top = location[1];
        int bottom = location[1] + childAt.getMeasuredHeight();
        int left = location[0];
        int right = location[0] + childAt.getMeasuredWidth();
        float end = 0;
        if (visibility == VISIBLE) {
            end = 0;
        }
        if (bottom < DpHelper.dp2px(VISI_AREA)) {
            if (visibility != VISIBLE) {
                end = -bottom;
            }
            translateY(childAt, end, delay);
        } else if (mH - top < DpHelper.dp2px(VISI_AREA)) {
            //下
            if (visibility != VISIBLE) {
                end = mH - top;
            }
            translateY(childAt, end, delay);
        } else if (right < DpHelper.dp2px(VISI_AREA)) {
            //左
            if (visibility != VISIBLE) {
                end = -right;
            }
            translateX(childAt, end, delay);
        } else if (mW - left < DpHelper.dp2px(VISI_AREA)) {
            if (visibility != VISIBLE) {
                end = mW - left;
            }
            translateX(childAt, end, delay);
        } else {
            //中间
            end += 1;
            if (visibility != VISIBLE) {
                end -= 1;
            }
            alpha(childAt, end, delay);
        }
    }

    public void visibilityAniAlpha(int visibility, View childAt, long delay) {
        int end = 1;
        if (visibility != VISIBLE) {
            end -= 1;
        }
        alpha(childAt, end, delay);
    }

    public int getVisibility() {
        return mVisibility;
    }

    public void translateY(View v, float end, long delay) {
        v.animate().translationY(end).setDuration(DURATION).setStartDelay(delay).start();
    }

    public void translateX(View v, float end, long delay) {
        v.animate().translationX(end).setDuration(DURATION).setStartDelay(delay).start();
    }

    public void alpha(View v, float end, long delay) {
        v.animate().alpha(end).setDuration(DURATION).setStartDelay(delay).start();
    }
}
