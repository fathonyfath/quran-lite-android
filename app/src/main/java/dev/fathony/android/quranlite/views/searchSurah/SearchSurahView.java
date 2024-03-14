package dev.fathony.android.quranlite.views.searchSurah;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.fathony.android.quranlite.Res;
import dev.fathony.android.quranlite.models.Surah;
import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.useCase.DoSearchUseCase;
import dev.fathony.android.quranlite.useCase.UseCaseCallback;
import dev.fathony.android.quranlite.useCase.UseCaseProvider;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;
import dev.fathony.android.quranlite.utils.ViewUtil;
import dev.fathony.android.quranlite.utils.viewLifecycle.ViewCallback;
import dev.fathony.android.quranlite.views.common.CloseView;
import dev.fathony.android.quranlite.views.common.ToolbarView;
import dev.fathony.android.quranlite.views.common.WrapperView;
import dev.fathony.android.quranlite.views.surahList.SurahAdapter;

public class SearchSurahView extends WrapperView implements ViewCallback {

    private final List<Surah> surahList = new ArrayList<>();
    private final ListView surahListView;
    private final EnterSearchQueryView enterSearchQueryView;
    private final NoResultView noResultView;
    private final SurahAdapter surahAdapter;
    private final ProgressBar progressBar;

    private boolean hasSearched = false;
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
            hasSearched = false;

            unregisterAndClearUseCase();

            updateViewStateInit();

