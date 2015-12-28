package com.meyerlaurent.cactv;

import android.content.Context;
import android.content.res.TypedArray;
import android.telephony.PhoneNumberUtils;
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

/**
 * Main class of the lib
 */
public class AutoCompleteContactTextView extends AutoCompleteTextView implements CustomAdapter.AsyncLoad {
    Context context;
    int colorData;
    int colorName;
    boolean typedLettersShouldBeDifferent;
    String returnPattern;
    People selected = null;
    boolean hasCustomAdapter = false;
    CustomAdapter adapter;
    String typedLetterStyle;
    AttributeSet attrs;
    int xmlIntType = 0;

    /**
     * Choose which data you want to have
     */
    public enum TYPE_OF_DATA {
        PHONE, EMAIL, BOTH
    }

    public enum STYLE {NONE, BOLD, UNDERLINE}

    /**
     * Call it if you want to create this view programmatically
     *
     * @param s: Style you want to set refer to {@link com.meyerlaurent.cactv.AutoCompleteContactTextView.STYLE}
     */
    public void changeStyle(STYLE s) {
        switch (s) {
            case NONE:
                typedLettersShouldBeDifferent = false;
                break;
            case BOLD:
                typedLettersShouldBeDifferent = true;
                typedLetterStyle = "b";
                break;
            case UNDERLINE:
                typedLettersShouldBeDifferent = true;
                typedLetterStyle = "i";
                break;
        }
    }

    // Default is phone
    private TYPE_OF_DATA type = TYPE_OF_DATA.PHONE;

    boolean displayPhoto;

    public AutoCompleteContactTextView(Context context) {
        super(context);
        this.context = context;
        init(context, null, false);
    }

