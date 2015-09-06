package com.meyerlaurent.cactv;

import android.telephony.PhoneNumberUtils;
import android.text.Spanned;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by laurentmeyer on 06/09/15.
 */
public abstract class ContactsFilter extends Filter {

    ArrayList<People> allPeople;
    boolean typedLettersShouldBeDifferent;
    String typedLetterStyle;

    public ContactsFilter(ArrayList<People> allPeople, boolean typedLettersShouldBeDifferent, String typedLetterStyle) {
        this.allPeople = allPeople;
        this.typedLettersShouldBeDifferent = typedLettersShouldBeDifferent;
        this.typedLetterStyle = typedLetterStyle;

    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults r = new FilterResults();
        if (constraint == null || constraint.length() == 0) {
            r.values = allPeople;
            r.count = (allPeople == null ? 0 : allPeople.size());
        } else {
            ArrayList<People> filtered = new ArrayList<>();
            for (People toFilter : allPeople) {
                String nameAnalysed = toFilter.getName().toString().toLowerCase();
                String constraintProperlyFormatted = constraint.toString().toLowerCase();
                if (nameAnalysed.contains(constraintProperlyFormatted)) {
                    if (typedLettersShouldBeDifferent) {
                        Spanned text = Utils.setBold(constraint, toFilter, typedLetterStyle);
                        toFilter.setName(text);
                    }
                    filtered.add(toFilter);
                }
                String data = toFilter.getData().toString();
                boolean isPhoneNumber = PhoneNumberUtils.isGlobalPhoneNumber(data);
                if (isPhoneNumber) {
                    if (toFilter.getData().toString().toLowerCase().startsWith(constraintProperlyFormatted)) {
                        filtered.add(toFilter);
                    }
                } else {
                    if (toFilter.getData().toString().toLowerCase().contains(constraintProperlyFormatted)) {
                        filtered.add(toFilter);
                    }
                }
                // TODO: Make it country code insensible
                // One approach but with limitations:
                /*
                    It does work when you type "0176", it looks for "176" which is contained in +49176....
                 */
                if (constraint.toString().startsWith("0") && !constraint.toString().startsWith("00")) {
                    if (toFilter.getData().toString().toLowerCase().contains(constraintProperlyFormatted.substring(1))) {
                        filtered.add(toFilter);
                    }
                }
            }
            r.values = filtered;
            r.count = filtered.size();
        }
        return r;
    }


    public void setTypedLettersShouldBeDifferent(boolean typedLettersShouldBeDifferent) {
        this.typedLettersShouldBeDifferent = typedLettersShouldBeDifferent;
    }

    public void setTypedLetterStyle(String typedLetterStyle) {
        this.typedLetterStyle = typedLetterStyle;
    }

    public void setAllPeople(ArrayList<People> allPeople) {
        this.allPeople = allPeople;
    }

}
