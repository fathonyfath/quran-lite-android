package id.fathonyfath.quranlite.data.json;

import java.util.Map;

public class SurahDetailJSON {

    public int number;
    public String name;
    public String name_latin;
    public int number_of_ayah;
    public Map<Integer, String> text;
    public SurahTranslationsJSON translations;
    public SurahTafsirsJSON tafsir;
}
