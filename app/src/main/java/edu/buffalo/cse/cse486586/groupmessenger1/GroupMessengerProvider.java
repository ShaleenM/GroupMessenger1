package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko, shaleen
 *
 */
public class GroupMessengerProvider extends ContentProvider {
    private static final String TAG = GroupMessengerProvider.class.getName();
    //reference :: http://www.tutorialspoint.com/android/android_content_providers.htm

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Context context1 = getContext();

        Log.e(TAG, "!!!....Tag1....!!!");
        String filename = values.get("key").toString();
        String value = values.get("value").toString();

        Log.e(TAG, "filename :: "+ filename);
        Log.e(TAG, "message :: "+ value);

        FileOutputStream outputStream;

        try {
            outputStream = context1.openFileOutput(filename , context1.MODE_PRIVATE);
            outputStream.write(value.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);

        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Context context2= getContext();

        Log.e("Selection ::  ", selection);
        String value = null;
        String key = selection;
        //Reference : http://stackoverflow.com/questions/2786655/test-if-file-exists
        File file = context2.getFileStreamPath(selection);
        Log.e(TAG, "!!!...Tag4...!!!");
        if (file.exists()) {
            Log.e(TAG, "!!!...Tag5:: File Exists...!!!" + file.toString());
            FileReader fr;
            BufferedReader br;
            try
            {
                Log.e(TAG, "!!!...Tag6...!!!");
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                Log.e(TAG, "!!!...Tag7 :: BufferReader and Filereader in action Now...!!!");

                value = br.readLine();
                Log.e(TAG, "value = "+value);

            } catch (FileNotFoundException e) {
                Log.e(TAG, e.toString());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }

        //reference :: http://stackoverflow.com/questions/18290864/create-a-cursor-from-hardcoded-array-instead-of-db
        //          :: http://www.programcreek.com/java-api-examples/android.database.MatrixCursor
        String[] columns = new String[] { "key", "value"};
        MatrixCursor matrixCursor= new MatrixCursor(columns);

        matrixCursor.addRow(new Object[] { key, value});
        Log.e(TAG, "key and Value :: "+ key + value );
        matrixCursor.close();

        Log.v("query", selection);
        return matrixCursor;
    }
}