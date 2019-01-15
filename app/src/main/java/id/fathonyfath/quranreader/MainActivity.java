package id.fathonyfath.quranreader;

import android.app.Activity;
import android.os.Bundle;

import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.data.remote.QuranJsonService;
import id.fathonyfath.quranreader.views.MainView;

public class MainActivity extends Activity {

    public static final String QURAN_REPOSITORY_SERVICE = "MainActivity.QuranRepository";

    private final QuranJsonService quranJsonService = new QuranJsonService();

    private final QuranRepository quranRepository = new QuranRepository(quranJsonService);

    private MainView mainView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainView = new MainView(this);
        setContentView(this.mainView);
    }

    @Override
    public void onBackPressed() {
        if (!this.mainView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if (name.equals(QURAN_REPOSITORY_SERVICE)) {
            return quranRepository;
        }
        return super.getSystemService(name);
    }
}
