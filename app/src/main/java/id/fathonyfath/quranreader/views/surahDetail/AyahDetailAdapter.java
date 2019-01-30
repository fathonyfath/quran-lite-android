package id.fathonyfath.quranreader.views.surahDetail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import java.util.List;

public class AyahDetailAdapter extends ArrayAdapter<AyahDetailViewType> {

    private static final int BASMALAH_VIEW_TYPE = 0;
    private static final int AYAH_VIEW_TYPE = 1;

    public AyahDetailAdapter(Context context, List<AyahDetailViewType> surahList) {
        super(context, 0, surahList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AyahDetailViewType currentItem = getItem(position);

        if (convertView == null) {
            convertView = getViewForType(getItemViewType(position));
        }

        if (convertView instanceof AyahView && currentItem instanceof AyahDetailViewType.AyahViewModel) {
            AyahView ayahView = (AyahView) convertView;
            AyahDetailViewType.AyahViewModel ayahViewModel = (AyahDetailViewType.AyahViewModel) currentItem;
            ayahView.updateAyah(ayahViewModel);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        final AyahDetailViewType item = getItem(position);
        if (item instanceof AyahDetailViewType.BasmalahViewModel) {
            return BASMALAH_VIEW_TYPE;
        } else if (item instanceof AyahDetailViewType.AyahViewModel) {
            return AYAH_VIEW_TYPE;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private View getViewForType(int type) {
        if (type == BASMALAH_VIEW_TYPE) {
            final BasmalahView view = new BasmalahView(getContext());
            view.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            return view;
        } else if (type == AYAH_VIEW_TYPE) {
            final AyahView view = new AyahView(getContext());
            view.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            return view;
        } else {
            return null;
        }
    }
}