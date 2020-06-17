package id.fathonyfath.quran.lite.data.json.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import id.fathonyfath.quran.lite.data.json.SurahJSON;

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

    private static SurahJSON parseJSONObjectToSurahJSON(JSONObject index) throws JSONException {
        final SurahJSON surah = new SurahJSON();
        surah.number = index.getInt("number");
        surah.name = index.getString("name");
        surah.name_latin = index.getString("name_latin");
        surah.number_of_ayah = index.getInt("number_of_ayah");
        return surah;
    }
}
