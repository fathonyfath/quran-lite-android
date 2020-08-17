package id.fathonyfath.quran.lite.data;

import org.json.JSONException;
import org.json.JSONObject;

import id.fathonyfath.quran.lite.data.source.preferences.BookmarkPreferencesSource;
import id.fathonyfath.quran.lite.models.Bookmark;

public class BookmarkRepository {
    private static final String SURAH_NUMBER_KEY = "surahNumber";
    private static final String SURAH_NAME_KEY = "surahName";
    private static final String SURAH_NAME_IN_LATIN_KEY = "surahNameInLatin";
    private static final String LAST_READ_AYAH_KEY = "lastReadAyah";

    private final BookmarkPreferencesSource bookmarkPreferencesSource;

    public BookmarkRepository(BookmarkPreferencesSource bookmarkPreferencesSource) {
        this.bookmarkPreferencesSource = bookmarkPreferencesSource;
    }

    public void putBookmark(Bookmark bookmark) {
        try {
            final JSONObject bookmarkJson = new JSONObject();
            bookmarkJson.put(SURAH_NUMBER_KEY, bookmark.getSurahNumber());
            bookmarkJson.put(SURAH_NAME_KEY, bookmark.getSurahName());
            bookmarkJson.put(SURAH_NAME_IN_LATIN_KEY, bookmark.getSurahNameInLatin());
            bookmarkJson.put(LAST_READ_AYAH_KEY, bookmark.getLastReadAyah());
            this.bookmarkPreferencesSource.putValue(bookmarkJson.toString());
        } catch (JSONException ignored) {

        }
    }

    public Bookmark getBookmark() {
        try {
            final String jsonString = this.bookmarkPreferencesSource.getValue();
            final JSONObject bookmarkJson = new JSONObject(jsonString);

            final int surahNumber = bookmarkJson.getInt(SURAH_NUMBER_KEY);
            final String surahName = bookmarkJson.getString(SURAH_NAME_KEY);
            final String surahNameInLatin = bookmarkJson.getString(SURAH_NAME_IN_LATIN_KEY);
            final int lastReadAyah = bookmarkJson.getInt(LAST_READ_AYAH_KEY);

            return new Bookmark(surahNumber, surahName, surahNameInLatin, lastReadAyah);
        } catch (JSONException ignored) {
            return null;
        }
    }
}
