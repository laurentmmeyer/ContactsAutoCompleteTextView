package com.meyerlaurent.cactv;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
 */
public abstract class CustomAdapter extends BaseAdapter implements Filterable {

    Context context;
    /*
    I'm not the biggest fan of initialize the variable directly but we need to have it to modify it in the handler
     */
    public ArrayList<People> dataList = new ArrayList<>();

    CustomAdapter(Context context, String whatToGet, Uri[] services, AsyncLoad load) {
        this.context = context;
        Handler mHandler = new CustomHandler(dataList, load, this);
        SearchThread st = new SearchThread(whatToGet, services, mHandler);
        st.start();
    }


    /**
     * Handler used to update UI once data are loaded
     */
    private static class CustomHandler extends Handler {
        ArrayList<People> people;
        AsyncLoad load;
        CustomAdapter current;

        private CustomHandler(ArrayList<People> people, AsyncLoad load, CustomAdapter current) {
            this.load = load;
            this.people = people;
            this.current = current;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            people.addAll((ArrayList<People>) msg.obj);
            load.hasLoaded(current);
        }
    }

    public CustomAdapter(Context context, AutoCompleteContactTextView.TYPE_OF_DATA data, AsyncLoad load) {
        this(context, transformInt(data), transformDataUri(data), load);

    }


    private static String transformInt(AutoCompleteContactTextView.TYPE_OF_DATA data) {
        switch (data) {
            case PHONE:
                return ContactsContract.CommonDataKinds.Phone.NUMBER;
            case EMAIL:
                return ContactsContract.CommonDataKinds.Email.DATA;
            default:
                return ContactsContract.CommonDataKinds.Phone.NUMBER;
        }
    }

    private static Uri [] transformDataUri(AutoCompleteContactTextView.TYPE_OF_DATA data) {
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

    public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }


    public static interface AsyncLoad {
        void hasLoaded(CustomAdapter adapter);
    }

    private class SearchThread extends Thread {
        String whatToGet;
        Uri[] services;
        Handler mHandler;

        ArrayList<People> list = new ArrayList<>();

        private SearchThread(String whatToGet, Uri[] services, Handler handler) {
            this.whatToGet = whatToGet;
            this.services = services;
            mHandler = handler;
        }

        @Override
        public void run() {
            Looper.prepare();
            for (Uri service : services) {
                Cursor cursor = context.getContentResolver().query(service, null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String data = cursor.getString(cursor.getColumnIndex(whatToGet));
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        People toBeAdded = new People(new SpannableStringBuilder(name), new SpannableStringBuilder(data), loadContactPhoto(context.getContentResolver(), Long.parseLong(id)));
                        if (!list.contains(toBeAdded)) {
                            list.add(toBeAdded);
                        }
                    }
                    cursor.close();
                }
            }
            Message m = new Message();
            m.obj = list;
            mHandler.handleMessage(m);
        }
    }
}