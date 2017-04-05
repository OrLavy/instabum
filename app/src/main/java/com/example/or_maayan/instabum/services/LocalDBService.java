package com.example.or_maayan.instabum.services;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.or_maayan.instabum.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by orlavy on 4/5/17.
 */

public class LocalDBService {
    private static final LocalDBService ourInstance = new LocalDBService();
    public static LocalDBService getInstance() {
        return ourInstance;
    }

    private static final String TAG = "LoaclDbService";

    private ImageCacheDbHelper imageCacheDbHelper;
    private Map<String,String> imageUriToFileLocationMap;

    private LocalDBService() {
        this.imageCacheDbHelper = new ImageCacheDbHelper(MainActivity.getAppContext());
        this.imageUriToFileLocationMap = new HashMap<>();

        this.readAllRecordsFromDb();
    }

    public void saveImageToFile(Bitmap imageBitmap, String imageUri){
        try {
            String fileName = "imageCache_" + imageUri.substring(imageUri.length() - 10, imageUri.length());
            File dir = MainActivity.getAppFilesDir();//Environment.getExternalStorageDirectory();
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,fileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            addPicureToGallery(imageFile);
            saveRecordToDb(new ImageCacheItem(imageUri,fileName ));
        } catch (FileNotFoundException e) {
            Log.e(TAG,"Error : ", e);
        } catch (IOException e) {
            Log.e(TAG,"Error : ", e);
        }
    }

    public Bitmap loadImageFromFile(String imageUri){
        Bitmap bitmap = null;
        String imageFileName = this.imageUriToFileLocationMap.get(imageUri);
        try {
            File dir = MainActivity.getAppFilesDir();//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d(TAG,"got image from cache: " + imageFileName);
        } catch (FileNotFoundException e) {
            Log.e(TAG,"Error : ", e);
        } catch (IOException e) {
            Log.e(TAG,"Error : ", e);
        }
        return bitmap;
    }

    public boolean hasCacheForImage(String imageUri){
        return this.imageUriToFileLocationMap.containsKey(imageUri);
    }

    private void addPicureToGallery(File imageFile){
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MainActivity.getAppContext().sendBroadcast(mediaScanIntent);
    }

    private void saveRecordToDb(ImageCacheItem imageCacheItem){
        // Gets the data repository in write mode
        SQLiteDatabase db = imageCacheDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.ImageCacheEntry.COLUMN_NAME_URI, imageCacheItem.imageURI);
        values.put(FeedReaderContract.ImageCacheEntry.COLUMN_NAME_FILE_LOCATION, imageCacheItem.fileLocation);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.ImageCacheEntry.TABLE_NAME, null, values);

        if (newRowId != -1){
            this.imageUriToFileLocationMap.put(imageCacheItem.imageURI, imageCacheItem.fileLocation);
        }
    }

    private void readAllRecordsFromDb(){
        SQLiteDatabase db = imageCacheDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FeedReaderContract.ImageCacheEntry._ID,
                FeedReaderContract.ImageCacheEntry.COLUMN_NAME_URI,
                FeedReaderContract.ImageCacheEntry.COLUMN_NAME_FILE_LOCATION
        };

        Cursor cursor = db.query(
                FeedReaderContract.ImageCacheEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.ImageCacheEntry._ID));
            String imageUri = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.ImageCacheEntry.COLUMN_NAME_URI));
            String fileLocation = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.ImageCacheEntry.COLUMN_NAME_FILE_LOCATION));

            imageUriToFileLocationMap.put(imageUri, fileLocation);
        }
        cursor.close();

    }

    ///
    /// Helper Models
    ///
    public static class ImageCacheItem {
        public final String imageURI;
        public final String fileLocation;

        public ImageCacheItem(String imageURI, String fileLocation){
            this.imageURI = imageURI;
            this.fileLocation = fileLocation;
        }
    }


    ///
    /// SQL FUNCTIONS
    ///

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.ImageCacheEntry.TABLE_NAME + " (" +
                    FeedReaderContract.ImageCacheEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.ImageCacheEntry.COLUMN_NAME_URI + " TEXT," +
                    FeedReaderContract.ImageCacheEntry.COLUMN_NAME_FILE_LOCATION + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.ImageCacheEntry.TABLE_NAME;

    public static final class FeedReaderContract {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private FeedReaderContract() {}

        /* Inner class that defines the table contents */
        public static class ImageCacheEntry implements BaseColumns {
            public static final String TABLE_NAME = "image_files";
            public static final String COLUMN_NAME_URI = "image_uri";
            public static final String COLUMN_NAME_FILE_LOCATION = "file_location";
        }
    }

    public class ImageCacheDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public ImageCacheDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
