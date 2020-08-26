package id.fathonyfath.quran.lite.views.searchSurah;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;

import id.fathonyfath.quran.lite.Res;
import id.fathonyfath.quran.lite.models.Surah;
import id.fathonyfath.quran.lite.useCase.DoSearchUseCase;
import id.fathonyfath.quran.lite.useCase.UseCaseCallback;
import id.fathonyfath.quran.lite.useCase.UseCaseProvider;
import id.fathonyfath.quran.lite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quran.lite.views.common.CloseView;
import id.fathonyfath.quran.lite.views.common.ToolbarView;
import id.fathonyfath.quran.lite.views.common.WrapperView;

public class SearchSurahView extends WrapperView implements ViewCallback {

    private final ToolbarView.OnSearchListener searchListener = new ToolbarView.OnSearchListener() {
        @Override
        public void onSearch(String query) {
            if (getSearchInput() != null) {
                getSearchInput().clearFocus();
                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getSearchInput().getWindowToken(), 0);
            }

            doSearchProcess(query);
        }
    };

    private final UseCaseCallback<List<Surah>> doSearchUseCaseCallback = new UseCaseCallback<List<Surah>>() {
        @Override
        public void onProgress(float progress) {

        }

        @Override
        public void onResult(List<Surah> data) {
            Toast.makeText(getContext(), "Size is " + data.size(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable throwable) {

        }
    };

    private OnViewEventListener onViewEventListener;

    public SearchSurahView(Context context) {
        super(context);

        setId(Res.Id.searchSurahView);

        CloseView closeView = new CloseView(context);
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewEventListener != null) {
                    onViewEventListener.onCloseClicked();
                }
            }
        });

        this.setToolbarLeftView(closeView);

        this.setElevationAlpha(0.1f);
        this.setToolbarIsSearchMode(true);
    }

    public void setOnViewEventListener(OnViewEventListener onViewEventListener) {
        this.onViewEventListener = onViewEventListener;
    }

    @Override
    public void onStart() {
        if (getSearchInput() != null) {
            getSearchInput().requestFocus();
            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(getSearchInput(), InputMethodManager.SHOW_IMPLICIT);
        }

        this.setOnSearchListener(searchListener);
    }

    @Override
    public void onResume() {
        tryToRestoreUseCase();
    }

    @Override
    public void onPause() {
        unregisterUseCase();
    }

    @Override
    public void onStop() {
        if (getSearchInput() != null) {
            getSearchInput().clearFocus();
            getSearchInput().setText("");
            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getSearchInput().getWindowToken(), 0);
        }

        this.setOnSearchListener(null);
    }

    private void doSearchProcess(String query) {
        unregisterAndClearUseCase();
        createAndRunUseCase(query);
    }

    private void unregisterUseCase() {
        DoSearchUseCase useCase = UseCaseProvider.getUseCase(DoSearchUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }
    }

    private void unregisterAndClearUseCase() {
        DoSearchUseCase useCase = UseCaseProvider.getUseCase(DoSearchUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }

        UseCaseProvider.clearUseCase(DoSearchUseCase.class);
    }

    private void createAndRunUseCase(String query) {
        DoSearchUseCase useCase = UseCaseProvider.createUseCase(DoSearchUseCase.class);
        useCase.setCallback(this.doSearchUseCaseCallback);
        useCase.setArguments(query);

        useCase.run();
    }

    private boolean tryToRestoreUseCase() {
        DoSearchUseCase useCase = UseCaseProvider.getUseCase(DoSearchUseCase.class);
        if (useCase != null) {
            useCase.setCallback(this.doSearchUseCaseCallback);
            return true;
        }
        return false;
    }

    public interface OnViewEventListener {
        void onCloseClicked();
    }
}
