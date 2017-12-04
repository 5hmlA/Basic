package com.blueprint.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.blueprint.helper.DrawHelper;
import com.blueprint.helper.LogHelper;

import static com.blueprint.helper.DpHelper.dp2px;
import static com.blueprint.helper.DrawHelper.getConverserColor;

/**
 * android:maxLines="3"不为0 就是正方形 圆角失效
 * android:textSize="5dp" 小于0为圆形，大于0为圆角
 */
@SuppressLint("AppCompatCustomView")
public class JDimImageView extends ImageView {
    //    PorterDuff.Mode枚举值：
    //            1.PorterDuff.Mode.CLEAR
    //    所绘制不会提交到画布上。
    //            2.PorterDuff.Mode.SRC
    //            显示上层绘制图片
    //3.PorterDuff.Mode.DST
    //            显示下层绘制图片
    //4.PorterDuff.Mode.SRC_OVER
    //    正常绘制显示，上下层绘制叠盖。
    //            5.PorterDuff.Mode.DST_OVER
    //    上下层都显示。下层居上显示。
    //            6.PorterDuff.Mode.SRC_IN
    //    取两层绘制交集。显示上层。
    //            7.PorterDuff.Mode.DST_IN
    //    取两层绘制交集。显示下层。
    //            8.PorterDuff.Mode.SRC_OUT
    //    取上层绘制非交集部分。
    //            9.PorterDuff.Mode.DST_OUT
    //    取下层绘制非交集部分。
    //            10.PorterDuff.Mode.SRC_ATOP
    //            取下层非交集部分与上层交集部分
    //11.PorterDuff.Mode.DST_ATOP
    //            取上层非交集部分与下层交集部分
    //12.PorterDuff.Mode.XOR
    //    //变暗
    //13.PorterDuff.Mode.DARKEN
    //    //调亮
    //14.PorterDuff.Mode.LIGHTEN
    //    //用于颜色滤镜
    //15.PorterDuff.Mode.MULTIPLY
    //16.PorterDuff.Mode.SCREEN
    public static final int MASK_HINT_COLOR = 0x99000000;
    /**
     * 变暗
     */
    public static final float[] SELECTED_DARK = new float[]{1, 0, 0, 0, -80, 0, 1, 0, 0, -80, 0, 0, 1, 0, -80, 0, 0, 0, 1, 0};
    /**
     * 变亮
     */

    public static final float[] SELECTED_BRIGHT = new float[]{1, 0, 0, 0, 80, 0, 1, 0, 0, 80, 0, 0, 1, 0, 80, 0, 0, 0, 1, 0};

    /**
     * 高对比度
     */
    public static final float[] SELECTED_HDR = new float[]{5, 0, 0, 0, -250, 0, 5, 0, 0, -250, 0, 0, 5, 0, -250, 0, 0, 0, 1, 0};

    /**
     * 高饱和度
     */
    public static final float[] SELECTED_HSAT = new float[]{(float)3, (float)-2, (float)-0.2, 0, 50, -1, 2, -0, 0, 50, -1, -2, 4, 0, 50, 0, 0, 0, 1, 0};

