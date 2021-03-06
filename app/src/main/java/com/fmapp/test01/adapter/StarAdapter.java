package com.fmapp.test01.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chaychan.adapter.MultipleItemRvAdapter;
import com.fmapp.test01.adapter.provider.star.FilesItemPrivider;
import com.fmapp.test01.network.model.star.FilesModel;
import com.fmapp.test01.network.model.star.StarModel;

import java.util.List;

public class StarAdapter extends MultipleItemRvAdapter<StarModel, BaseViewHolder> {
    public static final int TYPE_FILES = 0;


    public StarAdapter(List<StarModel> data) {
        super(data);
        finishInitialize();
    }


    @Override
    protected int getViewType(StarModel starModel) {
        //返回对应的viewType
        if (starModel instanceof FilesModel) {
            return TYPE_FILES;
        }
        return 0;
    }

    @Override
    public void registerItemProvider() {
        //注册相关的条目provider
        mProviderDelegate.registerProvider(new FilesItemPrivider());
    }
}
