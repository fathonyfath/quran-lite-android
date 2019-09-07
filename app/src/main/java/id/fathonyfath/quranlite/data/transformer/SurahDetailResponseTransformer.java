package id.fathonyfath.quranlite.data.transformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import id.fathonyfath.quranlite.data.models.SurahDetailResponse;
import id.fathonyfath.quranlite.data.models.SurahTafsirResponse;
import id.fathonyfath.quranlite.data.models.SurahTafsirSourceResponse;
import id.fathonyfath.quranlite.data.models.SurahTafsirsResponse;
import id.fathonyfath.quranlite.data.models.SurahTranslationResponse;
import id.fathonyfath.quranlite.data.models.SurahTranslationsResponse;

public class SurahDetailResponseTransformer {

    public static SurahDetailResponse parseJSONObjectToSurahDetailResponse(JSONObject jsonObject) throws JSONException {
        final SurahDetailResponse surahDetailResponse = new SurahDetailResponse();
        surahDetailResponse.number = jsonObject.getInt("number");
        surahDetailResponse.name = jsonObject.getString("name");
        surahDetailResponse.name_latin = jsonObject.getString("name_latin");
        surahDetailResponse.number_of_ayah = jsonObject.getInt("number_of_ayah");
        surahDetailResponse.text = parseJSONObjectToMapOfIntegerString(jsonObject.getJSONObject("text"));
        surahDetailResponse.translations = parseJSONObjectToSurahTranslationsResponse(jsonObject.getJSONObject("translations"));
        surahDetailResponse.tafsir = parseJSONObjectToSurahTafsirsResponse(jsonObject.getJSONObject("tafsir"));
        return surahDetailResponse;
    }

    private static SurahTranslationsResponse parseJSONObjectToSurahTranslationsResponse(JSONObject translations) throws JSONException {
        final SurahTranslationsResponse surahTranslations = new SurahTranslationsResponse();

        final Map<String, SurahTranslationResponse> translationsMap = new TreeMap<>();

        JSONArray keys = translations.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = translations.getJSONObject(String.valueOf(key));
            translationsMap.put(key, parseJSONOBjectToSurahTranslationResponse(value));
        }

        surahTranslations.translations = translationsMap;

        return surahTranslations;
    }

    private static SurahTranslationResponse parseJSONOBjectToSurahTranslationResponse(JSONObject value) throws JSONException {
        final SurahTranslationResponse surahTranslation = new SurahTranslationResponse();

        surahTranslation.name = value.getString("name");
        surahTranslation.text = parseJSONObjectToMapOfIntegerString(value.getJSONObject("text"));

        return surahTranslation;
    }

    private static SurahTafsirsResponse parseJSONObjectToSurahTafsirsResponse(JSONObject tafsir) throws JSONException {
        final SurahTafsirsResponse surahTafsirs = new SurahTafsirsResponse();
        final Map<String, SurahTafsirResponse> tafsirsMap = new TreeMap<>();

        JSONArray keys = tafsir.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = tafsir.getJSONObject(String.valueOf(key));
            tafsirsMap.put(key, parseJSONObjectToSurahTafsirResponse(value));
        }

        surahTafsirs.tafsir = tafsirsMap;

        return surahTafsirs;
    }

    private static SurahTafsirResponse parseJSONObjectToSurahTafsirResponse(JSONObject tafsirSources) throws JSONException {
        final SurahTafsirResponse surahTafsirResponse = new SurahTafsirResponse();
        final Map<String, SurahTafsirSourceResponse> tafsirMap = new TreeMap<>();

        JSONArray keys = tafsirSources.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = tafsirSources.getJSONObject(String.valueOf(key));
            tafsirMap.put(key, parseJSONObjectToSurahTafsirSourceResponse(value));
        }

        surahTafsirResponse.sources = tafsirMap;

        return surahTafsirResponse;
    }

    private static SurahTafsirSourceResponse parseJSONObjectToSurahTafsirSourceResponse(JSONObject source) throws JSONException {
        final SurahTafsirSourceResponse tafsirSource = new SurahTafsirSourceResponse();

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
