package com.blueprint.helper.spannable;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * @author yiw
 * @Description:
 * @date 16/1/2 16:32
 */
public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {

    private int DEFAULT_COLOR_ID = Color.GRAY;
    /**
     * text颜色
     */
    public int textColor;
    public boolean underLineText ;

    public SpannableClickable(){
        this.textColor = DEFAULT_COLOR_ID;
    }

    public SpannableClickable(int textColor){
        this.textColor = textColor;
    }

    public SpannableClickable(int textColor,boolean underLineText){
        this.textColor = textColor;
        this.underLineText = underLineText;
    }

    @Override
    public void updateDrawState(TextPaint ds){
        super.updateDrawState(ds);
        ds.setColor(textColor);
        ds.setUnderlineText(underLineText);
        ds.clearShadowLayer();
    }
}
