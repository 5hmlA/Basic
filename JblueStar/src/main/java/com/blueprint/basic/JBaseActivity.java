package com.blueprint.basic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.blueprint.LibApp;
import com.blueprint.R;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public class JBaseActivity extends AppCompatActivity {

    public Toast mDoubleFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDoubleFinish = Toast.makeText(this, LibApp.findString(R.string.jdouble_exit), Toast.LENGTH_SHORT);
    }

    public void doubleExit(){
        if(mDoubleFinish.getView() != null && mDoubleFinish.getView().isShown()) {
            finish();
        }else {
            mDoubleFinish.show();
        }
    }

}
