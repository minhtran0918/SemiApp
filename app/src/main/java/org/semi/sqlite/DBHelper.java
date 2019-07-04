package org.semi.sqlite;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static org.semi.sqlite.SQLiteDBContract.*;

import org.semi.utils.MyApp;
import org.semi.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "MyDB";

    public DBHelper() {
        super(MyApp.getContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creatte table
        db.execSQL(Country.CREATE_STATEMENT);
        db.execSQL(City.CREATE_STATEMENT);
        db.execSQL(District.CREATE_STATEMENT);
        db.execSQL(Town.CREATE_STATEMENT);
        //add data
        Resources resources = MyApp.getContext().getResources();
        db.beginTransaction();
        insertFromRawFile(resources.openRawResource(R.raw.contries), db, Country.TABLE_NAME, Country.getInstance());
        insertFromRawFile(resources.openRawResource(R.raw.cities), db, City.TABLE_NAME, City.getInstance());
        insertFromRawFile(resources.openRawResource(R.raw.districts), db, District.TABLE_NAME, District.getInstance());
        insertFromRawFile(resources.openRawResource(R.raw.towns), db, Town.TABLE_NAME, Town.getInstance());
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        if(newVer > oldVer) {
            db.execSQL(Country.DELETE_STATEMENT);
            db.execSQL(City.DELETE_STATEMENT);
            db.execSQL(District.DELETE_STATEMENT);
            db.execSQL(Town.DELETE_STATEMENT);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVer, int newVer) {
        if(newVer < oldVer) {

        }
    }

    //insert data from CSV file
    private void insertFromRawFile(InputStream inp, SQLiteDatabase db, String tableName, IInsertion table) {
        try(BufferedReader bufReader = new BufferedReader(new InputStreamReader(inp))) {
            String line;
            while((line = bufReader.readLine()) != null) {
                String[] arr = line.split(",");
                db.insert(tableName, null, table.getContentFromArray(arr));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
