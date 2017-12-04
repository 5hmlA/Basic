package com.blueprint;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.blueprint.Consistent.ErrorCode.CONNECT404;
import static com.blueprint.Consistent.ErrorCode.ERROR_DATA;
import static com.blueprint.Consistent.ErrorCode.ERROR_EMPTY;
import static com.blueprint.Consistent.ErrorCode.ERROR_NETERROR;
import static com.blueprint.Consistent.ErrorCode.ERROR_NONET;
import static com.blueprint.Consistent.ErrorCode.HTTP404;

public class Consistent {
    public static final String DEFAULTSTR = "--";
    public static final String DIFF_TYPE = "doundle_typle";
    public static final String DIFF_INDEX = "doundle_index";
    public static final String BUND_TAG = "bund_extra";
    public static final int VIEWTAG_ERROR_MSG = 0x12fcde1;
    public static final String SPLIT_DOS = ", ";
    public static final String CONTACT_DOS = ",";

    public static final int DEFAULTERROR = -12306;

    public static interface PageState {
        int STATE_FIRST_LOAD = 0x10;
        /**
         * 上拉加载更多
         */
        int STATE_UP2LOAD_MORE = 0x11;
        /**
         * 下拉刷新
         */
        int STATE_DOWN_REFRESH = 0x12;
        int STATE_DATA_ERROR = 0x13;
        int STATE_DATA_EMPTY = 0x14;
        int STATE_DATA_SUCCESS = 0x15;
    }

    public static class ErrorData {
        public int errorCode;
        public String errorMsg;

        public ErrorData(@Consistent.ErrorCode int errorCode, String errorMsg){
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }
    }

    public static interface LoadMoreWrapper {
        int NEED_UP2LOAD_MORE = 1;
        int NON_UP2LOAD_MORE = 0;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HTTP404, CONNECT404, ERROR_EMPTY, ERROR_NONET, ERROR_NETERROR, ERROR_DATA})
    public @interface ErrorCode {
        int HTTP404 = -404;
        int CONNECT404 = 404;
        /**
         * 数据为空
         */
        int ERROR_EMPTY = 0;
        /**
         * 没有网络
         */
        int ERROR_NONET = 1;
        /**
         * 网络错误
         */
        int ERROR_NETERROR = 2;
        /**
         * 数据异常
         */
        int ERROR_DATA = 3;
    }

    public interface Common {
        String DIFF_TYPE = "doundle_typle";
        String DIFF_INDEX = "doundle_index";
        String BUND_TAG = "bund_extra";
        String INTENT_URL = "intent_url";
    }

    public interface ViewTag {
        int view_tag = 0x12345678;
        int view_tag2 = 0x1234567a;
        int view_tag3 = 0x1234567b;
        int view_tag4 = 0x1234567c;
        int view_tag5 = 0x1234567d;
        int value_tag = 0x1234567d;
    }

    public interface TransitionName {
        String TRANS_AVATAR = "javatar";
        String TRANS_IMG = "jimageview";
        String TRANS_IMG2 = "jimageview2";
        String TRANS_TV = "jtextview";
        String TRANS_TV2 = "jtextview2";
        String TRANS_BTN = "jbuttom";
        String TRANS_BTN2 = "jbuttom2";
    }

    public interface TEMP {
        String HTML = "https://juejin.im/entry/5860c0b4128fe1006dfb2f20";
        String URLT = "http://zhibo.4399youpai.com/detail/13816.HTML";
        String JIANSHU = "http://www.jianshu.com/p/162b36a84e8a";
        String BAIDU = "https://www.baidu.com/";
        String PIKAQ = "http://e.hiphotos.baidu"+".com/zhidao/wh%3D450%2C600/sign=918e9a2b45166d2238221d90731325c1/83025aafa40f4bfb3f9883b4054f78f0f6361881.jpg";
        String AVATAR = "http://wanzao2.b0.upaiyun.com/143851672805937d3d539b6003af3d660860e342ac65c1138b682.jpg";
        String PIC1 = "http://www.qihun8.com/Article/UploadFiles/201612/2016122311184095.jpg";
        String ICON = "http://img3.imgtn.bdimg.com/it/u=3528666336,2810085622&fm=214&gp=0.jpg";
        String DOWNLOAD_TEST1 = "http://appdl.hicloud.com/dl/appdl/application/apk/0b/0b3ced4070ac40ecaf051583251ad0ab/com.huawei.appmarket.1707131052.apk?sign=mw@mw1501342103576";
        String DOWNLOAD_TEST2 = "http://dldir1.qq.com/weixin/android/weixin6330android920.apk";
        String DOWNLOAD_TEST3 = "http://s1.music.126.net/download/android/CloudMusic_official_3.7.3_153912.apk";
        String DOWNLOAD_TEST4 = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    }
}
