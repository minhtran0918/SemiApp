package org.semi.databases;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.semi.databases.DAO.CityDAO;
import org.semi.databases.DAO.DistrictDAO;
import org.semi.databases.DAO.WardDAO;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;

public class AddressRepository {

    private CityDAO mCityDAO;
    private DistrictDAO mDistrictDAO;
    private WardDAO mWardDAO;

    public AddressRepository(Application application) {
        SemiRoomDatabase mSemiRoomDatabase = SemiRoomDatabase.getInstance(application);
        mCityDAO = mSemiRoomDatabase.cityDAO();
        mDistrictDAO = mSemiRoomDatabase.districtDAO();
        mWardDAO = mSemiRoomDatabase.wardDAO();
    }

    public LiveData<List<AddressCity>> getAllCities() {
        return mCityDAO.getListAllCities();
    }

    public LiveData<List<AddressDistrict>> getListDistrictByID(int city_id) {
        return mDistrictDAO.getListDistrictByCity(city_id);
    }
    public LiveData<List<AddressWard>> getListWardByID(int district_id) {
        return mWardDAO.getListWardByDistrict(district_id);
    }

    public AddressCity getCityByID(int city_id){
        try {
            return new getNameCityAsyncTask().execute(city_id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public AddressDistrict getDistrictByID(int district_id){
        try {
            return new getNameDistrictAsyncTask().execute(district_id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public AddressWard getWardByID(int ward_id){
        try {
            return new getNameWardAsyncTask().execute(ward_id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*public List<AddressCity> getListCities() {
        try {
            if (!mListCities.isEmpty()) {
                mListCities.clear();
            }
            mListCities = new GetDataCityAsyncTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mListCities;
    }

    public List<AddressDistrict> getListDistrictByID(int city_id) {
        try {
            if (!mListDistrict.isEmpty()) {
                mListDistrict.clear();
            }
            mListDistrict = new GetDataDistrictAsyncTask().execute(city_id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mListDistrict;
    }

    public List<AddressWard> getListWardByID(int district_id) {
        try {
            if (!mListWard.isEmpty()) {
                mListWard.clear();
            }
            mListWard = new GetDataWardAsyncTask().execute(district_id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mListWard;
    }



    private class GetDataCityAsyncTask extends AsyncTask<Void, Void, List<AddressCity>> {
        @Override
        protected List<AddressCity> doInBackground(Void... voids) {
            return mCityDAO.getListAllCities();
        }
    }

    private class GetDataDistrictAsyncTask extends AsyncTask<Integer, Void, List<AddressDistrict>> {

        @Override
        protected List<AddressDistrict> doInBackground(Integer... integers) {
            return mDistrictDAO.getListDistrictByCity(integers[0]);
        }
    }

    private class GetDataWardAsyncTask extends AsyncTask<Integer, Void, List<AddressWard>> {

        @Override
        protected List<AddressWard> doInBackground(Integer... integers) {
            return mWardDAO.getListWardByDistrict(integers[0]);
        }
    }*/
    private class getNameCityAsyncTask extends AsyncTask<Integer,Void,AddressCity>{

        @Override
        protected AddressCity doInBackground(Integer... integers) {
            return mCityDAO.getCityById(integers[0]);
        }
    }
    private class getNameDistrictAsyncTask extends AsyncTask<Integer,Void,AddressDistrict>{

        @Override
        protected AddressDistrict doInBackground(Integer... integers) {
            return mDistrictDAO.getDistrictById(integers[0]);
        }
    }
    private class getNameWardAsyncTask extends AsyncTask<Integer,Void,AddressWard>{

        @Override
        protected AddressWard doInBackground(Integer... integers) {
            return mWardDAO.getWardById(integers[0]);
        }
    }
}
