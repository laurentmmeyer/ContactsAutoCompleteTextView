package com.meyerlaurent.cactv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by laurentmeyer on 06/09/15.
 */
public class DemoAdapter extends CustomViewAdapter {

    public DemoAdapter(Context context, AutoCompleteContactTextView.TYPE_OF_DATA data, String styleOfDifferentLetters, boolean typedLetterAreDifferent, AsyncLoad load, int colorData, int colorName, boolean displayPhoto) {
        super(context, data, styleOfDifferentLetters, typedLetterAreDifferent, load);
        this.colorData = colorData;
        this.colorName = colorName;
        this.displayPhoto = displayPhoto;
    }

    boolean displayPhoto;
    int colorName, colorData;

    @Override
    public int getCount() {
        return displayed == null ? 0 : displayed.size();
    }

    @Override
    public Object getItem(int position) {
        return displayed.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if (convertView == null) {
            v = inflater.inflate(R.layout.layout_cell, null);
        } else {
            v = convertView;
        }
        TextView name = (TextView) v.findViewById(R.id.cell_name);
        TextView data = (TextView) v.findViewById(R.id.cell_data);
        name.setText(((People) getItem(position)).getName());
        name.setTextColor(colorName);
        data.setText(((People) getItem(position)).getData());
        data.setTextColor(colorData);
        if (displayPhoto) {
            ImageView iv = (ImageView) v.findViewById(R.id.thumbnail_picture);
            People p = (People) getItem(position);
            if (p.getPicture() != null) {
                iv.setImageBitmap(p.getPicture());
                iv.setVisibility(View.VISIBLE);
            }
        }
        return v;
    }
}
