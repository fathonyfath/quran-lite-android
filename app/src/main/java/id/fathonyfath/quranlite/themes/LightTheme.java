package id.fathonyfath.quranlite.themes;

public class LightTheme extends BaseTheme {

    @Override
    public int primary() {
        return 0xFFFAFAFA;
    }

    @Override
    public int primaryLight() {
        return 0xFFFFFFFF;
    }

    @Override
    public int primaryDark() {
        return 0xFFC7C7C7;
    }

    @Override
    public int secondary() {
        return 0xFFEEEEEE;
    }

    @Override
    public int secondaryLight() {
        return 0xFFFFFFFF;
    }

    @Override
    public int secondaryDark() {
        return 0xFFBCBCBC;
    }

    @Override
    public int objectOnPrimary() {
        return 0xFF000000;
    }

    @Override
    public int objectOnSecondary() {
        return 0xFF000000;
    }
}
