package com.blueprint.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @another 江祖赟
 * @date 2017/10/9 0009.
 */
public class PixelateHelper {
    /**
     * 普通图像－>像素图，zoneWidth为像素图的大像素的宽度
     **/
    public static Bitmap pixelate(Bitmap bitmap, int zoneWidth){
        return pixelate(bitmap, zoneWidth, 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * 普通图－>像素图，left、top、right、bottom可指定打马赛克区域
     **/
    public static Bitmap pixelate(Bitmap bitmap, int zoneWidth, int left, int top, int right, int bottom){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for(int i = left; i<right; i += zoneWidth) {
            for(int j = top; j<bottom; j += zoneWidth) {
                int color = bitmap.getPixel(i, j);
                paint.setColor(color);
                int gridRight = Math.min(w, i+zoneWidth);
                int gridBottom = Math.min(h, j+zoneWidth);
                canvas.drawRect(i, j, gridRight, gridBottom, paint);
            }
        }
        return result;
    }
}
