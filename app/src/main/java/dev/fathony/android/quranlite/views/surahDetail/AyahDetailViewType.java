package dev.fathony.android.quranlite.views.surahDetail;

public abstract class AyahDetailViewType {

    public static class BasmalahViewModel extends AyahDetailViewType {
    }

    public static class AyahViewModel extends AyahDetailViewType {

        public final Integer ayahNumber;
        public final String ayahContent;
        public final String ayahTranslation;

        public AyahViewModel(Integer ayahNumber, String ayahContent, String ayahTranslation) {
            this.ayahNumber = ayahNumber;
            this.ayahContent = ayahContent;
            this.ayahTranslation = ayahTranslation;
        }
    }
}
