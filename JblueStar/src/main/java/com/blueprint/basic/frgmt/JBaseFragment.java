package com.blueprint.basic.frgmt;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blueprint.R;
import com.blueprint.basic.LazyFragment;
import com.blueprint.helper.LogHelper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class JBaseFragment extends LazyFragment {
    public Context mAttachActivity;
    protected Bundle mArguments;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected void collectDisposables(Disposable disposable){
        mCompositeDisposable.add(disposable);
    }

    protected void clearDisposables(){
        LogHelper.Log_d("before-clearDisposables()-: "+mCompositeDisposable.size());
        mCompositeDisposable.clear();
        LogHelper.Log_d("after-clearDisposables()-: "+mCompositeDisposable.size());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //onCreateView 之后 执行一次
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View inflate = inflater.inflate(R.layout.jmain_fm_content, null);
        TextView mTempTv = (TextView)inflate.findViewById(R.id.temp_tv);
        mTempTv.setText("JBaseFragment");
        return inflate;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mAttachActivity = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        if(( mArguments = getArguments() ) != null) {
            parseArguments(mArguments);
        }
        super.onCreate(savedInstanceState);
    }

    protected void parseArguments(Bundle arguments){

    }

    @Override
    public void firstUserVisibile(){
        LogHelper.Log_d("firstUserVisibile---");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        clearDisposables();
    }
}
