package com.blueprint.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

public class WebViewHelper {

//    http://blog.csdn.net/carson_ho/article/details/64904691
// 对于Android调用JS代码的方法有2种：
//            1. 通过WebView的loadUrl（）
//            2. 通过WebView的evaluateJavascript（）
//
// 对于JS调用Android代码的方法有3种：
//            1. 通过WebView的addJavascriptInterface（）进行对象映射
//            2. 通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url
//            3. 通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt（）方法回调拦截JS对话框alert()、confirm()、prompt（） 消息

    public static WebSettings setWebViewOptions(WebView webView) {
        Context context = webView.getContext().getApplicationContext();
        //支持获取手势焦点，输入用户名、密码或其他
        webView.requestFocusFromTouch();

        //设置编码
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setLoadsImagesAutomatically(true);  //支持自动加载图片
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        //设置缓存
        webView.getSettings().setDomStorageEnabled(true); //开启DOM storage API 功能
        webView.getSettings().setDatabaseEnabled(true); //开启database storage API 功能

//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局，不要设置，会改变原网页的布局
        webView.getSettings().supportMultipleWindows();  //多窗口
        webView.getSettings().setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
        webView.getSettings().setAllowFileAccess(true);//设置可以访问文件
        webView.getSettings().setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //设置WebView视图大小与HTML中viewport Tag的关系
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        //设置内置的缩放控件。
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);//隐藏原生的缩放控件

        File cacheFile = context.getCacheDir();
        if (cacheFile != null) {
            webView.getSettings().setAppCachePath(cacheFile.getAbsolutePath());
        }
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        ////优先使用缓存：
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        settWebViewDownloadListener(webView);
        return webView.getSettings();
    }

    private static void settWebViewDownloadListener(final WebView webView) {
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                webView.getContext().startActivity(intent);
            }
        });
    }

    public static void loadHtmlAsWeb(WebView webView, String html){
        webView.loadData(html,"text/html; charset=utf-8", "utf-8");
    }

    public static void java2js(WebView webView,String jsMathWithParam){
//        使用 loadUrl() 方法实现 Java 调用 Js 功能时，必须放置在主线程中，否则会发生崩溃异常
//        webView调用js的基本格式为webView.loadUrl(“javascript:methodName(parameterValues)”)
//        webView.loadUrl("javascript:javaCallJs(" + "'Message From Java'" + ")");
        webView.loadUrl("javascript:"+jsMathWithParam);
    }
}
