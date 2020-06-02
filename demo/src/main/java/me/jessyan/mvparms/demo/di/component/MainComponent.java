package me.jessyan.mvparms.demo.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ViewModelScope;

import dagger.Component;
import me.jessyan.mvparms.demo.di.module.MainModule;
import me.jessyan.mvparms.demo.mvvm.viewmodel.MainViewModel;

@ViewModelScope
@Component(modules = MainModule.class, dependencies = AppComponent.class)
public interface MainComponent {
    void inject(MainViewModel viewModel);
}
