package id.fathonyfath.quranreader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.data.remote.QuranJsonService;
import id.fathonyfath.quranreader.utils.UnitConverter;
import id.fathonyfath.quranreader.views.MainView;

public class MainActivity extends Activity {

    public static final String QURAN_REPOSITORY_SERVICE = "MainActivity.QuranRepository";

    private final QuranJsonService quranJsonService = new QuranJsonService();

    private final QuranRepository quranRepository = new QuranRepository(quranJsonService);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MainView mainView = new MainView(this);
        setContentView(mainView);

        final SampleView sampleView = new SampleView(this);
//        setContentView(sampleView);
    }

    @Override
    public Object getSystemService(String name) {
        if (name.equals(QURAN_REPOSITORY_SERVICE)) {
            return quranRepository;
        }
        return super.getSystemService(name);
    }

    private static class SampleView extends FrameLayout {

        private final FrameLayout frameLayout;
        private boolean isResuming = false;

        public SampleView(Context context) {
            super(context);

            setId(1);

            this.frameLayout = new FrameLayout(context);
            this.frameLayout.setId(2);
            addView(this.frameLayout, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }

        @Override
        protected void onRestoreInstanceState(Parcelable state) {
            super.onRestoreInstanceState(state);
            this.isResuming = true;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();

            if (!isResuming) {
                initView();
            }
        }

        private void initView() {
            View view = new View(getContext());
            view.setBackgroundColor(Color.RED);
            addView(view, new LayoutParams(
                    (int) UnitConverter.fromDpToPx(getContext(), 80f),
                    (int) UnitConverter.fromDpToPx(getContext(), 80f)
            ));
        }
    }
}
