package com.meyerlaurent.cactv;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.SpannableStringBuilder;
import android.widget.BaseAdapter;
import android.widget.Filterable;

import java.util.ArrayList;

/**
 * Created by laurentmeyer on 11/04/15.
 */
public abstract class CustomAdapter extends BaseAdapter implements Filterable {

    ArrayList<People> phoneList = new ArrayList<>();

    CustomAdapter(Context context) {

        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null)

        {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneList.add(new People(new SpannableStringBuilder(name), new SpannableStringBuilder(phoneNumber)));
            }
            phones.close();
        }
    }
}
