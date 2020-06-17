package id.fathonyfath.quran.lite.themes;

public class DarkTheme extends BaseTheme {

    @Override
    public int primary() {
        return 0xFF424242;
    }

    @Override
    public int primaryLight() {
        return 0xFF6D6D6D;
    }

    @Override
    public int primaryDark() {
        return 0xFF1B1B1B;
    }

    @Override
    public int secondary() {
        return 0xFF212121;
    }

    @Override
    public int secondaryLight() {
        return 0xFF484848;
    }

    @Override
    public int secondaryDark() {
        return 0xFF000000;
    }

    @Override
    public int objectOnPrimary() {
        return 0xFFFFFFFF;
    }

    @Override
    public int objectOnSecondary() {
        return 0;
    }
}
