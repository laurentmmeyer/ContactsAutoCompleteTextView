package com.meyerlaurent.cactv;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by laurentmeyer on 02/03/15.
 */
public class AutoCompleteContactTextView extends AutoCompleteTextView {
    Context context;
    int colorPhone;
    int colorName;
    boolean shouldBeBold;
    String returnPattern;
    People selected = null;
    boolean hasCustomAdapter = false;
    CustomAdapter adapter;

    public AutoCompleteContactTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoCompleteContactTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setCustomAdapter(CustomAdapter adapter){
        this.adapter = adapter;
        hasCustomAdapter = true;
        setAdapter(adapter);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        adapter = new ContactsAdapter(context);
        this.setAdapter(adapter);
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((ContactsAdapter) AutoCompleteContactTextView.this.getAdapter()).getFilter().filter(s);
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
        if (attrs != null){
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PhoneNumberAutoComplete);
            colorPhone = array.getColor(R.styleable.PhoneNumberAutoComplete_color_phone_numbers, getResources().getColor(android.R.color.holo_blue_dark));
            colorName = array.getColor(R.styleable.PhoneNumberAutoComplete_color_names, getResources().getColor(android.R.color.black));
            shouldBeBold = array.getBoolean(R.styleable.PhoneNumberAutoComplete_typed_letters_should_be_bold, false);
            returnPattern = TextUtils.isEmpty(array.getString(R.styleable.PhoneNumberAutoComplete_return_pattern))?"[Nn]: [P]":array.getString(R.styleable.PhoneNumberAutoComplete_return_pattern);
        }

    }

    public AutoCompleteContactTextView(Context context) {
        super(context);
        init(context, null);
        this.context = context;
    }

    @Override
    public Editable getText() {
        if (isSomeoneSelected()){
            String temp = returnPattern;
            String name = selected.getName().toString();
            String phone = selected.getNumber().toString();
            if (temp.contains("[Nn]")){
                boolean isAdapted = Character.isUpperCase(name.toCharArray()[0]);
                if (!isAdapted){
                    char[] array = name.toCharArray();
                    array[0] = Character.toUpperCase(array[0]);
                    name = String.valueOf(array);
                }
                temp.replace("[Nn]", name);
            }
            if (temp.contains("[N]")){
                temp.replace("[N]", name.toUpperCase());
            }
            if (temp.contains("[n]")){
                temp.replace("[n]", name.toLowerCase());
            }
            if (temp.contains("[p]")){
                temp.replace("[p]", phone);
            }
            return Editable.Factory.getInstance().newEditable(temp);
        }
        return super.getText();
    }

    private class ContactsAdapter extends CustomAdapter {
        ArrayList<People> toDisplayList = new ArrayList<>();
        TextView number;
        Filter filter;

        private ContactsAdapter(Context context) {
            super(context);
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
            View v = inflater.inflate(R.layout.fragment_send_auto_text, null);
            TextView name = (TextView) v.findViewById(R.id.fragment_send_auto_text_name);
            number = (TextView) v.findViewById(R.id.fragment_send_auto_text_number);
            name.setText(((People) getItem(position)).getName());
            name.setTextColor(colorName);
            number.setText(((People) getItem(position)).getNumber());
            number.setTextColor(colorPhone);
            return v;
        }

        @Override
        public Filter getFilter() {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults r = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        r.values = phoneList;
                        r.count = phoneList.size();
                    } else {
                        ArrayList<People> filtered = new ArrayList<>();
                        for (People toFilter : phoneList) {
                            if (toFilter.getName().toString().toLowerCase().contains(constraint.toString().toLowerCase()) || toFilter.getNumber().toString().contains(constraint)) {
                                if (shouldBeBold){
                                    // Get the index of the first concerned letter
                                    int nameFirst = toFilter.getName().toString().toLowerCase().indexOf(constraint.toString().toLowerCase().charAt(0));
                                    // The end
                                    int nameEnd = nameFirst + constraint.length();
                                    // Make it bold baby
                                    final SpannableStringBuilder str = new SpannableStringBuilder(toFilter.getName());
                                    str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), nameFirst, nameEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    toFilter.setName(str);
                                }
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
    }

    public boolean isSomeoneSelected(){
        return selected!=null;
    }
}
