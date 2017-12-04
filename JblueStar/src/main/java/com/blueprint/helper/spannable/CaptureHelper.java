package com.blueprint.helper.spannable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.view.View;
import android.webkit.WebView;

/**
 * @another 江祖赟
 * @date 2017/11/8 0008.
 */
public class CaptureHelper {
    /**
     * 对WebView进行截屏，虽然使用过期方法，但在当前Android版本中测试可行
     *
     * @param webView
     * @return
     */
    public static Bitmap captureWebViewKitKat(WebView webView){
        Picture picture = webView.capturePicture();
        int width = picture.getWidth();
        int height = picture.getHeight();
        if(width>0 && height>0) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            picture.draw(canvas);
            return bitmap;
        }
        return null;
    }

    /**
     * 在Android5.0及以上版本，Android对WebView进行了优化，为了减少内存使用和提高性能，使用WebView加载网页时只绘制显示部分。
     * 如果我们不做处理，仍然使用上述代码截图的话，就会出现只截到屏幕内显示的WebView内容，其它部分是空白的情况。
     * 这时候，我们通过调用WebView.enableSlowWholeDocumentDraw()方法可以关闭这种优化，
     * 但要注意的是，该方法需要在WebView实例被创建前就要调用，否则没有效果。所以我们在WebView实例被创建前加入代码：
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.webkit.WebView.enableSlowWholeDocumentDraw();
        }

     作者：贝聊科技
     链接：https://juejin.im/post/5a016e8d518825295f5d57a4
     来源：掘金
     著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     * @param webView
     * @return
     */
    public static Bitmap captureWebViewLollipop(WebView webView){
        float scale = webView.getScale();
        int width = webView.getWidth();
        int height = (int)( webView.getContentHeight()*scale+0.5 );
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);
        return bitmap;
    }

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
    public static Bitmap CaptureView(View view){
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
}
