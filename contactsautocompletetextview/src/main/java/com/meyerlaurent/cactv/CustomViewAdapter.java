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
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by laurentmeyer on 06/09/15.
 * This class get the data and filters them.
 */
public abstract class CustomViewAdapter extends BaseAdapter implements Filterable {

    Context context;
    private ArrayList<People> allPeople;
    private boolean typedLetterAreDifferent;
    private String styleOfDifferentLetters;
    protected ArrayList<People> displayed = new ArrayList<>();

    Filter filter;

    public CustomViewAdapter(Context context, AutoCompleteContactTextView.TYPE_OF_DATA data, String styleOfDifferentLetters, boolean typedLetterAreDifferent, AsyncLoad load) {
        this.allPeople = new ArrayList<>();
        this.context = context;
        this.styleOfDifferentLetters = styleOfDifferentLetters;
        this.typedLetterAreDifferent = typedLetterAreDifferent;
        Handler mHandler = new CustomHandler(allPeople, load, this);
        SearchThread st = new SearchThread(Utils.typeToColumn(data), Utils.typeToUri(data), mHandler);
        st.start();
        Log.d("CustomViewAdapter", "displayed.size():" + displayed.size());
    }

    private static class CustomHandler extends Handler {
        ArrayList<People> people;
        AsyncLoad load;
        CustomViewAdapter current;

        private CustomHandler(ArrayList<People> people, AsyncLoad load, CustomViewAdapter current) {
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
                    Log.d("SearchThread", "list.size():" + list.size());
                }
            }
            Message m = new Message();
            m.obj = list;
            mHandler.handleMessage(m);
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


    @Override
    public Filter getFilter() {
        Log.d("CustomViewAdapter", "get filter called");
        if (filter == null) {
            Log.d("CustomViewAdapter", "filter is null");
            ArrayList<People> toBeAnalysed = new ArrayList<>();
            toBeAnalysed.addAll(allPeople);
            filter = new ContactsFilter(toBeAnalysed, typedLetterAreDifferent, styleOfDifferentLetters) {
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    displayed = (ArrayList<People>) results.values;
                    Log.d("CustomViewAdapter", "after search disp.size():" + displayed.size());
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        ArrayList<People> toBeAnalysed = new ArrayList<>();
        toBeAnalysed.addAll(allPeople);
        ((ContactsFilter)getFilter()).setAllPeople(toBeAnalysed);
    }



    public void setTypedLetterAreDifferent(boolean typedLetterAreDifferent) {
        this.typedLetterAreDifferent = typedLetterAreDifferent;
        ((ContactsFilter)getFilter()).setTypedLettersShouldBeDifferent(typedLetterAreDifferent);
    }

    public void setStyleOfDifferentLetters(String styleOfDifferentLetters) {
        this.styleOfDifferentLetters = styleOfDifferentLetters;
        ((ContactsFilter)getFilter()).setTypedLetterStyle(styleOfDifferentLetters);
    }
}
