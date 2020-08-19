package id.fathonyfath.quran.lite.views.searchSurah;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.LinkedHashSet;

import id.fathonyfath.quran.lite.Res;
import id.fathonyfath.quran.lite.utils.UnitConverter;
import id.fathonyfath.quran.lite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quran.lite.views.common.CloseView;
import id.fathonyfath.quran.lite.views.common.WrapperView;

public class SearchSurahView extends WrapperView implements ViewCallback {

    public SearchSurahView(Context context) {
        super(context);

        setId(Res.Id.searchSurahView);

        this.setToolbarLeftView(new CloseView(getContext()));

        this.setElevationAlpha(0.1f);
        this.setToolbarIsSearchMode(true);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }
}
