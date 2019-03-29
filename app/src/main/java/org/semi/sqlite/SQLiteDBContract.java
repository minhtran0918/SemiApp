package org.semi.sqlite;

import android.content.ContentValues;

public class SQLiteDBContract {
    private SQLiteDBContract() { }
    public static final String ID = "_id";

    public static class Country implements IInsertion {
        private static Country instance;
        private Country(){}
        public static final String TABLE_NAME = "country";
        public static final String NAME = "name";
        public static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT NOT NULL" +
                ")";
        public static final String DELETE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

        @Override
        public ContentValues getContentFromArray(String[] arr) {
            ContentValues values = new ContentValues();
            values.put(ID, arr[0]);
            values.put(NAME, arr[1]);
            return values;
        }
        public static Country getInstance() {
            if(instance == null) {
                instance = new Country();
            }
            return instance;
        }
    }

    public static class City implements IInsertion {
        private static City instance;
        private City() {}
        public static final String TABLE_NAME = "city";
        public static final String NAME = "name";
        public static final String COUNTRY_ID = "countryID";
        public static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT NOT NULL," +
                COUNTRY_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY("+ COUNTRY_ID +") REFERENCES " + Country.TABLE_NAME + "(" + ID + ") " +
                "ON DELETE CASCADE" +
                ")";
        public static final String DELETE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

        @Override
        public ContentValues getContentFromArray(String[] arr) {
            ContentValues values = new ContentValues();
            values.put(COUNTRY_ID, arr[0]);
            values.put(ID, arr[1]);
            values.put(NAME, arr[2]);
            return values;
        }
        public static City getInstance() {
            if(instance == null) {
                instance = new City();
            }
            return instance;
        }
    }

    public static class District implements IInsertion{
        private static District instance;
        private District() {}
        public static final String TABLE_NAME = "district";
        public static final String NAME = "name";
        public static final String CITY_ID = "cityID";
        public static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT NOT NULL," +
                CITY_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY(" + CITY_ID + ") REFERENCES " + City.TABLE_NAME + "(" + ID + ") " +
                "ON DELETE CASCADE" +
                ")";
        public static final String DELETE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

        @Override
        public ContentValues getContentFromArray(String[] arr) {
            ContentValues values = new ContentValues();
            values.put(CITY_ID, arr[0]);
            values.put(ID, arr[1]);
            values.put(NAME, arr[2]);
            return values;
        }
        public static District getInstance() {
            if(instance == null) {
                instance = new District();
            }
            return instance;
        }
    }

    public static class Town implements IInsertion {
        private static Town instance;
        private Town() {}
        public static final String TABLE_NAME = "town";
        public static final String NAME = "name";
        public static final String DISTRICT_ID = "districtID";
        public static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT NOT NULL," +
                DISTRICT_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY(" + DISTRICT_ID + ") REFERENCES " + District.TABLE_NAME + "(" + ID + ") " +
                "ON DELETE CASCADE" +
                ")";
        public static final String DELETE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

        @Override
        public ContentValues getContentFromArray(String[] arr) {
            ContentValues values = new ContentValues();
            values.put(DISTRICT_ID, arr[0]);
            values.put(ID, arr[1]);
            values.put(NAME, arr[2]);
            return values;
        }
        public static Town getInstance() {
            if(instance == null) {
                instance = new Town();
            }
            return instance;
        }
    }
}
