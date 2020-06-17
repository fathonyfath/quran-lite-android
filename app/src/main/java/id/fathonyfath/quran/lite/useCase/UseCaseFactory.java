package id.fathonyfath.quran.lite.useCase;

public interface UseCaseFactory<T extends BaseUseCase> {

    T create();
}
