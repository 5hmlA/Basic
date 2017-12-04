package com.blueprint.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueprint.Consistent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

import static com.blueprint.helper.DrawHelper.getColorStateList2;
import static com.blueprint.helper.DrawHelper.getListDrable;

public class JFlowLayout extends ViewGroup implements View.OnClickListener {


    public static final int DEFAULT_SPACING = 20;
    /**
     * 横向间隔
     */
    private int mHorizontalSpacing = DEFAULT_SPACING;
    /**
     * 纵向间隔
     */
    private int mVerticalSpacing = DEFAULT_SPACING;
    /**
     * 是否需要布局，只用于第一次
     */
    boolean mNeedLayout = true;
    /**
     * 当前行已用的宽度，由子View宽度加上横向间隔
     */
    private int mUsedWidth = 0;
    /**
     * 代表每一行的集合
     */
    private final List<Line> mLines = new ArrayList<Line>();
    private Line mLine = null;
    /**
     * 最大的行数
     */
    private int mMaxLinesCount = Integer.MAX_VALUE;
    private boolean mSingleSelecte;
    private Checkable lastSelected;
    private OnItemSelectedListener mListener;
    private int mMaxChileEachLine = Integer.MAX_VALUE;
    private boolean mNeedExpend = false;
    private int mItemTvColor = Color.RED;
    private ColorStateList mItemTvColorSelector;
    private LayoutParams mItemLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, dip2px(28));
    private int mTextSize = 13;
    private StateListDrawable mItemBgselector;
    private int mItemBg_ids = Consistent.DEFAULTERROR;
    private int mItemGravity = Gravity.CENTER;
    private boolean mChildClickAble = true;

    public JFlowLayout(Context context){
        super(context);
    }

    public JFlowLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    private void requestLayoutInner(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            if(isAttachedToWindow()) {
                requestLayout();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if(getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getWidth(),getMinimumHeight());
        }else {
            int sizeWidth = MeasureSpec.getSize(widthMeasureSpec)-getPaddingRight()-getPaddingLeft();
            int sizeHeight = MeasureSpec.getSize(heightMeasureSpec)-getPaddingTop()-getPaddingBottom();

            int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
            int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

            restoreLine();// 还原数据，以便重新记录
            final int count = getChildCount();
            for(int i = 0; i<count; i++) {
                final View child = getChildAt(i);
                if(child.getVisibility() == GONE) {
                    continue;
                }
                int childWidthMeasureSpec = MeasureSpec
                        .makeMeasureSpec(sizeWidth, modeWidth == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeWidth);
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(sizeHeight,
                        modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeHeight);
                // 测量child
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                if(mLine == null) {
                    mLine = new Line();
                }
                int childWidth = child.getMeasuredWidth();
                mUsedWidth += childWidth;// 增加使用的宽度
                if(mUsedWidth<=sizeWidth && mLine.getViewCount()<mMaxChileEachLine) {// 使用宽度小于总宽度，该child属于这一行。
                    mLine.addView(child);// 添加child
                    mUsedWidth += mHorizontalSpacing;// 加上间隔
                    if(mUsedWidth>=sizeWidth) {// 加上间隔后如果大于等于总宽度，需要换行
                        if(!newLine()) {
                            break;
                        }
                    }
                }else {// 使用宽度大于总宽度。需要换行
                    if(mLine.getViewCount() == 0) {// 如果这行一个child都没有，即使占用长度超过了总长度，也要加上去，保证每行都有至少有一个child
                        mLine.addView(child);// 添加child
                        if(!newLine()) {// 换行
                            break;
                        }
                    }else {// 如果该行有数据了，就直接换行
                        if(!newLine()) {// 换行
                            break;
                        }
                        // 在新的一行，不管是否超过长度，先加上去，因为这一行一个child都没有，所以必须满足每行至少有一个child
                        mLine.addView(child);
                        mUsedWidth += childWidth+mHorizontalSpacing;
                    }
                }
            }

            if(mLine != null && mLine.getViewCount()>0 && !mLines.contains(mLine)) {
                // 由于前面采用判断长度是否超过最大宽度来决定是否换行，则最后一行可能因为还没达到最大宽度，所以需要验证后加入集合中
                mLines.add(mLine);
            }

            //        if (mLines.size() >= 1) {
            //            mLines.get(mLines.size() - 1).setLastLine(true);
            //        }
            int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
            int totalHeight = 0;
            final int linesCount = mLines.size();
            for(int i = 0; i<linesCount; i++) {// 加上所有行的高度
                totalHeight += mLines.get(i).mHeight;
            }
            totalHeight += mVerticalSpacing*( linesCount-1 );// 加上所有间隔的高度
            totalHeight += getPaddingTop()+getPaddingBottom();// 加上padding
            // 设置布局的宽高，宽度直接采用父view传递过来的最大宽度，而不用考虑子view是否填满宽度，因为该布局的特性就是填满一行后，再换行
            // 高度根据设置的模式来决定采用所有子View的高度之和还是采用父view传递过来的高度
            setMeasuredDimension(totalWidth, resolveSize(totalHeight, heightMeasureSpec));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        if(!mNeedLayout || changed) {// 没有发生改变就不重新布局
            //这只最后一行标签
            if(mLines.size()>=1) {
                mLines.get(mLines.size()-1).setLastLine(true);
            }
            mNeedLayout = false;
            int left = getPaddingLeft();// 获取最初的左上点
            int top = getPaddingTop();
            final int linesCount = mLines.size();
            for(int i = 0; i<linesCount; i++) {
                final Line oneLine = mLines.get(i);
                oneLine.layoutView(left, top);// 布局每一行
                top += oneLine.mHeight+mVerticalSpacing;// 为下一行的top赋值
            }
        }
    }

    /**
     * 还原所有数据
     */
    private void restoreLine(){
        mLines.clear();
        mLine = new Line();
        mUsedWidth = 0;
    }

    /**
     * 新增加一行
     */
    private boolean newLine(){
        mLines.add(mLine);
        if(mLines.size()<mMaxLinesCount) {
            mLine = new Line();
            mUsedWidth = 0;
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v){
        if(v instanceof Checkable) {
            //CheckedTextView点击之后不会像checkBox那样自动改变checked状态 需要手动设置
            ( (Checkable)v ).setChecked(!( (Checkable)v ).isChecked());
            if(mSingleSelecte && ( (Checkable)v ).isChecked()) {
                if(lastSelected != null) {
                    lastSelected.setChecked(false);
                }
                lastSelected = (Checkable)v;
            }else {
                lastSelected = null;
            }
            if(mListener != null) {
                //选中状态表示被点击
                mListener.onItemSelected(v, indexOfChild(v));
                //                mListener.onItemSelected(v, (Integer)v.getTag(Consistent.ViewTag.view_tag5));
            }
        }
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

    /**
     * 代表着一行，封装了一行所占高度，该行子View的集合，以及所有View的宽度总和
     */
    class Line {
        private boolean lastLine;
        int mWidth = 0;// 该行中所有的子View累加的宽度
        int mHeight = 0;// 该行中所有的子View中高度的那个子View的高度
        List<View> views = new ArrayList<View>();

        public void addView(View view){// 往该行中添加一个
            views.add(view);
            mWidth += view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();
            mHeight = mHeight<childHeight ? childHeight : mHeight;// 高度等于一行中最高的View
        }

        public int getViewCount(){
            return views.size();
        }

        public void layoutView(int l, int t){// 布局
            int left = l;
            int top = t;
            int count = getViewCount();
            // 总宽度
            int layoutWidth = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
            // 剩余的宽度，是除了View和间隙的剩余空间
            int surplusWidth = layoutWidth-mWidth-mHorizontalSpacing*( count-1 );
            if(surplusWidth>=0) {// 剩余空间
                // 采用float类型数据计算后四舍五入能减少int类型计算带来的误差
                int splitSpacing = (int)( surplusWidth/count+0.5 );
                for(int i = 0; i<count; i++) {
                    final View view = views.get(i);
                    int childWidth = view.getMeasuredWidth();
                    int childHeight = view.getMeasuredHeight();
                    // 计算出每个View的顶点，是由最高的View和该View高度的差值除以2
                    int topOffset = (int)( ( mHeight-childHeight )/2.0+0.5 );
                    if(topOffset<0) {
                        topOffset = 0;
                    }
                    //                    if (!(mMaxLinesCount != Integer.MAX_VALUE && count < mMaxLinesCount && !mNeedExpend)) {
                    if(!lastLine && mNeedExpend) {
                        // 把剩余空间平均到每个View上
                        childWidth = childWidth+splitSpacing;
                        view.getLayoutParams().width = childWidth;
                        if(splitSpacing>0) {// View的长度改变了，需要重新measure
                            int widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
                            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
                            view.measure(widthMeasureSpec, heightMeasureSpec);
                        }
                    }
                    // 布局View
                    view.layout(left, top+topOffset, left+childWidth, top+topOffset+childHeight);
                    left += childWidth+mHorizontalSpacing; // 为下一个View的left赋值
                }
            }else {
                if(count == 1) {
                    View view = views.get(0);
                    view.layout(left, top, left+view.getMeasuredWidth(), top+view.getMeasuredHeight());
                }
            }
        }

        public void setLastLine(boolean lastLine){
            this.lastLine = lastLine;
        }
    }


    public ArrayList<Integer> getSelectedIndexs(){
        ArrayList<Integer> indexs = new ArrayList<>();
        for(int i = 0; i<getChildCount(); i++) {
            Checkable childAt = (Checkable)getChildAt(i);
            if(childAt.isChecked()) {
                indexs.add(i);
            }
        }
        return indexs;
    }

    public JFlowLayout clearAllSelectedIndex(){
        for(int i = 0; i<getChildCount(); i++) {
            Checkable childAt = (Checkable)getChildAt(i);
            if(childAt.isChecked()) {
                childAt.setChecked(false);
            }
        }
        return this;
    }

    public JFlowLayout setSingleSelected(boolean singleSelecte){

        mSingleSelecte = singleSelecte;
        return this;
    }

    @Override
    public void addView(View child){
        //        child.setTag(Consistent.ViewTag.view_tag5, getChildCount());
        super.addView(child);
        if(mChildClickAble && child instanceof TextView) {
            ( (TextView)child ).setOnClickListener(this);
        }
    }

    public JFlowLayout setChildClickAble(boolean clickAble){
        mChildClickAble = clickAble;
        return this;
    }

    public ImageView addMarkView(@DrawableRes int dres){
        ImageView iv = new ImageView(getContext());
        iv.setBackgroundResource(dres);
        addView(iv);
        return iv;
    }

    public CheckedTextView addContent(String str){
        CheckedTextView child = newCheckedItem(str);
        addView(child);
        return child;
    }

    public JFlowLayout addContents(String... str){
        for(String s : str) {
            addView(newCheckedItem(s));
        }
        return this;
    }

    public JFlowLayout addContents(List<String> str){
        for(String s : str) {
            addView(newCheckedItem(s));
        }
        return this;
    }

    // 初始化条件布局
    public CheckedTextView newCheckedItem(String content){
        CheckedTextView box = new CheckedTextView(getContext());
        if(mItemTvColorSelector == null) {
            box.setTextColor(mItemTvColor);
        }else {
            box.setTextColor(mItemTvColorSelector);
        }
        box.setLayoutParams(mItemLayoutParams);
        if(mItemBgselector != null) {
            box.setBackground(mItemBgselector);
        }else if(mItemBg_ids != Consistent.DEFAULTERROR) {
            box.setBackgroundResource(mItemBg_ids);
        }
        box.setTextSize(mTextSize);
        box.setText(content);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1) {
            box.setTextAlignment(TEXT_ALIGNMENT_GRAVITY);
        }
        box.setGravity(mItemGravity);
        return box;
    }

    public JFlowLayout setMaxCountEachLines(int count){
        mMaxChileEachLine = count;
        return this;
    }

    public interface OnItemSelectedListener {
        /**
         * CheckedTextView
         *
         * @param v
         *         CheckedTextView
         * @param position
         */
        void onItemSelected(View v, int position);
    }

    /**
     * 将dip转成px
     *
     * @param dip
     *         尺寸
     * @return 结果
     */
    public int dip2px(float dip){
        return (int)( dip*getResources().getDisplayMetrics().density+0.5f );
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     *         上下文
     * @param sp
     *         尺寸
     * @return 值
     */
    public static int sp2px(Context context, float sp){
        return (int)( sp*context.getResources().getDisplayMetrics().scaledDensity+0.5f );
    }

    public JFlowLayout setOnItemSelectedListener(OnItemSelectedListener listener){
        mListener = listener;
        return this;
    }

    public JFlowLayout setNeedExpend(boolean needExpend){
        mNeedExpend = needExpend;
        return this;
    }

    public JFlowLayout setItemTvColor(@NonNull int itemTvColor){
        mItemTvColor = itemTvColor;
        return this;
    }

    public JFlowLayout setItemTvColorSelector(@Size(value = 3) @ColorInt int... colors){
        mItemTvColorSelector = getColorStateList2(colors);
        return this;
    }

    public JFlowLayout setItemLayoutParams(LayoutParams itemLayoutParams){
        mItemLayoutParams = itemLayoutParams;
        return this;
    }

    /**
     * 单位sp直接设置到textview
     *
     * @param textSize
     * @return
     */
    public JFlowLayout setTextSize(int textSize){
        mTextSize = textSize;
        return this;
    }

    public JFlowLayout setItemBgselector(@Size(value = 2) @DrawableRes int... resIds){
        mItemBgselector = getListDrable(resIds);
        return this;
    }

    public JFlowLayout setItemBackgroundResource(int itemBg_ids){
        mItemBg_ids = itemBg_ids;
        return this;
    }

    public JFlowLayout setItemGravity(int itemGravity){
        mItemGravity = itemGravity;
        return this;
    }

    public JFlowLayout setHorizontalSpacing(int spacing){
        if(mHorizontalSpacing != spacing) {
            mHorizontalSpacing = spacing;
            requestLayoutInner();
        }
        return this;
    }

    public JFlowLayout setVerticalSpacing(int spacing){
        if(mVerticalSpacing != spacing) {
            mVerticalSpacing = spacing;
            requestLayoutInner();
        }
        return this;
    }

    public JFlowLayout setMaxLines(int count){
        if(mMaxLinesCount != count) {
            mMaxLinesCount = count;
            requestLayoutInner();
        }
        return this;
    }
}
