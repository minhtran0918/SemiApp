package org.semi.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.semi.object.City;
import org.semi.object.Country;
import org.semi.object.District;
import org.semi.object.Town;

import java.util.ArrayList;
import java.util.List;

public class AddressDBConnector {
    private static AddressDBConnector instance;
    private SQLiteDatabase readDB;
    private AddressDBConnector() {
        DBHelper helper = new DBHelper();
        readDB = helper.getReadableDatabase();
    }

    public static AddressDBConnector getInstance() {
        if(instance == null) {
            instance = new AddressDBConnector();
        }
        return instance;
    }

    public Country getCountry(int id) {
        String[] cols = {
                SQLiteDBContract.Country.NAME
        };
        String[] values = {
            String.valueOf(id)
        };
        try(Cursor cursor = readDB.query(SQLiteDBContract.Country.TABLE_NAME, cols,
                SQLiteDBContract.ID + "=?", values,null,null,null);) {
            if (cursor.moveToFirst()) {
                return new Country(id, cursor.getString(cursor.getColumnIndexOrThrow(cols[0])));
            } else {
                return null;
            }
        }
    }

    @NonNull
    public List<Country> getCountries() {
        String[] cols = {
                SQLiteDBContract.ID,
                SQLiteDBContract.Country.NAME
        };
        List<Country> countries = new ArrayList<>();
        try(Cursor cursor = readDB.query(SQLiteDBContract.Country.TABLE_NAME, cols,
                null, null,null,null,null);) {
            while(cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndexOrThrow(cols[0]);
                int nameIndex = cursor.getColumnIndexOrThrow(cols[1]);
                Country country = new Country(cursor.getInt(idIndex), cursor.getString(nameIndex));
                countries.add(country);
            }
        }
        return countries;
    }

    public City getCity(int id) {
        String[] cols = {
                SQLiteDBContract.City.NAME
        };
        String[] values = {
                String.valueOf(id)
        };
        try(Cursor cursor = readDB.query(SQLiteDBContract.City.TABLE_NAME, cols,
                SQLiteDBContract.ID + "=?", values,null,null,null)) {
            if (cursor.moveToFirst()) {
                return new City(id, cursor.getString(cursor.getColumnIndexOrThrow(cols[0])));
            } else {
                return null;
            }
        }
    }

    @NonNull
    public List<City> getCitiesFromCountry(int countryID) {
        String[] cols = {
                SQLiteDBContract.ID,
                SQLiteDBContract.City.NAME
        };
        String[] args = {
                String.valueOf(countryID)
        };
        List<City> countries = new ArrayList<>();
        try(Cursor cursor = readDB.query(SQLiteDBContract.City.TABLE_NAME, cols,
                SQLiteDBContract.City.COUNTRY_ID+"=?", args,null,null,null)) {
            while(cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndexOrThrow(cols[0]);
                int nameIndex = cursor.getColumnIndexOrThrow(cols[1]);
                City city = new City(cursor.getInt(idIndex), cursor.getString(nameIndex));
                countries.add(city);
            }
        }
        return countries;
    }

    public District getDistrict(int id) {
        String[] cols = {
                SQLiteDBContract.District.NAME
        };
        String[] values = {
                String.valueOf(id)
        };
        try(Cursor cursor = readDB.query(SQLiteDBContract.District.TABLE_NAME, cols,
                SQLiteDBContract.ID + "=?", values,null,null,null)) {
            if (cursor.moveToFirst()) {
                return new District(id, cursor.getString(cursor.getColumnIndexOrThrow(cols[0])));
            } else {
                return null;
            }
        }
    }

    @NonNull
    public List<District> getDistrictsFromCity(int cityID) {
        String[] cols = {
                SQLiteDBContract.ID,
                SQLiteDBContract.District.NAME
        };
        String[] args = {
                String.valueOf(cityID)
        };
        List<District> countries = new ArrayList<>();
        try(Cursor cursor = readDB.query(SQLiteDBContract.District.TABLE_NAME, cols,
                SQLiteDBContract.District.CITY_ID+"=?", args,null,null,null)) {
            while(cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndexOrThrow(cols[0]);
                int nameIndex = cursor.getColumnIndexOrThrow(cols[1]);
                District district = new District(cursor.getInt(idIndex), cursor.getString(nameIndex));
                countries.add(district);
            }
        }
        return countries;
    }

    public Town getTown(int id) {
        String[] cols = {
                SQLiteDBContract.Town.NAME
        };
        String[] values = {
                String.valueOf(id)
        };
        try(Cursor cursor = readDB.query(SQLiteDBContract.Town.TABLE_NAME, cols,
                SQLiteDBContract.ID + "=?", values,null,null,null)) {
            if (cursor.moveToFirst()) {
                return new Town(id, cursor.getString(cursor.getColumnIndexOrThrow(cols[0])));
            } else {
                return null;
            }
        }
    }

    @NonNull
    public List<Town> getTownsFromDistrict(int districtID) {
        String[] cols = {
                SQLiteDBContract.ID,
                SQLiteDBContract.Town.NAME
        };
        String[] args = {
                String.valueOf(districtID)
        };
        List<Town> countries = new ArrayList<>();
        try(Cursor cursor = readDB.query(SQLiteDBContract.Town.TABLE_NAME, cols,
                SQLiteDBContract.Town.DISTRICT_ID+"=?", args,null,null,null)) {
            while(cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndexOrThrow(cols[0]);
                int nameIndex = cursor.getColumnIndexOrThrow(cols[1]);
                Town town = new Town(cursor.getInt(idIndex), cursor.getString(nameIndex));
                countries.add(town);
            }
        }
        return countries;
    }
}
