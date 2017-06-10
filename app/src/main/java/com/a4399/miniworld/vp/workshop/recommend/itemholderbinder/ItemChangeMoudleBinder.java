package com.a4399.miniworld.vp.workshop.recommend.itemholderbinder;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baselib.rx.RxBus;
import com.a4399.miniworld.vp.workshop.recommend.RecomPresenter;
import com.a4399.miniworld.vp.workshop.recommend.itemholderbinder.bean.ItemChangeMoudle;
import com.baselib.adapter.RecyclerHolder;
import com.first.a4399.miniworld.R;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    protected void onBindViewHolder(@NonNull RecyclerHolder holder, @NonNull ItemChangeMoudle item){
        fillItemView(holder, holder.getView(R.id.lay_recv_first),item);
        fillItemView(holder, holder.getView(R.id.lay_recv_sec),item);
    }

    private void fillItemView(@NonNull RecyclerHolder holder, View firstItem,ItemChangeMoudle item){
        ImageView imageIcon = holder.getView(firstItem, R.id.im_recv_common_icon);
        Button down_start = holder.getView(firstItem, R.id.btn_recv_common_right);
        final TextView title = holder.getView(firstItem, R.id.tv_recv_common_1);
        final TextView anthor = holder.getView(firstItem, R.id.tv_recv_common_2);
        TextView downloadnum = holder.getView(firstItem, R.id.tv_recv_common_3);

        title.setText(item.getTitle());
        Disposable subscribe = RxBus.getInstance().register(String.class).subscribe(new Consumer<String>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull String s) throws Exception{
                anthor.setText(s);

            }
        });
        Disposable subscribe2 = RxBus.getInstance().register(Integer.class).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer s) throws Exception{
                title.setText(s+"");
            }
        });
        RxBus.getInstance().putDisposable(0, subscribe);
        RxBus.getInstance().putDisposable(0, subscribe2);
    }
}
