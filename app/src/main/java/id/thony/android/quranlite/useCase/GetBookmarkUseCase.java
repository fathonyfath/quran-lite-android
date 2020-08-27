package id.thony.android.quranlite.useCase;

import id.thony.android.quranlite.data.BookmarkRepository;
import id.thony.android.quranlite.models.Bookmark;
import id.thony.android.quranlite.utils.scheduler.Schedulers;

public class GetBookmarkUseCase extends BaseUseCase {

    private final BookmarkRepository bookmarkRepository;

    private UseCaseCallback<Bookmark> callback;

    public GetBookmarkUseCase(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    public void setCallback(UseCaseCallback<Bookmark> callback) {
        this.callback = callback;
    }

    @Override
    protected void task() {
        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                final Bookmark bookmark = bookmarkRepository.getBookmark();
                postResultToMainThread(bookmark);
            }
        });
    }

    private void postResultToMainThread(final Bookmark bookmark) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResult(bookmark);
                }
            }
        });
    }

    public static class Factory implements UseCaseFactory<GetBookmarkUseCase> {

        private final BookmarkRepository bookmarkRepository;

        public Factory(BookmarkRepository bookmarkRepository) {
            this.bookmarkRepository = bookmarkRepository;
        }

        @Override
        public GetBookmarkUseCase create() {
            return new GetBookmarkUseCase(this.bookmarkRepository);
        }
    }
}
