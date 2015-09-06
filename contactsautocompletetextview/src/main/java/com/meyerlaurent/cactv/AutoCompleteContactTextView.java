package com.meyerlaurent.cactv;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

/**
 * Created by laurentmeyer on 02/03/15.
 */

/**
 * Main class of the lib
 */
public class AutoCompleteContactTextView extends AutoCompleteTextView implements AsyncLoad {
    Context context;
    int colorData;
    int colorName;
    boolean typedLettersShouldBeDifferent;
    String returnPattern;
    People selected = null;
    boolean hasCustomAdapter = false;
    CustomViewAdapter adapter;
    String typedLetterStyle;
    AttributeSet attrs;
    int xmlIntType = 0;

    @Override
    public void hasLoaded(CustomViewAdapter adapter) {
        setAdapter(adapter);
        Log.d("AutoCompleteContactText", "Has loaded");
    }

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
        ((CustomViewAdapter)getAdapter()).setStyleOfDifferentLetters(typedLetterStyle);
        ((CustomViewAdapter)getAdapter()).setTypedLetterAreDifferent(typedLettersShouldBeDifferent);
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
                    ((CustomViewAdapter) AutoCompleteContactTextView.this.getAdapter()).getFilter().filter(s);
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
                setText(((People) AutoCompleteContactTextView.this.getAdapter().getItem(position)).getName().toString());
                selected = (People) getAdapter().getItem(position);
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
        adapter = new DemoAdapter(context, type, typedLetterStyle, typedLettersShouldBeDifferent, this , colorData, colorName, displayPhoto);
        this.setAdapter(adapter);
    }

    /**
     * Should give the possibility to people to change the basic adapter but NOT TESTED!
     *
     * @param adapter: adapter to be set
     */
    // TODO: Try to see if it works
    public void setCustomAdapter(CustomViewAdapter adapter) {
        this.adapter = adapter;
        hasCustomAdapter = true;
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
