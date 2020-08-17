package id.fathonyfath.quran.lite.models;

public class Bookmark {

    private final int surahNumber;
    private final String surahName;
    private final String surahNameInLatin;
    private final int lastReadAyah;

    public Bookmark(int surahNumber, String surahName, String surahNameInLatin, int lastReadAyah) {
        this.surahNumber = surahNumber;
        this.surahName = surahName;
        this.surahNameInLatin = surahNameInLatin;
        this.lastReadAyah = lastReadAyah;
    }

    public int getSurahNumber() {
        return surahNumber;
    }

    public String getSurahName() {
        return surahName;
    }

    public String getSurahNameInLatin() {
        return surahNameInLatin;
    }

    public int getLastReadAyah() {
        return lastReadAyah;
    }
}
