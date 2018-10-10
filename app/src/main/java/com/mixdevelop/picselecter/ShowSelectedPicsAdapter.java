package com.mixdevelop.picselecter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import jp.wasabeef.glide.transformations.CropSquareTransformation;


/**
 * Author:wang_sir
 * Time:2018/7/19 10:52
 * Description:This is MySuggestionAdapter
 */
public class ShowSelectedPicsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ShowSelectedPicsAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if ("-1".equals(item)) {
            Glide.with(mContext).load(R.mipmap.mine_suggest_add)
                    .skipMemoryCache(false)
                    .bitmapTransform(new CropSquareTransformation(mContext))
                    .into((ImageView) helper.getView(R.id.mine_sugguest_icon_iv));

        }else{
            Glide.with(mContext).load(item)
                    .skipMemoryCache(false)
                    .bitmapTransform(new CropSquareTransformation(mContext))
                    .into((ImageView) helper.getView(R.id.mine_sugguest_icon_iv));
        }
        helper.addOnClickListener(R.id.mine_sugguest_icon_iv);
        helper.addOnClickListener(R.id.mine_sugguest_delete_iv);
    }
}