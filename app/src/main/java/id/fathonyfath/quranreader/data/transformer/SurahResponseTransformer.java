package id.fathonyfath.quranreader.data.transformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import id.fathonyfath.quranreader.data.models.SurahResponse;

public class SurahResponseTransformer {

    public static Map<Integer, SurahResponse> parseJSONObjectToMapOfIntegerSurahResponse(JSONObject jsonObject) throws JSONException {
        final Map<Integer, SurahResponse> response = new TreeMap<>();

        JSONArray keys = jsonObject.names();
        for (int i = 0; i < keys.length(); i++) {
            Integer key = keys.getInt(i);
            JSONObject value = jsonObject.getJSONObject(String.valueOf(key));
            response.put(key, parseJSONObjectToSurahResponse(value));
        }

        return response;
    }

    private static SurahResponse parseJSONObjectToSurahResponse(JSONObject index) throws JSONException {
        final SurahResponse surahResponse = new SurahResponse();
        surahResponse.number = index.getInt("number");
        surahResponse.name = index.getString("name");
        surahResponse.name_latin = index.getString("name_latin");
        surahResponse.number_of_ayah = index.getInt("number_of_ayah");
        return surahResponse;
    }
}
