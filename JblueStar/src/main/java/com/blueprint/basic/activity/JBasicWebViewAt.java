package com.blueprint.basic.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.blueprint.R;
import com.blueprint.basic.JBasePresenter;

import static com.blueprint.helper.WebViewHelper.setWebViewOptions;


/**
 * @another 江祖赟
 * @date 2017/6/27.
 */
public class JBasicWebViewAt extends JBaseTitleStateActivity {
    protected WebView mWebView;
    protected ProgressBar mProgressBar;
    protected String mUrl;
    public static final String URL = "url";

    public static void start(Activity activity, final String url){
        Intent intent = new Intent(activity, JBasicWebViewAt.class);
        intent.putExtra(URL, url);
        activity.startActivity(intent);
    }

    @Override
    protected JBasePresenter initPresenter(){
        mUrl = getIntent().getStringExtra(URL);
        return null;
    }

    @Override
    protected boolean setEnableOuterSwipeRefresh(){
        return true;
    }

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){
        View rootView = inflater.inflate(R.layout.jbasic_webview_layout, container);
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.jcommon_webfrgmt_pb);
        mWebView = (WebView)rootView.findViewById(R.id.jcommon_webfrgmt_wv);
        setWebViewOptions(mWebView);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        moreConfig2WebView(mWebView);
        mProgressBar.setMax(100);
        mWebView.loadUrl(getLoadUrl());
    }

    protected void moreConfig2WebView(WebView webView){

    }

    @Override
    public void onRefresh(){
        mWebView.loadUrl(mUrl);
    }

    /**
     * 需要加载的Url<br/>
     * assert中的文件：file:///android_asset/about.htm<br/>
     * 网页： http://www.jianshu.com/users/6725c8e8194f/<br/>
     * <p/>
     *
     * @return 需要加载的Url
     */
    protected String getLoadUrl(){
        return mUrl;
    }

    public boolean canGoBack(){
        return mWebView != null && mWebView.canGoBack();
    }

    public void goBack(){
        if(mWebView != null) {
            mWebView.goBack();
        }
    }

    @Override
    public void onBackPressed(){
        if(canGoBack()) {
            goBack();
        }else {
            super.onBackPressed();
        }
    }

    //WebViewClient就是帮助WebView处理各种通知、请求事件的。
    class MyWebViewClient extends WebViewClient {
        //不复写 会出现 webview加载url跳转到系统浏览器，用户体验非常的差
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url){
            super.onPageFinished(view, url);

            onPageLoadFinished(view, url);
            mMultiStateLayout.showStateSucceed();
        }

        //        shouldOverrideUrlLoading(WebView view, String url)  最常用的，比如上面的。
        //        //在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。
        //        //比如获取url，查看url.contains(“add”)，进行添加操作
        //
        //        shouldOverrideKeyEvent(WebView view, KeyEvent event)
        //        //重写此方法才能够处理在浏览器中的按键事件。
        //
        //        onPageStarted(WebView view, String url, Bitmap favicon)
        //        //这个事件就是开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。
        //
        //        onPageFinished(WebView view, String url)
        //        //在页面加载结束时调用。同样道理，我们可以关闭loading 条，切换程序动作。
        //
        //        onLoadResource(WebView view, String url)
        //        // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
        //
        //        onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        //        // (报告错误信息)
        //
        //        doUpdateVisitedHistory(WebView view, String url, boolean isReload)
        //        //(更新历史记录)
        //
        //        onFormResubmission(WebView view, Message dontResend, Message resend)
        //        //(应用程序重新请求网页数据)
        //
        //        onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,String realm)
        //        //（获取返回信息授权请求）
        //
        //        onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
        //        //重写此方法可以让webview处理https请求。
        //
        //        onScaleChanged(WebView view, float oldScale, float newScale)
        //        // (WebView发生改变时调用)
        //
        //        onUnhandledKeyEvent(WebView view, KeyEvent event)
        //        //（Key事件未被加载时调用）
    }

    //WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
    class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress){
            mProgressBar.setProgress(newProgress);
            if(newProgress == 0) {
                loadStart();
            }else if(newProgress>98) {
                mProgressBar.setVisibility(View.GONE);
                mMultiStateLayout.showStateSucceed();
            }else {
                mMultiStateLayout.showStateLoading();
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        //        //获取Web页中的title用来设置自己界面中的title
        //        //当加载出错的时候，比如无网络，这时onReceiveTitle中获取的标题为 找不到该网页,
        //        //因此建议当触发onReceiveError时，不要使用获取到的title
        @Override
        public void onReceivedTitle(WebView view, String title){
            mTitleBar.setTitle(title);
        }
        //
        //        @Override
        //        public void onReceivedIcon(WebView view, Bitmap icon) {
        //            //
        //        }
        //
        //        @Override
        //        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        //            //
        //            return true;
        //        }
        //
        //        @Override
        //        public void onCloseWindow(WebView window) {
        //        }
        //
        //        //处理alert弹出框，HTML 弹框的一种方式
        //        @Override
        //        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        //            //
        //            return true;
        //        }
        //
        //        //处理confirm弹出框
        //        @Override
        //        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
        //                result) {
        //            //
        //            return true;
        //        }
        //
        //        //处理prompt弹出框
        //        @Override
        //        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        //            //
        //            return true;
        //        }
    }

    protected void loadStart(){
    }

    protected void onPageLoadFinished(WebView view, String url){

    }

    @Override
    public void onPause(){
        super.onPause();
        if(mWebView != null) {
            mWebView.onPause();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        if(mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mWebView != null) {
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.removeAllViews();
            ViewGroup parent = (ViewGroup)mWebView.getParent();
            if(parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.setTag(null);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
        }
    }
}