    public AutoCompleteContactTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, false);
    }

    public AutoCompleteContactTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, false);
    }

    private void init(Context context, AttributeSet attrs, boolean programmatic) {
        this.context = context;
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (AutoCompleteContactTextView.this.getAdapter() != null) {
                    ((CustomAdapter) AutoCompleteContactTextView.this.getAdapter()).getFilter().filter(s);
                }
                selected = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = (People) getAdapter().getItem(position);
                setText(selected.getName().toString());
            }
        });
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PhoneNumberAutoComplete);
            colorData = array.getColor(R.styleable.PhoneNumberAutoComplete_colorOfData, getResources().getColor(android.R.color.holo_blue_dark));
            colorName = array.getColor(R.styleable.PhoneNumberAutoComplete_colorOfNames, getResources().getColor(android.R.color.black));
            typedLettersShouldBeDifferent = array.getBoolean(R.styleable.PhoneNumberAutoComplete_typedLettersHaveDifferentStyle, false);
            if (typedLettersShouldBeDifferent) {
                typedLetterStyle = array.getInt(R.styleable.PhoneNumberAutoComplete_styleOfTypedLetters, 2) == 1 ? "u" : "b";
            }
            if (!programmatic) {
                xmlIntType = array.getInt(R.styleable.PhoneNumberAutoComplete_typeOfData, 1);
                switch (xmlIntType) {
                    case 1:
                        type = TYPE_OF_DATA.PHONE;
                        break;
                    case 2:
                        type = TYPE_OF_DATA.EMAIL;
                        break;
                    case 3:
                        type = TYPE_OF_DATA.BOTH;
                        break;
                }
            }
            returnPattern = TextUtils.isEmpty(array.getString(R.styleable.PhoneNumberAutoComplete_getTextPattern)) ? "[Nn]: [P]" : array.getString(R.styleable.PhoneNumberAutoComplete_getTextPattern);
            displayPhoto = array.getBoolean(R.styleable.PhoneNumberAutoComplete_displayPhotoIfAvailable, false);
            array.recycle();
        }
        adapter = new ContactsAdapter(context, type);
        this.setAdapter(adapter);
    }

    /**
     * Should give the possibility to people to change the basic adapter but NOT TESTED!
     *
     * @param adapter: adapter to be set
     */
    // TODO: Try to see if it works
    public void setCustomAdapter(CustomAdapter adapter) {
        this.adapter = adapter;
        hasCustomAdapter = true;
        setAdapter(adapter);
    }

    @Override
    public void hasLoaded(CustomAdapter adapter) {
        setAdapter(adapter);
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
                temp = temp.replace("[Nn]", name);
            }
            if (temp.contains("[N]")) {
                temp = temp.replace("[N]", name.toUpperCase());
            }
            if (temp.contains("[n]")) {
                temp = temp.replace("[n]", name.toLowerCase());
            }
            if (temp.contains("[d]")) {
                temp = temp.replace("[d]", data);
            }
            return Editable.Factory.getInstance().newEditable(temp);
        }
        return super.getText();
    }

    public String getName() {
        if (isSomeoneSelected()) {
            return selected.getName().toString();
        }
        return null;
    }

    public String getData() {
        if (isSomeoneSelected()) {
            return selected.getData().toString();
        }
        return null;
    }

    /**
     * Example of a possible implementation of the {@link CustomAdapter}
     */
    private class ContactsAdapter extends CustomAdapter {

        ArrayList<People> toDisplayList = new ArrayList<>();
        Filter filter;

        private ContactsAdapter(Context context) {
            super(context, type, AutoCompleteContactTextView.this);
        }

        public ContactsAdapter(Context context, TYPE_OF_DATA type) {
            super(context, type, AutoCompleteContactTextView.this);
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

        @Override
        public Filter getFilter() {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults r = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        r.values = dataList;
                        r.count = (dataList == null ? 0 : dataList.size());
                    } else {
                        ArrayList<People> filtered = new ArrayList<>();
                        for (People toFilter : dataList) {
                            if (toFilter.getName().toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                if (typedLettersShouldBeDifferent) {
                                    Spanned text = setBold(constraint, toFilter);
                                    toFilter.setName(text);
                                }
                                filtered.add(toFilter);
                            }
                            String data = toFilter.getData().toString();
                            boolean isPhoneNumber = PhoneNumberUtils.isGlobalPhoneNumber(data);
                            if (isPhoneNumber) {
                                String phone = toFilter.getData().toString().toLowerCase().replaceAll("\\D", "");
                                if (phone.indexOf(constraint.toString().toLowerCase()) != -1) {
                                    filtered.add(toFilter);
                                }
                            } else {
                                if (toFilter.getData().toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                    filtered.add(toFilter);
                                }
                            }
                            // TODO: Make it country code insensible
                            // One approach but with limitations:
                            /*
                                It does work when you type "0176", it looks for "176" which is contained in +49176....
                             */
                            if (constraint.toString().startsWith("0") && !constraint.toString().startsWith("00")) {
                                if (toFilter.getData().toString().toLowerCase().contains(constraint.toString().toLowerCase().substring(1))) {
                                    filtered.add(toFilter);
                                }
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

        /**
         * It's maybe the most complicated function of this lib (yes, this lib has a trivial logic!)
         * It's just a loop which add to all occurrences of a string a <[letter]></[letter]> to be
         * displayed as bold or underlined by the #TextView (because rendered to HTML)
         *
         * @param constraint:                    The substring we want to highlight
         * @param nameOfPeopleWeWantToHighlight: The people object on which these modifications will be done.
         * @return a highlighted TextView compatible string
         */
        private Spanned setBold(CharSequence constraint, People nameOfPeopleWeWantToHighlight) {
            String temp = nameOfPeopleWeWantToHighlight.getName().toString().replace("<" + typedLetterStyle + ">", "").replace("</" + typedLetterStyle + ">", "");
            ArrayList<Integer> positions = new ArrayList<>();
            for (int i = -1; (i = temp.toLowerCase().indexOf(constraint.toString().toLowerCase(), i + 1)) != -1; ) {
                positions.add(i);
            }
            StringBuilder builder = new StringBuilder(temp);
            int offsetIntroduced = 0;
            for (Integer position : positions) {
                builder.insert(position + (offsetIntroduced * 7), "<" + typedLetterStyle + ">");
                builder.insert(position + ((offsetIntroduced * 7) + 3) + constraint.length(), "</" + typedLetterStyle + ">");
                offsetIntroduced++;
            }
            return Html.fromHtml(builder.toString());
        }

    }

    public TYPE_OF_DATA getType() {
        return type;
    }

    public void setType(TYPE_OF_DATA type) {
        this.type = type;
        clear();
        init(context, attrs, true);
    }

    private void clear() {
        setText("");
    }

    /**
     * Used just to see what the getText should return.
     *
     * @return if someone is selected or not
     */
    public boolean isSomeoneSelected() {
        return selected != null;
    }
}
