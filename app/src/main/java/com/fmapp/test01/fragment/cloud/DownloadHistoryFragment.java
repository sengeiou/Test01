package com.fmapp.test01.fragment.cloud;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmapp.test01.R;
import com.fmapp.test01.adapter.HistoryAdapter;
import com.fmapp.test01.base.BaseFragment;
import com.fmapp.test01.network.model.BaseResponse;
import com.fmapp.test01.network.model.history.FilesModel;
import com.fmapp.test01.network.model.history.HistoryListModel;
import com.fmapp.test01.network.model.history.HistoryModel;
import com.fmapp.test01.network.util.DataResultException;
import com.fmapp.test01.network.util.RetrofitUtil;
import com.mylhyl.crlayout.SwipeRefreshAdapterView;
import com.mylhyl.crlayout.SwipeRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadHistoryFragment extends BaseFragment implements SwipeRefreshAdapterView.OnListLoadListener, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshRecyclerView mRecycleView;
    private boolean isGetData = false;
    private List<HistoryModel> mHisData = new ArrayList<>();
    private HistoryAdapter mHistoryAdapter;
    private int pg = 1;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter && !isGetData) {
            isGetData = true;
            //   这里可以做网络请求或者需要的数据刷新操作
            if (mHisData.size() > 0) {
                mHisData.clear();
                mHistoryAdapter.notifyDataSetChanged();
            }
            GetData();
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    /**
     * 获取下载历史列表数据
     */
    private void GetData() {
        LoaddingShow();
        RetrofitUtil.getInstance().gethisfiles(token, pg, new Subscriber<BaseResponse<HistoryListModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LoaddingDismiss();
                if (e instanceof DataResultException) {
                    DataResultException resultException = (DataResultException) e;
                    showToast(resultException.getMsg());
                }
            }

            @Override
            public void onNext(BaseResponse<HistoryListModel> baseResponse) {
                LoaddingDismiss();
                if (baseResponse.getStatus() == 1) {
                    HistoryListModel historyListModel = baseResponse.getData();
                    if (historyListModel.getFiles().size() > 0) {
                        List<HistoryListModel.FilesBean> filesBeans = historyListModel.getFiles();
                        for (HistoryListModel.FilesBean bean : filesBeans) {
                            FilesModel filesModel = new FilesModel();
                            filesModel.setExt(bean.getExt());
                            filesModel.setFid(bean.getFid());
                            filesModel.setId(bean.getId());
                            filesModel.setIntime(bean.getIntime());
                            filesModel.setName(bean.getName());
                            filesModel.setSize(bean.getSize());
                            mHisData.add(filesModel);
                        }
                        mHistoryAdapter.setNewData(mHisData);
                        mHistoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    showToast(baseResponse.getMsg());
                }
            }
        });
    }

    public DownloadHistoryFragment() {
        // Required empty public constructor
    }

    public static DownloadHistoryFragment newInstance() {
        return new DownloadHistoryFragment();
    }

    @Override
    protected void initView(View view) {
        mRecycleView = findView(R.id.mRecycleView);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        View EmptyView = LayoutInflater.from(getActivity()).inflate(R.layout.view_downhis_recycler_empty, null, false);
        mRecycleView.setEmptyView(EmptyView);
        mRecycleView.setOnListLoadListener(this);
        mRecycleView.setOnRefreshListener(this);
        mHistoryAdapter = new HistoryAdapter(mHisData);
        mRecycleView.setAdapter(mHistoryAdapter);
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListLoad() {
        ++pg;
        mRecycleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHistoryAdapter.notifyDataSetChanged();
                mRecycleView.setLoading(false);
                if (pg == 1) {
                    mRecycleView.setLoadCompleted(true);
                } else GetData();
                mRecycleView.setLoadCompleted(true);
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        pg = 1;
        mRecycleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mHisData.size() > 0) {
                    mHisData.clear();
                    mHistoryAdapter.notifyDataSetChanged();
                }
                GetData();
                mHistoryAdapter.notifyDataSetChanged();
                mRecycleView.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        isGetData = false;
    }


    @Override
    protected int initLayout() {
        return R.layout.fragment_download_history;
    }


    @Override
    protected void initData(Context mContext) {

    }
}
