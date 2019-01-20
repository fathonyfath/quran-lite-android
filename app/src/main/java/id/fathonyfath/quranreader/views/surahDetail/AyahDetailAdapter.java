package id.fathonyfath.quranreader.views.surahDetail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import java.util.List;

public class AyahDetailAdapter extends ArrayAdapter<AyahDetailViewType> {

    public AyahDetailAdapter(Context context, List<AyahDetailViewType> surahList) {
        super(context, 0, surahList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AyahDetailViewType currentItem = getItem(position);

        if (convertView == null) {
            AyahView ayahView = new AyahView(getContext());
            ayahView.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            convertView = ayahView;
        }

        if (convertView instanceof AyahView && currentItem instanceof AyahDetailViewType.AyahViewModel) {
            AyahView ayahView = (AyahView) convertView;
            AyahDetailViewType.AyahViewModel ayahViewModel = (AyahDetailViewType.AyahViewModel) currentItem;
            ayahView.updateAyah(ayahViewModel);
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}