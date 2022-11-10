package com.example.contentprovider_app_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG=MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate() method started");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart() method call  ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume() method call ");
    }

    public void doSaveContent(View view) {
        /**
         * Add the student data
         */
        Log.d(TAG,"Save button pressed");
        ContentValues values= new ContentValues();
        values.put(CompanyProvider.NAME,
                ((EditText)findViewById(R.id.editname)).getText().toString());
        values.put(CompanyProvider.JOBGRADE,
                ((EditText)findViewById(R.id.editjobname)).getText().toString());

        Uri uri = getContentResolver().insert(
                CompanyProvider.CONTENT_URI, values);
        Toast.makeText(getBaseContext(),
                uri.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG,"uri is ::"+uri.toString());

    }


    @SuppressLint("Range")
    public void doLoadContent(View view) {
        /**
         * Retrieve the StudentData
         *
         */
        Log.d(TAG,"Load button pressed");
        String URL = "content://com.example.contentprovider_app_activity.CompanyProvider";
        String selection= CompanyProvider.NAME+((EditText)findViewById(R.id.editname)).getText().toString()+"LIKE ?";
        String [] selectionArgs={"name"};
        Uri employees = Uri.parse(URL);


        Cursor c =getContentResolver().query(employees,null,selection,selectionArgs,"name");

        if (c.moveToFirst()) {
            Toast.makeText(this,
                    c.getString(c.getColumnIndex(CompanyProvider._ID)) +
                            ", " +  c.getString(c.getColumnIndex(CompanyProvider.NAME)) +
                            ", " + c.getString(c.getColumnIndex(CompanyProvider.JOBGRADE)),
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Retrieve Student Data"+c.getString(c.getColumnIndex(CompanyProvider._ID)) +
                    ", " +  c.getString(c.getColumnIndex(CompanyProvider.NAME)) +
                    ", " + c.getString(c.getColumnIndex(CompanyProvider.JOBGRADE)));

        }
    }

    public void doupdateContent(View v){
        Log.d(TAG,"update employees button pressed");

        ContentValues updateValues= new ContentValues();
        String selectionClause = CompanyProvider.NAME+((EditText)findViewById(R.id.editname)).getText().toString()+  " LIKE ?";
        String[] selectionArgs = {"name"};
        int rowupdate=0;
        updateValues.put(CompanyProvider.NAME, ((EditText)findViewById(R.id.editname)).getText().toString());
        updateValues.put(CompanyProvider.JOBGRADE,((EditText)findViewById(R.id.editjobname)).getText().toString());
        rowupdate = getContentResolver().update(CompanyProvider.CONTENT_URI,updateValues,selectionClause,selectionArgs);
        Toast.makeText(this, String.valueOf(rowupdate), Toast.LENGTH_SHORT).show();
        Log.d(TAG,"number of rows updated ::"+ rowupdate);
    }

    public void dodeleteContent(View v){
        Log.d(TAG,"delete employees button pressed");
        String selectionClause = CompanyProvider.NAME + ((EditText)findViewById(R.id.editname)).getText().toString()+ " LIKE ?";
        String[] selectionArgs = {"name"};
        int rowdelete=0;
        rowdelete = getContentResolver().delete(CompanyProvider.CONTENT_URI,selectionClause,selectionArgs);

        Toast.makeText(this,String.valueOf(rowdelete),Toast.LENGTH_LONG).show();
        Log.d(TAG,"number of rows in table  deteled ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause() method called ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop()method called ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()method called ");
    }
}