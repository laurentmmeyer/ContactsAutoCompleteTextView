package com.meyerlaurent.cactv;

import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spanned;

import java.util.ArrayList;

/**
 * Created by laurentmeyer on 06/09/15.
 */
public class Utils {

    /**
     * It's maybe the most complicated function of this lib (yes, this lib has a trivial logic!)
     * It's just a loop which add to all occurrences of a string a <[letter]></[letter]> to be
     * displayed as bold or underlined by the #TextView (because rendered to HTML)
     *
     * @param constraint:                    The substring we want to highlight
     * @param nameOfPeopleWeWantToHighlight: The people object on which these modifications will be done.
     * @param typedLetterStyle:              Bold or italic
     * @return a highlighted TextView compatible string
     */
    public static Spanned setBold(CharSequence constraint, People nameOfPeopleWeWantToHighlight, String typedLetterStyle) {
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

    public static String typeToColumn(AutoCompleteContactTextView.TYPE_OF_DATA data) {
        switch (data) {
            case PHONE:
                return ContactsContract.CommonDataKinds.Phone.NUMBER;
            case EMAIL:
                return ContactsContract.CommonDataKinds.Email.DATA;
            default:
                return ContactsContract.CommonDataKinds.Phone.NUMBER;
        }
    }

    public static Uri[] typeToUri(AutoCompleteContactTextView.TYPE_OF_DATA data) {
        switch (data) {
            case PHONE:
                Uri[] phone = {ContactsContract.CommonDataKinds.Phone.CONTENT_URI};
                return phone;
            case EMAIL:
                Uri[] email = {ContactsContract.CommonDataKinds.Email.CONTENT_URI};
                return email;
            case BOTH:
                Uri[] both = {ContactsContract.CommonDataKinds.Phone.CONTENT_URI, ContactsContract.CommonDataKinds.Email.CONTENT_URI};
                return both;
            default:
                Uri[] defaultArray = {ContactsContract.CommonDataKinds.Phone.CONTENT_URI};
                return defaultArray;
        }
    }
}
