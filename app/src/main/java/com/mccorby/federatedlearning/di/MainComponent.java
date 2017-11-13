package com.mccorby.federatedlearning.di;

import com.mccorby.federatedlearning.app.MainActivity;

import dagger.Component;

@Component(modules = {MainModule.class})
public interface MainComponent {

    void inject(MainActivity activity);
}
