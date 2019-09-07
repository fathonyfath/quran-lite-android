package id.fathonyfath.quranlite.useCase;

public interface UseCaseFactory<T extends BaseUseCase> {

    T create();
}
