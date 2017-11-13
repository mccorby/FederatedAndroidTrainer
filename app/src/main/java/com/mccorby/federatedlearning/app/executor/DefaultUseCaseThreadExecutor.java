package com.mccorby.federatedlearning.app.executor;

import com.mccorby.federatedlearning.core.executor.UseCaseThreadExecutor;

import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class DefaultUseCaseThreadExecutor implements UseCaseThreadExecutor {

    @Override
    public Scheduler getOriginScheduler() {
        return Schedulers.from(Executors.newSingleThreadExecutor());
    }

    @Override
    public Scheduler getPostScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
