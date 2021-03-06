package id.thony.android.quranlite.data.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.thony.android.quranlite.data.json.SurahJSON;
import id.thony.android.quranlite.models.Surah;

public class SurahMapper {
    public static List<Surah> map(Map<Integer, SurahJSON> remoteResponse) {
        final List<Surah> surahList = new ArrayList<>();
        for (SurahJSON surah : remoteResponse.values()) {
            surahList.add(new Surah(
                    surah.number,
                    surah.name,
                    surah.name_latin,
                    surah.number_of_ayah
            ));
        }
        return surahList;
    }
}
