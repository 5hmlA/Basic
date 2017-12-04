package com.blueprint.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapHelper {

    private static final String TAG = BitmapHelper.class.getSimpleName();

    public static Bitmap drawable2bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            return bitmapDrawable.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        w = w>0 ? w : 1;
        h = h>0 ? h : 1;
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * TODO<图片圆角处理>
     *
     * @param srcBitmap
     *         源图片的bitmap
     * @param ret
     *         圆角的度数
     * @return Bitmap
     *
     * @throw
     */
    public static Bitmap getRoundImage(Bitmap srcBitmap, float ret){

        if(null == srcBitmap) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }

        int bitWidth = srcBitmap.getWidth();
        int bitHight = srcBitmap.getHeight();

        BitmapShader bitmapShader = new BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        RectF rectf = new RectF(0, 0, bitWidth, bitHight);

        Bitmap outBitmap = Bitmap.createBitmap(bitWidth, bitHight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        canvas.drawRoundRect(rectf, ret, ret, paint);
        canvas.save();
        canvas.restore();

        return outBitmap;
    }


    public static Bitmap getRoundImage(Bitmap source){

        if(null == source) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }
        int size = Math.min(source.getWidth(), source.getHeight());

        int width = ( source.getWidth()-size )/2;
        int height = ( source.getHeight()-size )/2;

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if(width != 0 || height != 0) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(-width, -height);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        source.recycle();
        return bitmap;
    }


    public static Bitmap getCircleImage(Bitmap source){
        return getCircleImage(source, 0);
    }

    public static Bitmap getCircleImage(Bitmap source, float filterRadius){
        if(null == source) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }
        int size = Math.min(source.getWidth(), source.getHeight());

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if(filterRadius>0) {
            paint.setMaskFilter(new BlurMaskFilter(filterRadius, BlurMaskFilter.Blur.NORMAL));
        }
        float r = size/2f;
        canvas.drawCircle(r, r, r-filterRadius/2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        paint.setXfermode(null);
        source.recycle();
        return bitmap;
    }

    /**
     * 图片大小没变只显示 中间圆形部分
     *
     * @param source
     * @param corner
     * @return
     */
    public static Bitmap getRCdImage(Bitmap source, float corner){

        if(null == source) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        RectF rcBg = new RectF();
        float btmapLeft = 0;
        float btmapTop = 0;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//创建和原图一样大的画布
        Canvas canvas = new Canvas(bitmap);
        if(width>height) {
            if(corner>height/2f) {
                float extra = corner-height/2f;
                btmapLeft = extra = Math.min(extra, width/2f-height/2f);
                width -= 2*extra;
            }
            corner = Math.min(corner, height/2f);
            rcBg.set(btmapLeft, 0, width+btmapLeft, height);
        }else {
            if(corner>width/2f) {
                float extra = corner-width/2f;
                btmapTop = extra = Math.min(extra, height/2f-width/2f);
                height -= 2*extra;
            }
            corner = Math.min(corner, width/2f);
            rcBg.set(0, btmapTop, width, height+btmapTop);
        }
        Log.d("getRCdImage", "width:"+width+"   height:"+height+"  corner:"+corner+"  btmapLeft"+btmapLeft);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        canvas.drawRoundRect(rcBg, corner, corner, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);//0,0坐标表示画在画布的哪个位置而不是 画bitmap中的哪个位置
        paint.setXfermode(null);
        source.recycle();
        return bitmap;
    }

    /**
     * 图片大小适应形状变化 图片靠下/右边
     * 效果等同于getCircleImage()
     *
     * @param source
     * @param corner
     * @return
     */
    public static Bitmap getRCdImage2(Bitmap source, float corner){
        float filterRadius = 6;
        float btLeft = 0;
        float btTop = 0;
        if(null == source) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        RectF rcBg = new RectF();
        if(width>height) {
            if(corner>height/2f) {
                float extra = corner-height/2f;
                btLeft -= extra = Math.min(extra, width/2f-height/2f);
                width -= 2*extra;
            }
            corner = Math.min(corner, height/2f);
            rcBg.set(filterRadius/2, filterRadius/2, width-filterRadius/2, height-filterRadius/2);
        }else {
            if(corner>width/2f) {
                float extra = corner-width/2f;
                btTop -= extra = Math.min(extra, height/2f-width/2f);
                height -= 2*extra;
            }
            corner = Math.min(corner, width/2f);
            rcBg.set(filterRadius/2, filterRadius/2, width-filterRadius/2, height-filterRadius/2);
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setMaskFilter(new BlurMaskFilter(filterRadius, BlurMaskFilter.Blur.NORMAL));
        paint.setDither(true);
        canvas.drawRoundRect(rcBg, corner, corner, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 2*btLeft, 2*btTop, paint);//btLeft,btTop坐标表示画在画布的哪个位置而不是 画bitmap中的哪个位置
        paint.setXfermode(null);
        source.recycle();
        return bitmap;
    }

    /**
     * 圆角 图片 当角度过大时 取中间部分
     *
     * @param source
     * @param corner
     * @return
     */
    public static Bitmap getRCdImage3(Bitmap source, float corner){
        return getRCdImage3(source, corner, 0, Color.BLUE);
    }

    /**
     * 圆角 图片 当角度过大时 取中间部分
     *
     * @param source
     * @param corner
     * @return
     */
    public static Bitmap getRCdImage31(Bitmap source, float corner){
        float filterRadius = 6;
        float btLeft = 0;
        float btTop = 0;
        if(null == source) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        RectF rcBg = new RectF();
        if(width>height) {
            if(corner>height/2f) {
                float extra = corner-height/2f;
                btLeft -= extra = Math.min(extra, width/2f-height/2f);
                width -= 2*extra;
            }
            corner = Math.min(corner, height/2f);
            rcBg.set(filterRadius, filterRadius, width-filterRadius, height-filterRadius);
        }else {
            if(corner>width/2f) {
                float extra = corner-width/2f;
                btTop -= extra = Math.min(extra, height/2f-width/2f);
                height -= 2*extra;
            }
            corner = Math.min(corner, width/2f);
            rcBg.set(filterRadius, filterRadius, width-filterRadius, height-filterRadius);
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setMaskFilter(new BlurMaskFilter(filterRadius, BlurMaskFilter.Blur.NORMAL));
        paint.setDither(true);
        canvas.drawRoundRect(rcBg, corner, corner, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, btLeft, btTop, paint);//btLeft,btTop坐标表示画在画布的哪个位置而不是 画bitmap中的哪个位置
        paint.setXfermode(null);
        source.recycle();
        return bitmap;
    }

    public static Bitmap getRCdImage3(Bitmap source, float corner, int frameWidth, int frameColor){
        if(null == source) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }
        float filterRadius = 6;
        int width = source.getWidth();
        int height = source.getHeight();
        RectF rcBg = new RectF();
        Rect centBt = new Rect();
        float btmapLeft = 0;
        float btmapTop = 0;
        if(width>height) {
            if(corner>height/2f) {
                float extra = corner-height/2f;
                btmapLeft = extra = Math.min(extra, width/2f-height/2f);
                width -= 2*extra;
            }
            corner = Math.min(corner, height/2f);
            centBt.set(( (int)btmapLeft ), 0, ( (int)( width+btmapLeft ) ), height);
            //            centBt.set(( (int)btmapLeft )+(int)Math.ceil(frameWidth/2f), (int)Math.ceil(frameWidth/2f),
            //                    ( (int)( width+btmapLeft ) )-(int)Math.ceil(frameWidth/2f), height-(int)Math.ceil(frameWidth/2f));
        }else {
            if(corner>width/2f) {
                float extra = corner-width/2f;
                btmapTop = extra = Math.min(extra, height/2f-width/2f);
                height -= 2*extra;
            }
            corner = Math.min(corner, width/2f);
            centBt.set(0, (int)btmapTop, width, (int)( height+btmapTop ));
            //            centBt.set((int)Math.ceil(frameWidth/2f), (int)btmapTop+(int)Math.ceil(frameWidth/2f),
            //                    width-(int)Math.ceil(frameWidth/2f), (int)( height+btmapTop )-(int)Math.ceil(frameWidth/2f));
        }
        rcBg.set((int)Math.ceil(frameWidth/2f)+filterRadius/2, (int)Math.ceil(frameWidth/2f)+filterRadius/2,
                width-(int)Math.ceil(frameWidth/2f)-filterRadius/2,
                height-(int)Math.ceil(frameWidth/2f)-filterRadius/2);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);

        Paint framepaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        framepaint.setStyle(Paint.Style.STROKE);
        framepaint.setColor(frameColor);
        framepaint.setStrokeWidth(frameWidth);
        framepaint.setMaskFilter(new BlurMaskFilter(filterRadius, BlurMaskFilter.Blur.NORMAL));

        canvas.drawRoundRect(rcBg, corner, corner, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        try {
            BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder
                    .newInstance(new ByteArrayInputStream(Bitmap2Bytes(source)), true);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap reginBtmap = bitmapRegionDecoder.decodeRegion(centBt, options);//只画图片的某个区域
            canvas.drawBitmap(reginBtmap, 0, 0, paint);
        }catch(IOException e) {
            e.printStackTrace();
        }
        paint.setXfermode(null);
        source.recycle();
        canvas.drawRoundRect(rcBg, corner, corner, framepaint);
        return bitmap;
    }

    /**
     * TODO<给图片添加指定颜色的边框>
     *
     * @param srcBitmap
     *         原图片
     * @param borderWidth
     *         边框宽度
     * @param color
     *         边框的颜色值
     * @return
     */
    public static Bitmap addFrameBitmap(Bitmap srcBitmap, float btCorner, float borderWidth, int color){
        if(srcBitmap == null) {
            Log.e(TAG, "the srcBitmap or borderBitmap is null");
            return null;
        }

        Paint paint = new Paint();
        //设置边框颜色
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(borderWidth);

        Canvas canvas = new Canvas(srcBitmap);

        RectF rec = new RectF(borderWidth/2, borderWidth/2, srcBitmap.getWidth()-borderWidth/2,
                srcBitmap.getHeight()-borderWidth/2);
        canvas.drawRoundRect(rec, btCorner, btCorner, paint);

        return srcBitmap;
    }

    /**
     * 为图片添加 边框
     * @param srcBitmap
     * @param btCorner
     * @param borderWidth
     * @param filterRadius
     * @param color
     * @return
     */
    public static Bitmap addFrameBitmap(Bitmap srcBitmap, float btCorner, float borderWidth, float filterRadius, int color){
        if(srcBitmap == null) {
            Log.e(TAG, "the srcBitmap or borderBitmap is null");
            return null;
        }

        Paint paint = new Paint();
        //设置边框颜色
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(borderWidth);
        if(filterRadius>0) {
            paint.setMaskFilter(new BlurMaskFilter(filterRadius, BlurMaskFilter.Blur.NORMAL));
        }

        Canvas canvas = new Canvas(srcBitmap);

        RectF rec = new RectF(borderWidth/2+filterRadius/2, borderWidth/2+filterRadius/2,
                srcBitmap.getWidth()-borderWidth/2-filterRadius/2, srcBitmap.getHeight()-borderWidth/2-filterRadius/2);
        canvas.drawRoundRect(rec, btCorner, btCorner, paint);

        return srcBitmap;
    }

    public static boolean saveBitmap(Bitmap bmp, String path){
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if(path.endsWith("png")) {
            format = Bitmap.CompressFormat.PNG;
        }
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(path);
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        return bmp.compress(format, quality, stream);
    }

    // 把Bitmap 转成 Byte
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // byte[]转换成Bitmap
    public static Bitmap Bytes2Bitmap(byte[] b){
        if(b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    public static Bitmap stringtoBitmap(String string){
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        }catch(Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String bitmaptoString(Bitmap bitmap){
        // 将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    //图片缩放
    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context){
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int h = (int)( newHeight*densityMultiplier );
        int w = (int)( h*photo.getWidth()/( (double)photo.getHeight() ) );
        photo = Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }

    //
    public static Bitmap view2Bitmap(View view, int bitmapWidth, int bitmapHeight){
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));

        return bitmap;
    }

    public static Bitmap ViewToBitmap(View view){
        if(null == view) {
            throw new IllegalArgumentException("parameter can't be null.");
        }
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 将view转成bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap loadBitmapFromView(View view){
        if(view == null) {
            return null;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        // 生成bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        // 利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        // 把view中的内容绘制在画布上
        view.draw(canvas);

        return bitmap;
    }

    //图片缩放
    public static Bitmap scaleBitmap(Bitmap orignBtmap, float scaleRate){
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRate, scaleRate);
        return Bitmap.createBitmap(orignBtmap, 0, 0, orignBtmap.getWidth(), orignBtmap.getHeight(), matrix, true);
    }

    /**
     * 裁剪部分图片
     * ------------------
     *
     * @param orignBtmap
     * @param scaleRate
     * @return
     */
    public static Bitmap scaleTRangeBitmap(Bitmap orignBtmap, float scaleRate){
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRate, scaleRate);
        Bitmap scaleBtmap = Bitmap
                .createBitmap(orignBtmap, 0, 0, orignBtmap.getWidth(), orignBtmap.getHeight(), matrix, true);
        int width = scaleBtmap.getWidth();
        int height = scaleBtmap.getHeight();
        //裁剪
        scaleBtmap = Bitmap.createBitmap(scaleBtmap, width/4, 0, width/2, height/2);
        return scaleBtmap;
    }

    /**
     * Compress image by pixel, this will modify image width/height.
     *
     * @param imgPath image path
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return
     */
    public static Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float ww = pixelW; //设置宽度为120f，可以明显看到图片缩小了
        float hh = pixelH; //设置高度为240f时，可以明显看到图片缩小了
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//表示不缩放
        if (w > h && w > ww) { //如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { //如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
        //return compress(bitmap, maxSize); //这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }


    public static Bitmap fastBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap){

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at="" quasimondo.com="">
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at="" kayenko.com="">
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if(canReuseInBitmap) {
            bitmap = sentBitmap;
        }else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if(radius<1) {
            return ( null );
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w*h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w-1;
        int hm = h-1;
        int wh = w*h;
        int div = radius+radius+1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = ( div+1 )>>1;
        divsum *= divsum;
        int dv[] = new int[256*divsum];
        for(i = 0; i<256*divsum; i++) {
            dv[i] = ( i/divsum );
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius+1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for(y = 0; y<h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for(i = -radius; i<=radius; i++) {
                p = pix[yi+Math.min(wm, Math.max(i, 0))];
                sir = stack[i+radius];
                sir[0] = ( p&0xff0000 )>>16;
                sir[1] = ( p&0x00ff00 )>>8;
                sir[2] = ( p&0x0000ff );
                rbs = r1-Math.abs(i);
                rsum += sir[0]*rbs;
                gsum += sir[1]*rbs;
                bsum += sir[2]*rbs;
                if(i>0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                }else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for(x = 0; x<w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer-radius+div;
                sir = stack[stackstart%div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if(y == 0) {
                    vmin[x] = Math.min(x+radius+1, wm);
                }
                p = pix[yw+vmin[x]];

                sir[0] = ( p&0xff0000 )>>16;
                sir[1] = ( p&0x00ff00 )>>8;
                sir[2] = ( p&0x0000ff );

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = ( stackpointer+1 )%div;
                sir = stack[( stackpointer )%div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for(x = 0; x<w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius*w;
            for(i = -radius; i<=radius; i++) {
                yi = Math.max(0, yp)+x;

                sir = stack[i+radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1-Math.abs(i);

                rsum += r[yi]*rbs;
                gsum += g[yi]*rbs;
                bsum += b[yi]*rbs;

                if(i>0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                }else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if(i<hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for(y = 0; y<h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000&pix[yi] )|( dv[rsum]<<16 )|( dv[gsum]<<8 )|dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer-radius+div;
                sir = stack[stackstart%div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if(x == 0) {
                    vmin[y] = Math.min(y+r1, hm)*w;
                }
                p = x+vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = ( stackpointer+1 )%div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return ( bitmap );
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return inSampleSize;
    }
}
