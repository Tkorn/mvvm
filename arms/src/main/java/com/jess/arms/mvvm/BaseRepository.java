package com.jess.arms.mvvm;

import com.jess.arms.integration.IRepositoryManager;

public class BaseRepository {
    protected IRepositoryManager mRepositoryManager;//用于管理网络请求层, 以及数据缓存层

    public BaseRepository(IRepositoryManager repositoryManager) {
        this.mRepositoryManager = repositoryManager;
    }

    /**
     * 在框架中 {@link BaseViewModel#onCleared()} 时会默认调用
     */
    public void onCleared() {
        mRepositoryManager = null;
    }
}
