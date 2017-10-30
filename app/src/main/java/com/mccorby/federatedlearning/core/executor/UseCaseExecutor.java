package com.mccorby.federatedlearning.core.executor;

import com.mccorby.federatedlearning.core.domain.usecase.UseCase;

public interface UseCaseExecutor {

    void execute(UseCase useCase);
}
