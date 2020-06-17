package id.fathonyfath.quran.lite.views.surahList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import java.util.List;

import id.fathonyfath.quran.lite.models.Surah;

public class SurahAdapter extends ArrayAdapter<Surah> {

    public SurahAdapter(Context context, List<Surah> surahList) {
        super(context, 0, surahList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Surah currentSurah = getItem(position);

        if (convertView == null) {
            SurahView surahView = new SurahView(getContext());
            surahView.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            convertView = surahView;
        }

        if (convertView instanceof SurahView) {
            ((SurahView) convertView).bindData(currentSurah);
        }

        return convertView;
    }
}
