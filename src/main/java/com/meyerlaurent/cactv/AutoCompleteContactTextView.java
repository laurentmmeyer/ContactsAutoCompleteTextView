package com.meyerlaurent.cactv;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by laurentmeyer on 02/03/15.
 */
public class AutoCompleteContactTextView extends AutoCompleteTextView {
    Context context;
    int colorData;
    int colorName;
    boolean shouldBeDifferent;
    String returnPattern;
    People selected = null;
    boolean hasCustomAdapter = false;
    CustomAdapter adapter;
    String typedLetterStyle;

    int xmlIntType = 0;

    boolean displayPhoto;

    enum TYPE_OF_DATA {PHONE, EMAIL, PHYSICAL_ADDRESS}

    private TYPE_OF_DATA type = TYPE_OF_DATA.PHONE;

    public AutoCompleteContactTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoCompleteContactTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setCustomAdapter(CustomAdapter adapter) {
        this.adapter = adapter;
        hasCustomAdapter = true;
        setAdapter(adapter);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((ContactsAdapter) AutoCompleteContactTextView.this.getAdapter()).getFilter().filter(s);
                selected = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setText(((People) AutoCompleteContactTextView.this.getAdapter().getItem(position)).getName().toString());
                selected = (People) getAdapter().getItem(position);
            }
        });
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PhoneNumberAutoComplete);
            colorData = array.getColor(R.styleable.PhoneNumberAutoComplete_colorOfData, getResources().getColor(android.R.color.holo_blue_dark));
            colorName = array.getColor(R.styleable.PhoneNumberAutoComplete_colorOfNames, getResources().getColor(android.R.color.black));
            shouldBeDifferent = array.getBoolean(R.styleable.PhoneNumberAutoComplete_typedLettersHaveDifferentStyle, false);
            if (shouldBeDifferent){
                typedLetterStyle = array.getInt(R.styleable.PhoneNumberAutoComplete_styleOfTypedLetters, 2)==1?"u":"b";
            }
            xmlIntType = array.getInt(R.styleable.PhoneNumberAutoComplete_typeOfData, 1);
            switch (xmlIntType){
                case 1:
                    type = TYPE_OF_DATA.PHONE;
                case 2:
                    type = TYPE_OF_DATA.EMAIL;
                case 3:
                    type = TYPE_OF_DATA.PHYSICAL_ADDRESS;
            }
            returnPattern = TextUtils.isEmpty(array.getString(R.styleable.PhoneNumberAutoComplete_getTextPattern)) ? "[Nn]: [P]" : array.getString(R.styleable.PhoneNumberAutoComplete_getTextPattern);
            displayPhoto = array.getBoolean(R.styleable.PhoneNumberAutoComplete_displayPhotoIfAvailable, false);
        }
        adapter = new ContactsAdapter(context, type);
        this.setAdapter(adapter);

    }

    public AutoCompleteContactTextView(Context context) {
        super(context);
        init(context, null);
        this.context = context;
    }

    @Override
    public Editable getText() {
        if (isSomeoneSelected()) {
            String temp = returnPattern;
            String name = selected.getName().toString();
            String data = selected.getData().toString();
            if (temp.contains("[Nn]")) {
                boolean isAdapted = Character.isUpperCase(name.toCharArray()[0]);
                if (!isAdapted) {
                    char[] array = name.toCharArray();
                    array[0] = Character.toUpperCase(array[0]);
                    name = String.valueOf(array);
                }
                temp.replace("[Nn]", name);
            }
            if (temp.contains("[N]")) {
                temp.replace("[N]", name.toUpperCase());
            }
            if (temp.contains("[n]")) {
                temp.replace("[n]", name.toLowerCase());
            }
            if (temp.contains("[d]")) {
                temp.replace("[d]", data);
            }
            return Editable.Factory.getInstance().newEditable(temp);
        }
        return super.getText();
    }

    private class ContactsAdapter extends CustomAdapter {
        ArrayList<People> toDisplayList = new ArrayList<>();
        TextView data;
        Filter filter;

        private ContactsAdapter(Context context) {
            super(context, type);
        }

        public ContactsAdapter(Context context, TYPE_OF_DATA type) {
            super(context, type);
        }

        @Override
        public int getCount() {
            return toDisplayList == null ? 0 : toDisplayList.size();
        }

        @Override
        public Object getItem(int position) {
            return toDisplayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
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
            data = (TextView) v.findViewById(R.id.cell_data);
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

        @Override
        public Filter getFilter() {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults r = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        r.values = dataList;
                        r.count = dataList.size();
                    } else {
                        ArrayList<People> filtered = new ArrayList<>();
                        for (People toFilter : dataList) {
                            if (toFilter.getName().toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                if (shouldBeDifferent) {
                                    Spanned text = setBold(constraint, toFilter);
                                    toFilter.setName(text);
                                }
                                filtered.add(toFilter);
                            } else if (toFilter.getData().toString().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                filtered.add(toFilter);
                            }
                        }
                        r.values = filtered;
                        r.count = filtered.size();
                    }
                    return r;
                }


                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    toDisplayList = (ArrayList<People>) results.values;
                    notifyDataSetChanged();
                }
            };
            return filter;
        }

        private Spanned setBold(CharSequence constraint, People toFilter) {
            String temp = toFilter.getName().toString().replace("<"+typedLetterStyle+">", "").replace("</"+typedLetterStyle+">", "");
            ArrayList<Integer> positions = new ArrayList<>();
            for (int i = -1; (i = temp.toLowerCase().indexOf(constraint.toString().toLowerCase(), i + 1)) != -1; ) {
                positions.add(i);
            }
            StringBuilder builder = new StringBuilder(temp);
            int offsetIntroduced = 0;
            for (Integer position : positions) {
                builder.insert(position + (offsetIntroduced * 7), "<"+typedLetterStyle+">");
                builder.insert(position + ((offsetIntroduced * 7) + 3) + constraint.length(), "</"+typedLetterStyle+">");
                offsetIntroduced++;
            }
            return Html.fromHtml(builder.toString());
        }
    }

    public boolean isSomeoneSelected() {
        return selected != null;
    }
}
