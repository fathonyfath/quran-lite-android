package dev.fathony.android.quranlite.data.json.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import dev.fathony.android.quranlite.data.json.SurahJSON;

public class SurahJSONParser {

    public static Map<Integer, SurahJSON> parseJSONObject(JSONObject jsonObject) throws JSONException {
        final Map<Integer, SurahJSON> response = new TreeMap<>();

        JSONArray keys = jsonObject.names();
        for (int i = 0; i < keys.length(); i++) {
            Integer key = keys.getInt(i);
            JSONObject value = jsonObject.getJSONObject(String.valueOf(key));
            response.put(key, parseJSONObjectToSurahJSON(value));
        }

        return response;
    }

    public static SurahJSON parseJSONObjectToSurahJSON(JSONObject index) throws JSONException {
        final SurahJSON surah = new SurahJSON();
        surah.number = index.getInt("number");
        surah.name = index.getString("name");
        surah.name_latin = index.getString("name_latin");
        surah.number_of_ayah = index.getInt("number_of_ayah");
        return surah;
    }

    public static JSONObject parseSurahJSONToJSONObject(SurahJSON surahJSON) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", surahJSON.number);
        jsonObject.put("name", surahJSON.name);
        jsonObject.put("name_latin", surahJSON.name_latin);
        jsonObject.put("number_of_ayah", surahJSON.number_of_ayah);
        return jsonObject;
    }
}
