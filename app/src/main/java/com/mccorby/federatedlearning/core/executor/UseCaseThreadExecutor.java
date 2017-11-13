package com.mccorby.federatedlearning.core.executor;

import io.reactivex.Scheduler;

public interface UseCaseThreadExecutor {
    Scheduler getOriginScheduler();
    Scheduler getPostScheduler();
}
