package id.fathonyfath.quran.lite.views.searchSurah;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.fathonyfath.quran.lite.Res;
import id.fathonyfath.quran.lite.models.Surah;
import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.useCase.DoSearchUseCase;
import id.fathonyfath.quran.lite.useCase.UseCaseCallback;
import id.fathonyfath.quran.lite.useCase.UseCaseProvider;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.ViewUtil;
import id.fathonyfath.quran.lite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quran.lite.views.common.CloseView;
import id.fathonyfath.quran.lite.views.common.ToolbarView;
import id.fathonyfath.quran.lite.views.common.WrapperView;
import id.fathonyfath.quran.lite.views.surahList.SurahAdapter;

public class SearchSurahView extends WrapperView implements ViewCallback {

    private final List<Surah> surahList = new ArrayList<>();
    private final ListView surahListView;
    private final SurahAdapter surahAdapter;

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
            unregisterAndClearUseCase();

            updateSearchResult(data);
        }

        @Override
        public void onError(Throwable throwable) {
            unregisterAndClearUseCase();

            Toast.makeText(getContext(), "Keyword yang anda masukkan tidak memenuhi syarat minimum karakter.", Toast.LENGTH_SHORT).show();
        }
    };

    private OnViewEventListener onViewEventListener;

    public SearchSurahView(Context context) {
        super(context);

        setId(Res.Id.searchSurahView);

        this.surahListView = new ListView(getContext());
        this.surahAdapter = new SurahAdapter(getContext(), this.surahList);

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

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    public void setOnViewEventListener(OnViewEventListener onViewEventListener) {
        this.onViewEventListener = onViewEventListener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SearchSurahViewState viewState = new SearchSurahViewState(super.onSaveInstanceState());
        viewState.surahList = this.surahList;
        return viewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SearchSurahViewState viewState = (SearchSurahViewState) state;
        super.onRestoreInstanceState(viewState.getSuperState());
        restoreSurahList(viewState.surahList);
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

    private void initConfiguration() {
        setBackgroundColor(Color.WHITE);
    }

    private void initView() {
        this.surahListView.setId(Res.Id.searchSurahView_surahListView);
        this.surahListView.setAdapter(this.surahAdapter);

        addView(this.surahListView);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.baseColor());
            ViewUtil.setDefaultSelectableBackgroundDrawable(this.surahListView, theme.contrastColor());
        }
    }

    private void updateSearchResult(List<Surah> surahList) {
        this.surahAdapter.clear();
        this.surahAdapter.addAll(surahList);
        this.surahAdapter.notifyDataSetChanged();
    }

    private void restoreSurahList(List<Surah> surahList) {
        this.surahList.clear();
        this.surahList.addAll(surahList);
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

    private static class SearchSurahViewState extends BaseSavedState {

        public static final Parcelable.Creator<SearchSurahViewState> CREATOR
                = new Parcelable.ClassLoaderCreator<SearchSurahViewState>() {
            @Override
            public SearchSurahViewState createFromParcel(Parcel in) {
                return new SearchSurahViewState(in, null);
            }

            @Override
            public SearchSurahViewState createFromParcel(Parcel in, ClassLoader loader) {
                return new SearchSurahViewState(in, loader);
            }

            @Override
            public SearchSurahViewState[] newArray(int size) {
                return new SearchSurahViewState[size];
            }
        };

        private List<Surah> surahList = new ArrayList<>();

        public SearchSurahViewState(Parcel source, ClassLoader loader) {
            super(source);

            int size = source.readInt();
            Surah[] surahArray = new Surah[size];
            source.readTypedArray(surahArray, Surah.CREATOR);

            this.surahList.clear();
            this.surahList.addAll(Arrays.asList(surahArray));
        }

        public SearchSurahViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(this.surahList.size());
            Surah[] surahArray = this.surahList.toArray(new Surah[0]);
            out.writeTypedArray(surahArray, flags);
        }
    }
}
