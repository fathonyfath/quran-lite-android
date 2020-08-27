package id.thony.android.quranlite.useCase;

import id.thony.android.quranlite.data.BookmarkRepository;
import id.thony.android.quranlite.models.Bookmark;
import id.thony.android.quranlite.utils.scheduler.Schedulers;

public class PutBookmarkUseCase extends BaseUseCase {

    private final BookmarkRepository bookmarkRepository;

    private UseCaseCallback<Boolean> callback;
    private Bookmark bookmark;

    public PutBookmarkUseCase(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    public void setCallback(UseCaseCallback<Boolean> callback) {
        this.callback = callback;
    }

    public void setArguments(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    protected void task() {
        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                bookmarkRepository.putBookmark(bookmark);
                postResultToMainThread();
            }
        });
    }

    private void postResultToMainThread() {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResult(true);
                }
            }
        });
    }

    public static class Factory implements UseCaseFactory<PutBookmarkUseCase> {

        private final BookmarkRepository bookmarkRepository;

        public Factory(BookmarkRepository bookmarkRepository) {
            this.bookmarkRepository = bookmarkRepository;
        }

        @Override
        public PutBookmarkUseCase create() {
            return new PutBookmarkUseCase(this.bookmarkRepository);
        }
    }
}
