package com.blueprint.rx;

import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blueprint.R;
import com.blueprint.helper.CheckHelper;
import com.blueprint.helper.LogHelper;
import com.blueprint.helper.RegexHelper;
import com.blueprint.helper.SpanHelper;
import com.blueprint.helper.spannable.OnSpanClickListener;
import com.blueprint.helper.spannable.SpannableClickable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.blueprint.helper.SpanHelper.TAG_CLICK_SPAN;
import static com.blueprint.helper.StrHelper.buildStrArrays2Str;

/**
 * @another 江祖赟
 * @date 2017/10/20 0020.
 * Pair 为span点击
 * view object 为click点击
 */
public class SpanClickObservable extends Observable<Object> {
    private CharSequence mOrign;
    private final TextView textView;
    private String[] mClickText;
    private int mClickTextColor;
    private int mClickableSpanBgClor;
    private int mTextViewBgColor;

    //    public SpanClickObservable(String orign, @NonNull final TextView textView, @NonNull final String... clickText){
    //        this(orign, textView, ContextCompat.getColor(textView.getContext(), R.color.colorAccent), clickText);
    //    }
    //
    //    public SpanClickObservable(String orign,
    //                               @NonNull final TextView textView,
    //                               @ColorInt int clickTextColor, @NonNull final String... clickText){
    //        this(orign, textView, clickTextColor, ContextCompat.getColor(textView.getContext(), R.color.j_black_a85),
    //                clickText);
    //    }
    //
    //    public SpanClickObservable(String orign,
    //                               @NonNull final TextView textView,
    //                               @ColorInt int clickTextColor,
    //                               @ColorInt int clickableSpanBgClor, @NonNull final String... clickText){
    //        this(orign, textView, clickTextColor, clickableSpanBgClor, clickableSpanBgClor, clickText);
    //    }

    SpanClickObservable(CharSequence orign,
                        @NonNull final TextView textView,
                        @ColorInt int clickTextColor,
                        @ColorInt int clickableSpanBgClor,
                        @ColorInt int textViewBgColor, @NonNull final String... clickText){
        mOrign = orign;
        this.textView = textView;
        mClickText = clickText;
        mClickTextColor = clickTextColor;
        mClickableSpanBgClor = clickableSpanBgClor;
        mTextViewBgColor = textViewBgColor;
        this.textView.setTag(TAG_CLICK_SPAN, false);//可点击
    }

    @Override
    protected void subscribeActual(Observer<? super Object> observer){
        if(!checkMainThread(observer)) {
            return;
        }
        SpanClickListener spanClickListener = new SpanClickListener(textView, observer);
        observer.onSubscribe(spanClickListener);
        textView.setOnClickListener(spanClickListener);
        Spannable spannable = buildClickSpanString(getOrign(), getClickTextColor(), spanClickListener, getClickText());
        SpanHelper.wrapperClickMovement(textView, mClickableSpanBgClor, mTextViewBgColor).setText(spannable);
    }

    private Spannable buildClickSpanString(CharSequence orign, @ColorInt
            int spanTextColor, OnSpanClickListener onSpanClickListener, String... spanTexts){
        //        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString orignSpanString = new SpannableString(orign);
        for(String spanText : spanTexts) {
            clickSpanTextStrParser(orignSpanString, spanText, spanTextColor, onSpanClickListener);
        }
        return orignSpanString;
    }

