package dev.fathony.android.quranlite.data.json.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import dev.fathony.android.quranlite.data.json.SurahDetailJSON;
import dev.fathony.android.quranlite.data.json.SurahTafsirJSON;
import dev.fathony.android.quranlite.data.json.SurahTafsirSourceJSON;
import dev.fathony.android.quranlite.data.json.SurahTafsirsJSON;
import dev.fathony.android.quranlite.data.json.SurahTranslationJSON;
import dev.fathony.android.quranlite.data.json.SurahTranslationsJSON;

public class SurahDetailJSONParser {

    public static SurahDetailJSON parseJSONObject(JSONObject jsonObject) throws JSONException {
        final SurahDetailJSON surahDetail = new SurahDetailJSON();
        surahDetail.number = jsonObject.getInt("number");
        surahDetail.name = jsonObject.getString("name");
        surahDetail.name_latin = jsonObject.getString("name_latin");
        surahDetail.number_of_ayah = jsonObject.getInt("number_of_ayah");
        surahDetail.text = parseJSONObjectToMapOfIntegerString(jsonObject.getJSONObject("text"));
        surahDetail.translations = parseJSONObjectToSurahTranslationsJSON(jsonObject.getJSONObject("translations"));
        surahDetail.tafsir = parseJSONObjectToSurahTafsirsJSON(jsonObject.getJSONObject("tafsir"));
        return surahDetail;
    }

    private static SurahTranslationsJSON parseJSONObjectToSurahTranslationsJSON(JSONObject translations) throws JSONException {
        final SurahTranslationsJSON surahTranslations = new SurahTranslationsJSON();

        final Map<String, SurahTranslationJSON> translationsMap = new TreeMap<>();

        JSONArray keys = translations.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = translations.getJSONObject(String.valueOf(key));
            translationsMap.put(key, parseJSONOBjectToSurahTranslationJSON(value));
        }

        surahTranslations.translations = translationsMap;

        return surahTranslations;
    }

    private static SurahTranslationJSON parseJSONOBjectToSurahTranslationJSON(JSONObject value) throws JSONException {
        final SurahTranslationJSON surahTranslation = new SurahTranslationJSON();

        surahTranslation.name = value.getString("name");
        surahTranslation.text = parseJSONObjectToMapOfIntegerString(value.getJSONObject("text"));

        return surahTranslation;
    }

    private static SurahTafsirsJSON parseJSONObjectToSurahTafsirsJSON(JSONObject tafsir) throws JSONException {
        final SurahTafsirsJSON surahTafsirs = new SurahTafsirsJSON();
        final Map<String, SurahTafsirJSON> tafsirsMap = new TreeMap<>();

        JSONArray keys = tafsir.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = tafsir.getJSONObject(String.valueOf(key));
            tafsirsMap.put(key, parseJSONObjectToSurahTafsirJSON(value));
        }

        surahTafsirs.tafsir = tafsirsMap;

        return surahTafsirs;
    }

    private static SurahTafsirJSON parseJSONObjectToSurahTafsirJSON(JSONObject tafsirSources) throws JSONException {
        final SurahTafsirJSON surahTafsirJSON = new SurahTafsirJSON();
        final Map<String, SurahTafsirSourceJSON> tafsirMap = new TreeMap<>();

        JSONArray keys = tafsirSources.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = tafsirSources.getJSONObject(String.valueOf(key));
            tafsirMap.put(key, parseJSONObjectToSurahTafsirSourceJSON(value));
        }

        surahTafsirJSON.sources = tafsirMap;

        return surahTafsirJSON;
    }

    private static SurahTafsirSourceJSON parseJSONObjectToSurahTafsirSourceJSON(JSONObject source) throws JSONException {
        final SurahTafsirSourceJSON tafsirSource = new SurahTafsirSourceJSON();

        tafsirSource.name = source.getString("name");
        tafsirSource.source = source.getString("source");
        tafsirSource.text = parseJSONObjectToMapOfIntegerString(source.getJSONObject("text"));

        return tafsirSource;
    }

    private static Map<Integer, String> parseJSONObjectToMapOfIntegerString(JSONObject text) throws JSONException {
        final Map<Integer, String> response = new TreeMap<>();

        JSONArray keys = text.names();
        for (int i = 0; i < keys.length(); i++) {
            Integer key = keys.getInt(i);
            String value = text.getString(String.valueOf(key));
            response.put(key, value);
        }

        return response;
    }
}
