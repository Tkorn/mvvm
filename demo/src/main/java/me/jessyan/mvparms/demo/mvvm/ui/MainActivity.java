package me.jessyan.mvparms.demo.mvvm.ui;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.jess.arms.base.BaseVMActivity;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.paginate.Paginate;
import butterknife.BindView;
import me.jessyan.mvparms.demo.R;
import me.jessyan.mvparms.demo.mvp.ui.adapter.UserAdapter;
import me.jessyan.mvparms.demo.mvvm.viewmodel.MainViewModel;
import timber.log.Timber;

public class MainActivity extends BaseVMActivity<MainViewModel> implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar_back)
    RelativeLayout toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView.Adapter mAdapter;

    private Paginate mPaginate;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_user;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        mAdapter = new UserAdapter(mViewModel.mUsers.getValue());
        swipeRefreshLayout.setOnRefreshListener(this);
        ArmsUtils.configRecyclerView(recyclerView, new GridLayoutManager(this, 2));
        recyclerView.setAdapter(mAdapter);
        initPaginate();

        mViewModel.mUsers.observe(this, users ->
                mAdapter.notifyDataSetChanged());
    }

    /**
     * 初始化Paginate,用于加载更多
     */
    private void initPaginate() {
        if (mPaginate == null) {
            Paginate.Callbacks callbacks = new Paginate.Callbacks() {
                @Override
                public void onLoadMore() {
//                    加载更多
                    mViewModel.requestFromModel(false);
                }

                @Override
                public boolean isLoading() {
                    return mViewModel.showLoadMore.getValue();
                }

                @Override
                public boolean hasLoadedAllItems() {
                    return false;
                }
            };

            mPaginate = Paginate.with(recyclerView, callbacks)
                    .setLoadingTriggerThreshold(0)
                    .build();
            mPaginate.setHasMoreDataToLoad(false);
        }
    }

    @Override
    public void showLoading() {
        Timber.tag(TAG).w("showLoading");
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        Timber.tag(TAG).w("hideLoading");
        swipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public void onRefresh() {
        mViewModel.requestFromModel(true);
    }

    @Override
    protected void onDestroy() {
        DefaultAdapter.releaseAllHolder(recyclerView);//super.onDestroy()之后会unbind,所有view被置为null,所以必须在之前调用
        super.onDestroy();
        this.mPaginate = null;
    }
}
