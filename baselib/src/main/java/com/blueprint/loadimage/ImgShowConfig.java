package com.blueprint.loadimage;

import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.widget.ImageView;


/**
 * 江祖赟。
 * <br />
 * the config of image
 */
public class ImgShowConfig {

    private String url;
    private int placeHolder;
    private int error;
    private ImageView imageView;

    public ImgShowConfig(Builder builder) {
        this.url = builder.url;
        this.placeHolder = builder.placeHolder;
        this.error = builder.error;
        this.imageView = builder.imageView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(int placeHolder) {
        this.placeHolder = placeHolder;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public static class Builder {
        private String url;
        private int placeHolder;
        private int error;
        private ImageView imageView;

        public Builder() {
            this.url = "";
            this.placeHolder = com.blueprint.R.mipmap.ic_launcher;
            this.imageView = null;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder placeHolder(@IdRes int placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

        public Builder error(@IdRes int error) {
            this.error = error;
            return this;
        }

        public Builder imgView(ImageView imgView) {
            this.imageView = imgView;
            return this;
        }

        public ImgShowConfig build() {
            if (imageView == null) throw new IllegalArgumentException("imageView required");
            if (TextUtils.isEmpty(url)) throw new IllegalArgumentException("the url cannot be empty");
            return new ImgShowConfig(this);
        }
    }


}
