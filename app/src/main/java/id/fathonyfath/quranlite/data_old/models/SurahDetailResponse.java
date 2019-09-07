package id.fathonyfath.quranlite.data_old.models;

import java.util.Map;

public class SurahDetailResponse {

    public int number;
    public String name;
    public String name_latin;
    public int number_of_ayah;
    public Map<Integer, String> text;
    public SurahTranslationsResponse translations;
    public SurahTafsirsResponse tafsir;
}
