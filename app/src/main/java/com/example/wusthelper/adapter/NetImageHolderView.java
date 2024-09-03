package com.example.wusthelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.example.wusthelper.R;
import com.example.wusthelper.bean.javabean.CycleImageBean;

/**
 * 首页轮播图
 * //A、网络图片
 * */
public class NetImageHolderView extends Holder<CycleImageBean> {
    private final String TAG ="NetImageHolderView";

    private ImageView mImageView;

    private Context mContext;

    public NetImageHolderView(View itemView, Context context) {
        super(itemView);
        mContext = context;
    }

    @Override
    protected void initView(View itemView) {
        mImageView = itemView.findViewById(R.id.iv_card_app);
    }

    @SuppressLint("CheckResult")
    @Override
    public void updateUI(CycleImageBean data) {

        if(data!=null&&data.getImgUrl()!=null&&!data.getImgUrl().equals("")){
            Glide.with(mContext).load(data.getImgUrl()).into(mImageView);
        }
        else{
            Glide.with(mContext).load(R.mipmap.default_bg);
        }
    }
}