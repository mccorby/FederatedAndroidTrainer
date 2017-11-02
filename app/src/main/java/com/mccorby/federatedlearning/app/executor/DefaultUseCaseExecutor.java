package com.mccorby.federatedlearning.app.executor;

import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;

import java.util.concurrent.Executor;

public class DefaultUseCaseExecutor implements UseCaseExecutor {

    private Executor executor;

    public DefaultUseCaseExecutor(Executor executor) {

        this.executor = executor;
    }

    @Override
    public void execute(final UseCase useCase) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                useCase.execute();
            }
        });
    }
}