    /**
     * 改变色调
     */
    public static final float[] SELECTED_DISCOLOR = new float[]{(float)-0.5, (float)-0.6, (float)-0.8, 0, 0, (float)-0.4, (float)-0.6, (float)-0.1, 0, 0, (float)-0.3, 2, (float)-0.4, 0, 0, 0, 0, 0, 1, 0};

    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.maxLines};

    /**
     * 圆形模式
     */
    private static final int MODE_CIRCLE = 1;
    /**
     * 正方形 图片
     */
    private static final int MODE_SQUARE = 3;
    /**
     * 普通模式
     */
    private static final int MODE_NONE = 0;
    /**
     * 圆角模式
     */
    private static final int MODE_ROUND = 2;
    private Paint mPaint;
    private int mCurrMode = MODE_ROUND;

    /**
     * 圆角半径
     */
    private int mCorner_Radius = ( 0 );
    private BitmapShader mBitmapShader;
    private RectF mBitmapRect;
    private float mLoadProgress;
    private int mW;
    private int mH;
    private int mMinSide;
    private RectF mCenterArc = new RectF();
    private Paint mProgPaint;
    private String mUrl;
    private String mDrawMask;
    private RectF mDrawMaskRectf = new RectF();
    public static final String GIF = "gif";
    public static final String VIDEO = "mp4";
    private Paint mDrawMaskPaint;
    private float mFontHeight;
    private float offSet = dp2px(2);
    private Bitmap mDrawable2Bitmap;
    private int mDrawMaskBgColor = Color.parseColor("#F87534");
    private ColorMatrixColorFilter mDarkColorFilter = new ColorMatrixColorFilter(SELECTED_DARK);
    //变亮
    private ColorMatrixColorFilter mBlightColorFilter = new ColorMatrixColorFilter(SELECTED_BRIGHT);
    //高对比度
    private ColorMatrixColorFilter mHDRColorFilter = new ColorMatrixColorFilter(SELECTED_HDR);
    //高饱和度
    private ColorMatrixColorFilter mHSATColorFilter = new ColorMatrixColorFilter(SELECTED_HSAT);
    private ColorMatrixColorFilter mDISCOLORColorFilter = new ColorMatrixColorFilter(SELECTED_DISCOLOR);
    /**
     * 以剪切canvas的方式 处理圆角
     */
    public boolean clipTransform = true;
    private Path mClipTransformPath = new Path();

    {
        mDrawMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG) {
            {
                setColor(mDrawMaskBgColor);
                setTextAlign(Align.CENTER);
                setTextSize(dp2px(12));
            }
        };
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG) {
            {
                setTextAlign(Align.CENTER);
                setTextSize(dp2px(12));
            }
        };
        mProgPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG) {
            {
                setColor(Color.parseColor("#F3F3F5"));
                setStrokeWidth(dp2px(2));
            }
        };
    }

    public JDimImageView(Context context){
        this(context, null);
    }

    public JDimImageView(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mCorner_Radius = a.getDimensionPixelSize(0, 0);
        mCurrMode = a.getInt(1, MODE_NONE);
        if(mCurrMode == MODE_NONE) {
            if(mCorner_Radius == 0) {
                //普通
                mCurrMode = MODE_NONE;
            }else if(mCorner_Radius<0) {
                //圆形
                mCurrMode = MODE_CIRCLE;
            }else {
                mCurrMode = MODE_ROUND;
            }
        }else {
            //正方形
            mCurrMode = MODE_SQUARE;
        }
        a.recycle();
    }

    @Override
    protected void drawableStateChanged(){
        super.drawableStateChanged();
        //http://www.jianshu.com/p/84bb18f4f6c2
        //        什么时候显示pressed状态？
        //        由于我们的页面是一个可以滑动并且可以下拉刷新的页面，
        //        因此，其页面上的按钮在处理touch事件时就需要考虑区分点击事件和滑动事件。具体到这里说的按钮的点击状态的问题，
        //        我们给按钮设置了OnTouchListener，肯定不能在收到ACTION_DOWN事件后立刻就设置着色（即设置为pressed状态），
        //        因为此时用户手刚接触屏幕，接下来可能是短点击，也可能是滑动，所以需要有一个延时来判断具体是哪种动作，
        //        这个延时的时长，系统有一个特定的值，可以通过 ViewConfiguration 获取。
        //        还有一个小问题
        //        实现到这一步，还有个问题：当点击稍微快一些的时候，经常是看不到按钮的pressed状态，即着色后的效果的。
        //        原因稍一想也很明显：前面讲到，为了区分滑动和点击，我们并没有在ACTION_DOWN的时候立刻着色，而是有一个延时，
        //        那么如果点击的时候从ACTION_DOWN到ACTION_UP的时间小于这个延时，就没有触发着色。
        //        怎么解决？
        //        测试发现用StateListDrawable（即selector）的方式是没问题的，点击再快也有pressed效果。
        //        而且既然这个时延是从系统获得，那么我们不妨看看源码中是怎么解决这个问题的。
        //        开始我猜测源码应该是在ACTION_UP时候做了一次置为pressed状态的动作，然后一定短时间后再取消状态。这样视觉上可以达到效果。
        int[] drawableState = getDrawableState();
        boolean isWindowFocus = false;
        boolean isSelected = false;
        for(int state : drawableState) {
            if(state == android.R.attr.state_window_focused) {
                mBitmapShader = null;
                isWindowFocus = true;
            }
            if(isWindowFocus && ( state == android.R.attr.state_selected || state == android.R.attr.state_pressed || state == android.R.attr.state_long_pressable )) {
                setColorFilter(mDarkColorFilter);
                isSelected = true;
            }
            //按下                  state_window_focused,state_enabled,state_pressed,state_accelerated
            //提起/重现             state_window_focused,state_enabled,state_accelerated
        }
        if(isWindowFocus && !isSelected) {
            clearColorFilter();
        }
    }

    //  http://www.jianshu.com/p/84bb18f4f6c2
    //    @Override
    //    public boolean onTouchEvent(MotionEvent event){
    //        mBitmapShader = null;
    //        switch(event.getAction()) {
    //            case MotionEvent.ACTION_DOWN:
    //                //                setColorFilter(mDarkColorFilter);
    //                break;
    //            case MotionEvent.ACTION_CANCEL:
    //            case MotionEvent.ACTION_UP:
    //                                clearColorFilter();
    //                break;
    //        }
    //        return super.onTouchEvent(event);
    //    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmapRect = new RectF(getPaddingLeft(), getPaddingTop(), getWidth()-getPaddingRight(),
                getHeight()-getPaddingBottom());
        mCorner_Radius = Math.min(mCorner_Radius, Math.min(getWidth(), getHeight()));

        mW = w;
        mH = h;
        mMinSide = Math.min(w, h);
        float innerCircle = mMinSide/3f-dp2px(3);
        mCenterArc.set(mW/2f-innerCircle, mH/2f-innerCircle, mW/2f+innerCircle, mH/2f+innerCircle);
        mFontHeight = DrawHelper.getFontHeight(mPaint);
        float textWidth = mPaint.measureText(GIF);
        int right = mW-getPaddingRight();
        mDrawMaskRectf.set(right-( textWidth+mFontHeight*3f/2 ), mH-mFontHeight*2, right, mH);
        if(mCurrMode == MODE_CIRCLE) {
            mCorner_Radius = w/2;
        }
        mClipTransformPath.addRoundRect(mBitmapRect, mCorner_Radius, mCorner_Radius, Path.Direction.CCW);

    }

    public void setDimMask(){
        setColorFilter(MASK_HINT_COLOR, PorterDuff.Mode.DARKEN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        /**
         * 当模式为圆形模式的时候，我们强制让宽高一致
         */
        if(mCurrMode == MODE_CIRCLE || mCurrMode == MODE_SQUARE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int result = Math.min(getMeasuredHeight(), getMeasuredWidth());
            setMeasuredDimension(result, result);
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    protected void onDraw(Canvas canvas){
//        super.onDraw(canvas);
        try {
            Drawable mDrawable = getDrawable();
            Matrix mDrawMatrix = getImageMatrix();
            if(mDrawable == null || mBitmapRect == null) {
                return; // couldn't resolve the URI
            }

            if(mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
                return;     // nothing to draw (empty bounds)
            }

            if(mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
                mDrawable.draw(canvas);
            }else {
                final int saveCount = canvas.getSaveCount();
                canvas.save();

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
                    if(getCropToPadding()) {
                        final int scrollX = getScrollX();
                        final int scrollY = getScrollY();
                        canvas.clipRect(scrollX+getPaddingLeft(), scrollY+getPaddingTop(),
                                scrollX+getRight()-getLeft()-getPaddingRight(),
                                scrollY+getBottom()-getTop()-getPaddingBottom());
                    }
                }
                canvas.translate(getPaddingLeft(), getPaddingTop());
                if(!clipTransform && mCurrMode == MODE_CIRCLE) {//当为圆形模式的时候
                    if(mBitmapShader == null) {
                        drawable2Bitmap(mDrawable);
                        mBitmapShader = new BitmapShader(mDrawable2Bitmap, Shader.TileMode.CLAMP,
                                Shader.TileMode.CLAMP);
                    }
                    mPaint.setShader(mBitmapShader);
                    canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2, mPaint);
                }else if(!clipTransform && mCurrMode == MODE_ROUND) {//当为圆角模式的时候
                    if(mBitmapShader == null) {
                        drawable2Bitmap(mDrawable);
                        mBitmapShader = new BitmapShader(mDrawable2Bitmap, Shader.TileMode.CLAMP,
                                Shader.TileMode.CLAMP);
                    }
                    mPaint.setShader(mBitmapShader);
                    canvas.drawRoundRect(mBitmapRect, mCorner_Radius, mCorner_Radius, mPaint);
                }else {
                    if(clipTransform && ( mCurrMode == MODE_ROUND || mCurrMode == MODE_CIRCLE )) {
                        canvas.clipPath(mClipTransformPath);
                    }
                    if(mDrawMatrix != null) {
                        canvas.concat(mDrawMatrix);
                    }
                    mDrawable.draw(canvas);
                }
                canvas.restoreToCount(saveCount);
            }
        }catch(Exception e) {
            Log.e("JDimImageView", "JDimImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
        //draw progress
        if(mLoadProgress>0 && mLoadProgress<1) {
            mProgPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mW/2f, mH/2f, mMinSide/3f, mProgPaint);
            mProgPaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(mCenterArc, 0, 360*mLoadProgress, true, mProgPaint);
        }
        //draw mask  gif/video
        if(!TextUtils.isEmpty(mDrawMask)) {
            if(mDrawable2Bitmap != null) {
                int pixelColor = mDrawable2Bitmap
                        .getPixel(( (int)mDrawMaskRectf.centerX() ), ( (int)mDrawMaskRectf.centerY() ));
                drawMask(canvas, getConverserColor(pixelColor), pixelColor);
                //                drawMask(canvas, pixelColor, getConverserColor(pixelColor));
            }else {
                drawMask(canvas, mDrawMaskBgColor, getConverserColor(mDrawMaskBgColor));
            }
        }
    }

    private void drawMask(Canvas canvas, int textColor, int bgColor){
        mDrawMaskPaint.setColor(bgColor);
        mDrawMaskPaint.setColorFilter(mHSATColorFilter);
        canvas.drawRoundRect(mDrawMaskRectf, mCorner_Radius, mCorner_Radius, mDrawMaskPaint);
        mDrawMaskPaint.setColor(textColor);
        mDrawMaskPaint.setColorFilter(mDarkColorFilter);
        canvas.drawText(GIF, mDrawMaskRectf.centerX(), mDrawMaskRectf.centerY()+mFontHeight/2f, mDrawMaskPaint);
    }

    @Override
    protected void onDetachedFromWindow(){
        try {
            super.onDetachedFromWindow();
        }catch(Exception e) {
            Log.e("JDimImageView", "JDimImageView  -> onDetachedFromWindow() ");
        }
    }

    @Override
    public void setImageResource(int resId){
        mBitmapShader = null;
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable){
        mBitmapShader = null;
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm){
        mBitmapShader = null;
        super.setImageBitmap(bm);
    }

    /**
     * drawable转换成bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable == null) {
            return null;
        }
        try {
            mDrawable2Bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mDrawable2Bitmap);
            //根据传递的scaletype获取matrix对象，设置给bitmap
            Matrix matrix = getImageMatrix();
            if(matrix != null) {
                canvas.concat(matrix);
            }
            drawable.draw(canvas);
        }catch(OutOfMemoryError error) {
            LogHelper.Log_e("[JDimImageView]--内存溢出--");
        }
        return mDrawable2Bitmap;
    }

    /**
     * 会导致圆角无效
     *
     * @return
     */
    public JDimImageView squareMode(){
        mCurrMode = MODE_SQUARE;
        return this;
    }

    /**
     * 非正方形才有效  默认为普通图片设置圆角有效
     *
     * @param radius
     * @return
     */
    public JDimImageView setImgRadius(int radius){
        mCorner_Radius = radius;
        if(mCorner_Radius == 0) {
            //普通
            mCurrMode = MODE_NONE;
        }else if(mCorner_Radius<0) {
            //圆形
            mCurrMode = MODE_CIRCLE;
        }else {
            mCurrMode = MODE_ROUND;
        }
        return this;
    }

    public JDimImageView setLoadProgress2(@FloatRange(from = 0, to = 1) float loadProgress){
        if(loadProgress == 0 || loadProgress>mLoadProgress) {
            mLoadProgress = loadProgress;
            postInvalidate();
        }
        return this;
    }

    public JDimImageView setLoadProgress(@FloatRange(from = 0, to = 1) float loadProgress){
        mLoadProgress = loadProgress;
        postInvalidate();
        return this;
    }

    public JDimImageView setLoadProgColor(@ColorInt int color){
        mProgPaint.setColor(color);
        return this;
    }

    public JDimImageView bindUrl(String url){
        mUrl = url;
        if(!TextUtils.isEmpty(url)) {
            if(url.endsWith(GIF)) {
                mDrawMask = GIF;
            }else if(url.endsWith(VIDEO)) {
                mDrawMask = VIDEO;
            }
        }
        return this;
    }

    public boolean checkUrl(String url){
        return !TextUtils.isEmpty(mUrl) && mUrl.equals(url);
    }

    public String getUrl(){
        return mUrl;
    }
}
