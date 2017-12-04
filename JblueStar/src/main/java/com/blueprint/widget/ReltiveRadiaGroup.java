package com.blueprint.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

/**
 * @another 江祖赟
 * @date 2017/11/9 0009.
 */
@SuppressLint("ViewConstructor")
public class ReltiveRadiaGroup extends RelativeLayout {

    // holds the checked id; the selection is empty by default
    private int mCheckedId = -1;
    // tracks children radio buttons checked state
    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    private ReltiveRadiaGroup.OnCheckedChangeListener mOnCheckedChangeListener;

    public ReltiveRadiaGroup(Context context){
        super(context);
        init();
    }

    public ReltiveRadiaGroup(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public ReltiveRadiaGroup(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mChildOnCheckedChangeListener = new CheckedStateTracker();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        // checks the appropriate radio button as requested in the XML file
        if(mCheckedId != -1) {
            mProtectFromCheckedChange = true;
            setCheckedStateForView(mCheckedId, true);
            mProtectFromCheckedChange = false;
            setCheckedId(mCheckedId);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params){
        if(child instanceof CompoundButton) {
            ( (CompoundButton)child ).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            final Checkable button = (Checkable)child;
            if(button.isChecked()) {
                mProtectFromCheckedChange = true;
                if(mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
                setCheckedId(child.getId());
            }
        }
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params){
        if(child instanceof CompoundButton) {
            ( (CompoundButton)child ).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            final Checkable button = (Checkable)child;
            if(button.isChecked()) {
                mProtectFromCheckedChange = true;
                if(mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
                setCheckedId(child.getId());
            }
        }

        super.addView(child, index, params);
    }

    /**
     * <p>Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.</p>
     *
     * @param id
     *         the unique id of the radio button to select in this group
     * @see #getCheckedRadioButtonId()
     * @see #clearCheck()
     */
    public void check(@IdRes int id){
        // don't even bother
        if(id != -1 && ( id == mCheckedId )) {
            return;
        }

        if(mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if(id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

    private void setCheckedId(@IdRes int id){
        mCheckedId = id;
        if(mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked){
        View checkedView = findViewById(viewId);
        if(checkedView != null && checkedView instanceof Checkable) {
            ( (Checkable)checkedView ).setChecked(checked);
        }
    }

    /**
     * <p>Returns the identifier of the selected radio button in this group.
     * Upon empty selection, the returned value is -1.</p>
     *
     * @return the unique id of the selected radio button in this group
     *
     * @attr ref android.R.styleable#RadioGroup_checkedButton
     * @see #check(int)
     * @see #clearCheck()
     */
    @IdRes
    public int getCheckedButtonId(){
        return mCheckedId;
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and {@link #getCheckedRadioButtonId()} returns
     * null.</p>
     *
     * @see #check(int)
     * @see #getCheckedRadioButtonId()
     */
    public void clearCheck(){
        check(-1);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener
     *         the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(ReltiveRadiaGroup.OnCheckedChangeListener listener){
        mOnCheckedChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p){
        return p instanceof RelativeLayout.LayoutParams;
    }

    @Override
    protected RelativeLayout.LayoutParams generateDefaultLayoutParams(){
        return new ReltiveRadiaGroup.LayoutParams(ReltiveRadiaGroup.LayoutParams.WRAP_CONTENT,
                ReltiveRadiaGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public CharSequence getAccessibilityClassName(){
        return RadioGroup.class.getName();
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs){
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h){
            super(w, h);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams p){
            super(p);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source){
            super(source);
        }

        /**
         * <p>Fixes the child's width to
         * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and the child's
         * height to  {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
         * when not specified in the XML file.</p>
         *
         * @param a
         *         the styled attributes set
         * @param widthAttr
         *         the width attribute to fetch
         * @param heightAttr
         *         the height attribute to fetch
         */
        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr){

            if(a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            }else {
                width = WRAP_CONTENT;
            }

            if(a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            }else {
                height = WRAP_CONTENT;
            }
        }
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group
         *         the group in which the checked radio button has changed
         * @param checkedId
         *         the unique identifier of the newly checked radio button
         */
        public void onCheckedChanged(ReltiveRadiaGroup group, @IdRes int checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            // prevents from infinite recursion
            if(mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            if(mCheckedId != -1) {
                setCheckedStateForView(mCheckedId, false);
            }
            mProtectFromCheckedChange = false;

            int id = buttonView.getId();
            setCheckedId(id);
            requestLayout();
        }
    }

}
