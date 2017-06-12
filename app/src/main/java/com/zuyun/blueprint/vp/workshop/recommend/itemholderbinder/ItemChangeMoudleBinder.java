package com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueprint.adapter.RecyclerHolder;
import com.jakewharton.rxbinding2.view.RxView;
import com.zuyun.blueprint.R;
import com.zuyun.blueprint.vp.workshop.recommend.RecomPresenter;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.bean.ItemChangeMoudle;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author 江祖赟.
 * @date 2017/6/8
 * @des [一句话描述]
 */
public class ItemChangeMoudleBinder extends ItemViewBinder<ItemChangeMoudle,RecyclerHolder> {

    private RecomPresenter mRecomPresenter;

    public ItemChangeMoudleBinder(RecomPresenter recomPresenter){

        mRecomPresenter = recomPresenter;
    }

    @NonNull
    @Override
    protected RecyclerHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent){
        return new RecyclerHolder(inflater.inflate(R.layout.recv_item_recom_changed, null));
    }

    @Override
    protected void onBindViewHolder(@NonNull final RecyclerHolder holder, @NonNull ItemChangeMoudle item){
        fillItemView(holder, holder.getView(R.id.lay_recv_first), item);
        fillItemView(holder, holder.getView(R.id.lay_recv_sec), item);
        RxView.clicks(holder.getView(R.id.btn_recv_recom_change_next)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Object,ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@io.reactivex.annotations.NonNull Object o) throws Exception{
                        System.out.println(o.toString());
                        ViewGroup parent = (ViewGroup)holder.getView(R.id.btn_recv_recom_change_next).getParent()
                                .getParent();
                        parent.removeView(holder.getView(R.id.lay_recv_first));
                        return Observable.just(o).delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(AndroidSchedulers.mainThread());
                    }
                }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception{
                System.out.println(o.toString());
                ViewGroup parent = (ViewGroup)holder.getView(R.id.btn_recv_recom_change_next).getParent().getParent();
                //                parent.removeView(holder.getView(R.id.lay_recv_first));
                parent.addView(holder.getView(R.id.lay_recv_first));
            }
        });
    }

    private void fillItemView(@NonNull RecyclerHolder holder, View firstItem, ItemChangeMoudle item){
        ImageView imageIcon = holder.getView(firstItem, R.id.im_recv_common_icon);
        Button down_start = holder.getView(firstItem, R.id.btn_recv_common_right);
        final TextView title = holder.getView(firstItem, R.id.tv_recv_common_1);
        final TextView anthor = holder.getView(firstItem, R.id.tv_recv_common_2);
        TextView downloadnum = holder.getView(firstItem, R.id.tv_recv_common_3);

        title.setText(item.getTitle());

        //        Disposable subscribe = RxBus.getInstance().register(String.class).subscribe(new Consumer<String>() {
        //            @Override
        //            public void accept(@io.reactivex.annotations.NonNull String s) throws Exception{
        //                anthor.setText(s);
        //
        //            }
        //        });
        //        Disposable subscribe2 = RxBus.getInstance().register(Integer.class).subscribe(new Consumer<Integer>() {
        //            @Override
        //            public void accept(@io.reactivex.annotations.NonNull Integer s) throws Exception{
        //                title.setText(s+"");
        //            }
        //        });
        //        RxBus.getInstance().putDisposable(0, subscribe);
        //        RxBus.getInstance().putDisposable(0, subscribe2);
    }

}
