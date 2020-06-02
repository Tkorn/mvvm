package com.jess.arms.mvvm;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseViewModel<R extends BaseRepository> extends AndroidViewModel{

    protected CompositeDisposable mCompositeDisposable;
    @Inject
    protected R mRepository;

    public MutableLiveData<Boolean> showLoading;
    public MutableLiveData<String> showToastMsg = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
        setupActivityComponent(ArmsUtils.obtainAppComponentFromContext(application));
        initData();
        if (showLoading == null){
             showLoading = new MutableLiveData<>();
        }
        showLoading.setValue(false);
    }

    /**
     * 将 {@link Disposable} 添加到 {@link CompositeDisposable} 中统一管理
     * 在使用 {@link #onCleared()} 停止正在执行的 RxJava 任务,避免内存泄漏
     * 因为不建议在ViewModel中持有Activity的生命周期，所以没有是用RxLifecycle
     * @param disposable
     */
    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有 Disposable 放入容器集中处理
    }

    /**
     * 提供 AppComponent (提供所有的单例对象) 给实现类, 进行 Component 依赖
     *
     * @param appComponent
     */
    protected abstract void setupActivityComponent(@NonNull AppComponent appComponent);
    protected abstract void initData();


    @Override
    protected void onCleared() {
        if (mRepository != null) {
            mRepository.onCleared();
            mRepository = null;
        }
        if (mCompositeDisposable != null) {
            //ViewModel销毁时会执行，同时取消所有异步任务
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
        super.onCleared();
    }

}