            Toast.makeText(getContext(), "Keyword yang anda masukkan tidak memenuhi syarat minimum karakter.", Toast.LENGTH_SHORT).show();
        }
    };
    private final ToolbarView.OnSearchListener searchListener = new ToolbarView.OnSearchListener() {
        @Override
        public void onSearch(String query) {
            if (getSearchInput() != null) {
                getSearchInput().clearFocus();
                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getSearchInput().getWindowToken(), 0);
            }

            updateViewStateLoading();

            doSearchProcess(query);

            hasSearched = true;
        }
    };
    private OnViewEventListener onViewEventListener;
    private final View.OnClickListener onCloseClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onViewEventListener != null) {
                onViewEventListener.onCloseClicked();
            }
        }
    };
    private final AdapterView.OnItemClickListener onSurahItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (SearchSurahView.this.onViewEventListener != null) {
                Surah selectedSurah = SearchSurahView.this.surahAdapter.getItem(position);
                SearchSurahView.this.onViewEventListener.onSurahSelected(selectedSurah, 0);
            }
        }
    };

    public SearchSurahView(Context context) {
        super(context);

        setId(Res.Id.searchSurahView);

        this.surahListView = new ListView(getContext());
        this.enterSearchQueryView = new EnterSearchQueryView(getContext());
        this.noResultView = new NoResultView(getContext());
        this.progressBar = new ProgressBar(getContext());

        this.surahAdapter = new SurahAdapter(getContext(), this.surahList);

        CloseView closeView = new CloseView(context);
        closeView.setOnClickListener(onCloseClickListener);

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
        if (getSearchInput() != null) {
            viewState.searchQuery = getSearchInput().getText().toString();
        }
        viewState.hasSearched = this.hasSearched;
        return viewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SearchSurahViewState viewState = (SearchSurahViewState) state;
        super.onRestoreInstanceState(viewState.getSuperState());
        restoreSurahList(viewState.surahList);

        if (getSearchInput() != null) {
            getSearchInput().setText(viewState.searchQuery, TextView.BufferType.EDITABLE);
        }

        this.hasSearched = viewState.hasSearched;
    }

    @Override
    public void onStart() {
        if (getSearchInput() != null) {
            getSearchInput().requestFocus();
            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(getSearchInput(), InputMethodManager.SHOW_IMPLICIT);
        }

        updateViewStateInit();
    }

    @Override
    public void onResume() {
        tryToRestoreUseCase();

        this.setOnSearchListener(searchListener);
        this.surahListView.setOnItemClickListener(onSurahItemClickListener);

        if (!hasSearched) {
            updateViewStateInit();
        } else {
            if (this.surahList.isEmpty()) {
                updateViewStateEmpty();
            } else {
                updateViewStateResult();
            }
        }
    }

    @Override
    public void onPause() {
        unregisterUseCase();

        this.surahListView.setOnItemClickListener(null);

        this.setOnSearchListener(null);
    }

    @Override
    public void onStop() {
        if (getSearchInput() != null) {
            getSearchInput().clearFocus();
            getSearchInput().setText("");
            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getSearchInput().getWindowToken(), 0);
        }

        this.surahList.clear();
        this.surahAdapter.notifyDataSetChanged();

        if (getSearchInput() != null) {
            getSearchInput().setText("", TextView.BufferType.EDITABLE);
        }

        hasSearched = false;
    }

    private void initConfiguration() {
        setBackgroundColor(Color.WHITE);
    }

    private void initView() {
        this.surahListView.setId(Res.Id.searchSurahView_surahListView);
        this.surahListView.setAdapter(this.surahAdapter);

        final FrameLayout.LayoutParams enterSearchQueryParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        enterSearchQueryParams.gravity = Gravity.CENTER_HORIZONTAL;
        enterSearchQueryParams.topMargin = (int) UnitConverter.fromDpToPx(getContext(), 72.0f);
        this.enterSearchQueryView.setLayoutParams(enterSearchQueryParams);

        final FrameLayout.LayoutParams noResultParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        noResultParams.gravity = Gravity.CENTER_HORIZONTAL;
        noResultParams.topMargin = (int) UnitConverter.fromDpToPx(getContext(), 72.0f);

        final FrameLayout.LayoutParams progressBarParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressBarParams.gravity = Gravity.CENTER;

        this.enterSearchQueryView.setLayoutParams(enterSearchQueryParams);
        this.noResultView.setLayoutParams(enterSearchQueryParams);
        this.progressBar.setLayoutParams(progressBarParams);

        this.progressBar.setIndeterminate(true);

        addView(this.surahListView);
        addView(this.enterSearchQueryView);
        addView(this.noResultView);
        addView(this.progressBar);
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

        if (surahList.isEmpty()) {
            updateViewStateEmpty();
        } else {
            updateViewStateResult();
        }
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

    private void updateViewStateInit() {
        this.surahListView.setVisibility(View.GONE);
        this.enterSearchQueryView.setVisibility(View.VISIBLE);
        this.noResultView.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.GONE);
    }

    private void updateViewStateResult() {
        this.surahListView.setVisibility(View.VISIBLE);
        this.enterSearchQueryView.setVisibility(View.GONE);
        this.noResultView.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.GONE);
    }

    private void updateViewStateEmpty() {
        this.surahListView.setVisibility(View.GONE);
        this.enterSearchQueryView.setVisibility(View.GONE);
        this.noResultView.setVisibility(View.VISIBLE);
        this.progressBar.setVisibility(View.GONE);
    }

    private void updateViewStateLoading() {
        this.surahListView.setVisibility(View.GONE);
        this.enterSearchQueryView.setVisibility(View.GONE);
        this.noResultView.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    public interface OnViewEventListener {
        void onSurahSelected(Surah selectedSurah, int lastReadingAyah);

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
        private String searchQuery = "";
        private boolean hasSearched = false;

        public SearchSurahViewState(Parcel source, ClassLoader loader) {
            super(source);

            int size = source.readInt();
            Surah[] surahArray = new Surah[size];
            source.readTypedArray(surahArray, Surah.CREATOR);

            this.surahList.clear();
            this.surahList.addAll(Arrays.asList(surahArray));

            this.searchQuery = source.readString();

            this.hasSearched = source.readInt() >= 1;
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

            out.writeString(this.searchQuery);

            out.writeInt((this.hasSearched) ? 1 : 0);
        }
    }
}
