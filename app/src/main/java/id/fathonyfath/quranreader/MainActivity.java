package id.fathonyfath.quranreader;

import android.app.Activity;
import android.os.Bundle;

import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.data.remote.QuranJsonService;
import id.fathonyfath.quranreader.views.MainView;

public class MainActivity extends Activity {

    private final QuranJsonService quranJsonService = new QuranJsonService();

    public static final String QURAN_REPOSITORY_SERVICE = "MainActivity.QuranRepository";
    private final QuranRepository quranRepository = new QuranRepository(quranJsonService);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MainView mainView = new MainView(this);
        setContentView(mainView);
    }

    @Override
    public Object getSystemService(String name) {
        if (name.equals(QURAN_REPOSITORY_SERVICE)) {
            return quranRepository;
        }
        return super.getSystemService(name);
    }
}
