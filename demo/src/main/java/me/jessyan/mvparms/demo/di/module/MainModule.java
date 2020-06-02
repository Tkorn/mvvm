package me.jessyan.mvparms.demo.di.module;

import androidx.lifecycle.MutableLiveData;

import com.jess.arms.di.scope.ViewModelScope;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import me.jessyan.mvparms.demo.mvp.model.entity.User;

@Module
public class MainModule {

    @ViewModelScope
    @Provides
    static MutableLiveData<List<User>> provideUserList(){
        MutableLiveData<List<User>> users = new MutableLiveData<>();
        users.setValue(new ArrayList<>());
        return users;
    }

}
