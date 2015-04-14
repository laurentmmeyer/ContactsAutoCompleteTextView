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

    private static final String[] PHOTO_ID_PROJECTION = new String[]{
            ContactsContract.Contacts.PHOTO_ID
    };

    private static final String[] PHOTO_BITMAP_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Photo.PHOTO
    };

    Context context;

    //TODO: Get the picture

    ArrayList<People> dataList = new ArrayList<>();

    CustomAdapter(Context context, String whatToGet) {
        this.context = context;
        // TODO: Make it changeable (need to look in the  API)
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null)

        {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String data = phones.getString(phones.getColumnIndex(whatToGet));
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                dataList.add(new People(new SpannableStringBuilder(name), new SpannableStringBuilder(data), loadContactPhoto(context.getContentResolver(), Long.parseLong(id))));
            }
            phones.close();
        }
    }

    CustomAdapter(Context context) {
        this(context, ContactsContract.CommonDataKinds.Phone.NUMBER);

    }
    public static Bitmap loadContactPhoto(ContentResolver cr, long  id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }
}