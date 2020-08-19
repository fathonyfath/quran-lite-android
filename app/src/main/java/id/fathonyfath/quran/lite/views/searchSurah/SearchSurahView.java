package id.fathonyfath.quran.lite.views.searchSurah;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import id.fathonyfath.quran.lite.Res;
import id.fathonyfath.quran.lite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quran.lite.views.common.CloseView;
import id.fathonyfath.quran.lite.views.common.WrapperView;

public class SearchSurahView extends WrapperView implements ViewCallback {

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
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {
        if (getSearchInput() != null) {
            getSearchInput().clearFocus();
            getSearchInput().setText("");
            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getSearchInput().getWindowToken(), 0);
        }
    }

    public interface OnViewEventListener {
        void onCloseClicked();
    }
}
