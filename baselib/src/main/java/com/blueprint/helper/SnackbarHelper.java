package com.blueprint.helper;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SnackbarHelper {
    private Snackbar mSnackbar;
    private final Snackbar.SnackbarLayout mSnackbarLayout;
    private final TextView mMessageView;
    private final Button mActionView;

    private SnackbarHelper(Snackbar snackbar) {
        mSnackbar = snackbar;
        mSnackbarLayout = (Snackbar.SnackbarLayout) mSnackbar.getView();
        mMessageView = (TextView) mSnackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        mActionView = (Button) mSnackbarLayout.findViewById(android.support.design.R.id.snackbar_action);
    }

    public static SnackbarHelper create(Snackbar snackbar) {
        return new SnackbarHelper(snackbar);
    }

    public SnackbarHelper setBackground(int bgColor) {
        mSnackbarLayout.setBackgroundColor(bgColor);
        return this;
    }

    public SnackbarHelper setMessageViewColor(int bgColor) {
        mMessageView.setTextColor(bgColor);
        return this;
    }
    public SnackbarHelper setActionViewColor(int bgColor) {
        mActionView.setTextColor(bgColor);
        return this;
    }
    public SnackbarHelper addCustomView(View view, int position) {
        mSnackbarLayout.addView(view,position);
        return this;
    }

    public TextView getMessageView() {
        return mMessageView;
    }

    public Button getActionView() {
        return mActionView;
    }
}
