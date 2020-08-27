package id.thony.android.quranlite.useCase;

public interface UseCaseFactory<T extends BaseUseCase> {

    T create();
}