    private SpannableString clickSpanTextStrParser(
            @NonNull SpannableString orignSpan, final String key,
            @ColorInt int spanTextColor, final OnSpanClickListener onSpanClickListener){
        if(CheckHelper.checkStrings(orignSpan, key)) {
            Pattern p = Pattern.compile(RegexHelper.safeRegex(key));//不支持拆分
            Matcher m = p.matcher(orignSpan);
            while(m.find()) {
                int start = m.start();
                int end = m.end();
                final String matchKey = m.group();
                if(!TextUtils.isEmpty(matchKey)) {
                    orignSpan.setSpan(new SpannableClickable(spanTextColor) {
                        @Override
                        public void onClick(View widget){
                            //textview的clicklistener出需要把TAG_CLICK_SPAN设置为false
                            textView.setTag(TAG_CLICK_SPAN, true);
                            onSpanClickListener.onSpanClick(matchKey);
                        }
                    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return orignSpan;
    }

    public static boolean checkMainThread(Observer<?> observer){
        if(Looper.myLooper() != Looper.getMainLooper()) {
            LogHelper.Log_e("Expected to be called on the main thread but was "+Thread.currentThread().getName());
            observer.onComplete();
            return false;
        }
        return true;
    }

    public CharSequence getOrign(){
        return mOrign;
    }

    public void setOrign(CharSequence orign){
        mOrign = orign;
    }

    public String[] getClickText(){
        return mClickText;
    }

    public void setClickText(String[] clickText){
        mClickText = clickText;
    }

    public int getClickTextColor(){
        return mClickTextColor;
    }

    public void setClickTextColor(int clickTextColor){
        mClickTextColor = clickTextColor;
    }

    public int getClickableSpanBgClor(){
        return mClickableSpanBgClor;
    }

    public void setClickableSpanBgClor(int clickableSpanBgClor){
        mClickableSpanBgClor = clickableSpanBgClor;
    }

    public int getTextViewBgColor(){
        return mTextViewBgColor;
    }

    public void setTextViewBgColor(int textViewBgColor){
        mTextViewBgColor = textViewBgColor;
    }

    /**
     * 点击事件传递数据 View<p>
     * span点击事件传递 Pair<View,String>
     */
    static final class SpanClickListener extends MainThreadDisposable implements View.OnClickListener, OnSpanClickListener {
        private final TextView textView;
        private final Observer<? super Object> observer;

        SpanClickListener(TextView textView, Observer<? super Object> observer){
            this.textView = textView;
            this.observer = observer;
        }

        @Override
        protected void onDispose(){
            textView.setOnClickListener(null);
        }

        @Override
        public void onClick(View v){
            //spanclick响应玩之后 一定会触发onClick
            if(!isDisposed() && !( (boolean)textView.getTag(TAG_CLICK_SPAN) )) {
                //没有触发spanClick就走onClick
                observer.onNext(textView);
            }
            textView.setTag(TAG_CLICK_SPAN, false);
        }

        @Override
        public void onSpanClick(CharSequence clickSpanStr){
            observer.onNext(Pair.create(textView, clickSpanStr));
        }
    }

    public static final class Builder {
        private CharSequence mOrign;
        private TextView mTextView;
        private String[] mClickText;
        private int mClickTextColor;
        private int mClickableSpanBgClor;
        private int mTextViewBgColor;

        public Builder(TextView textView){
            mTextView = textView;
            mClickTextColor = ContextCompat.getColor(textView.getContext(), R.color.colorAccent);
            mTextViewBgColor = mClickableSpanBgClor = ContextCompat
                    .getColor(textView.getContext(), R.color.j_black_a85);
        }

        public Builder setOrign(CharSequence orign){
            mOrign = orign;
            return this;
        }

        public Builder setClickText(String... clickText){
            mClickText = clickText;
            return this;
        }

        public Builder setClickTextColor(int clickTextColor){
            mClickTextColor = clickTextColor;
            return this;
        }

        public Builder setClickableSpanBgClor(int clickableSpanBgClor){
            mClickableSpanBgClor = clickableSpanBgClor;
            return this;
        }

        public Builder setTextViewBgColor(int textViewBgColor){
            mTextViewBgColor = textViewBgColor;
            return this;
        }

        public SpanClickObservable build(){
            try2MakeOrignSafe();
            return new SpanClickObservable(mOrign, mTextView, mClickTextColor, mClickableSpanBgClor, mTextViewBgColor,
                    mClickText);
        }

        private void try2MakeOrignSafe(){
            if(TextUtils.isEmpty(mOrign) && TextUtils.isEmpty(mOrign = mTextView.getText())) {
                mOrign = buildStrArrays2Str(mClickText);
            }
        }
    }

    public static boolean isSpanClick(Object o){
        return o instanceof Pair;
    }
}
