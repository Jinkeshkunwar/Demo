package com.example.contentprovider_app_activity;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class CompanyProvider extends ContentProvider {

    public static final String TAg=CompanyProvider.class.getName();

    static final String PROVIDER_NAME = "com.example.contentprovider_app_activity.CompanyProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/employees";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String NAME = "name";
    static final String JOBGRADE = "jobgrade";

    private static HashMap<String, String> EMPLOYEE_PROJECTION_MAP;

    static final int EMPLOYEE = 1;
    static final int EMPLOYEE_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "employees", EMPLOYEE);
        uriMatcher.addURI(PROVIDER_NAME, "employees/#", EMPLOYEE_ID);
    }

    /**
     * Database specific constant declarations
     */

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Company";
    static final String EMPLOYEES_TABLE_NAME = "employees";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + EMPLOYEES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " jobgrade TEXT NOT NULL);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAg," DatabaseHelper class created ");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);

            Log.d(TAg," Database onCreate() method executed  ");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  EMPLOYEES_TABLE_NAME);
            onCreate(db);
            Log.d(TAg," Database onUpgrade() method executed");
        }
    }

    @Override
    public boolean onCreate() {


        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();

        Log.d(TAg,"  onCreate() method executed return :::"+String.valueOf((db == null)? false:true));

        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        /**
         * Add a new student record
         */
        long rowID = db.insert(	EMPLOYEES_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            Log.d(TAg,"  insert() method executed ::::"+_uri);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,String[] selectionArgs,
                        String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(EMPLOYEES_TABLE_NAME);


        switch (uriMatcher.match(uri)) {
            case EMPLOYEE:
                qb.setProjectionMap(EMPLOYEE_PROJECTION_MAP);
                break;

            case EMPLOYEE_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = NAME;
        }
        else if (selection.equals(NAME)){

            Cursor c= qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);

        }

        Cursor c = qb.query(db, projection, null,
                null, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(TAg, " query() executed & return the cursor :::: " + c);

        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri)){
            case EMPLOYEE:
                count = db.delete(EMPLOYEES_TABLE_NAME, null, null);
                break;

            case EMPLOYEE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( EMPLOYEES_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? "  AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAg," Database delete() method executed   values ::::  "+count);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri)) {
            case EMPLOYEE:
                count = db.update(EMPLOYEES_TABLE_NAME, values, null ,null);
                break;

            case EMPLOYEE_ID:
                count = db.update(EMPLOYEES_TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAg," update() method executed::  "+count);

        return count;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case EMPLOYEE:
                Log.d(TAg," getType() method executed  & return  if EMPLOYEES  :::  vnd.android.cursor.dir/vnd.example.employees ");
                return "vnd.android.cursor.dir/vnd.example.employees";
            /**
             * Get a particular student
             */
            case EMPLOYEE_ID:
                Log.d(TAg," getType() method executed  & return   if EMPLOYEES_ID  ::: vnd.android.cursor.item/vnd.example.employees ");
                return "vnd.android.cursor.item/vnd.example.employees";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}