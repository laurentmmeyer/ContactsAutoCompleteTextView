package com.meyerlaurent.cactv;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Filterable;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by laurentmeyer on 11/04/15.
 */

/**
 * It's the simplest class possible: it just needs a context and returns the data you need with the people class
 * The default behaviour is to get the phone but you can change it by calling the
 */
public abstract class CustomAdapter extends BaseAdapter implements Filterable {

    Context context;
    ArrayList<People> dataList = new ArrayList<>();

    CustomAdapter(Context context, String whatToGet, Uri service) {
        this.context = context;
        Log.w("test", whatToGet+0+service.toString());
        // TODO: Make it changeable (need to look in the  API)
        Cursor cursor = context.getContentResolver().query(service, null, null, null, null);
        if (cursor != null)

        {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String data = cursor.getString(cursor.getColumnIndex(whatToGet));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                People toBeAdded = new People(new SpannableStringBuilder(name), new SpannableStringBuilder(data), loadContactPhoto(context.getContentResolver(), Long.parseLong(id)));
                if (!dataList.contains(toBeAdded)){
                    dataList.add(toBeAdded);
                }
            }
            cursor.close();
        }
    }

    CustomAdapter(Context context, AutoCompleteContactTextView.TYPE_OF_DATA data) {
        this(context, transformInt(data), transformDataUri(data));

    }

    private static String transformInt(AutoCompleteContactTextView.TYPE_OF_DATA data) {
        switch (data) {
            case PHONE:
                return ContactsContract.CommonDataKinds.Phone.NUMBER;
            case EMAIL:
                return ContactsContract.CommonDataKinds.Email.DATA;
            case PHYSICAL_ADDRESS:
                // TODO: Implement that
            default:
                return ContactsContract.CommonDataKinds.Phone.NUMBER;
        }
    }

    private static Uri transformDataUri(AutoCompleteContactTextView.TYPE_OF_DATA data) {
        switch (data) {
            case PHONE:
                return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            case PHYSICAL_ADDRESS:
                // TODO: Implement that
            case EMAIL:
                return ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            default:
                return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        }
    }

    public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }
}