package me.jessyan.mvparms.demo.mvvm.repository;

import com.jess.arms.di.scope.ViewModelScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvvm.BaseRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import me.jessyan.mvparms.demo.mvp.model.api.service.UserService;
import me.jessyan.mvparms.demo.mvp.model.entity.User;


@ViewModelScope
public class MainRepository extends BaseRepository {
    public static final int USERS_PER_PAGE = 10;
    @Inject
    public MainRepository(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    public Observable<List<User>> getUsers(int lastIdQueried) {
        return mRepositoryManager.obtainRetrofitService(UserService.class).getUsers(lastIdQueried, USERS_PER_PAGE);
    }
}
