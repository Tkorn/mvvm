package me.jessyan.mvparms.demo.mvvm.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.mvvm.BaseViewModel;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.mvparms.demo.di.component.DaggerMainComponent;
import me.jessyan.mvparms.demo.mvp.model.entity.User;
import me.jessyan.mvparms.demo.mvvm.repository.MainRepository;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

public class MainViewModel extends BaseViewModel<MainRepository> {

    @Inject
    public RxErrorHandler mErrorHandler;
    @Inject
    public MutableLiveData<List<User>> mUsers;

    public MutableLiveData<Boolean> showLoadMore;


    private int lastUserId = 1;
    private boolean isFirst = true;
    private int preEndIndex;


    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    protected void initData() {
        if (showLoadMore == null){
            showLoadMore = new MutableLiveData<>();
        }
        showLoadMore.setValue(false);
    }


    public void requestFromModel(boolean pullToRefresh) {
        if (pullToRefresh) {
            lastUserId = 1;//下拉刷新默认只请求第一页
        }

        if (pullToRefresh && isFirst) {//默认在第一次下拉刷新时使用缓存
            isFirst = false;
        }
        mRepository.getUsers(lastUserId)
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    if (pullToRefresh) {
                        showLoading.postValue(true);//显示下拉刷新的进度条
                    } else {
                        showLoadMore.postValue(true);//显示上拉加载更多的进度条
                    }
                })
                .doFinally(() -> {
                    if (pullToRefresh) {
                        showLoading.postValue(false);//隐藏下拉刷新的进度条
                    } else {
                        showLoadMore.postValue(false);//隐藏上拉加载更多的进度条
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new ErrorHandleSubscriber<List<User>>(mErrorHandler) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        addDispose(d);
                    }

                    @Override
                    public void onNext(List<User> users) {
                        lastUserId = users.get(users.size() - 1).getId();//记录最后一个id,用于下一次请求
                        if (pullToRefresh) {
                            mUsers.getValue().clear();
                        }
                        mUsers.getValue().addAll(users);
                        mUsers.postValue(mUsers.getValue());
                        preEndIndex = mUsers.getValue().size();//更新之前列表总长度,用于确定加载更多的起始位置
                    }
                });
    }

}
