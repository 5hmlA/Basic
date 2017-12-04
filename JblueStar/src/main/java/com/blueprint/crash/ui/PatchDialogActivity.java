package com.blueprint.crash.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.blueprint.R;
import com.blueprint.crash.CrashWrapper;


public class PatchDialogActivity extends AppCompatActivity {

    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_ULTIMATE_MESSAGE = "extra_ultimate_message";
    private static final String EXTRA_URL = "extra_url";

    private String title, ultimateMessage, url;
    private static final String KEY_REBOUND = "key_rebound";


    public static Intent newIntent(Context context, String title, String ultimateMessage, String url){

        Intent intent = new Intent();
        intent.setClass(context, PatchDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_ULTIMATE_MESSAGE, ultimateMessage);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }


    private void parseIntent(Intent intent){
        title = intent.getStringExtra(EXTRA_TITLE);
        ultimateMessage = intent.getStringExtra(EXTRA_ULTIMATE_MESSAGE);
        url = intent.getStringExtra(EXTRA_URL);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        parseIntent(getIntent());
        if(ultimateMessage == null) {
            ultimateMessage = getString(R.string.cw_error_message);
        }
        if(title == null) {
            title = getString(R.string.cw_error_title);
        }
        ultimateSolution();
    }


    private void ultimateSolution(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(title).setMessage(ultimateMessage)
                .setCancelable(true).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog){
                        finish();
                    }
                }).setPositiveButton(R.string.cw_action_restart, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        CrashWrapper.restartApp();
                    }
                });
        if(!TextUtils.isEmpty(url)) {
            builder.setNegativeButton(R.string.cw_action_download, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    dialog.dismiss();
                    finish();
                }
            });
        }
        builder.show();
    }

}
