package org.semi.databases.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import org.semi.databases.model.AddressCity;

@Dao
public interface CityDAO {

    @Query("SELECT * FROM city ORDER BY name ASC")
    LiveData<List<AddressCity>> getListAllCities();

    @Query("SELECT * FROM city WHERE id = :id")
    AddressCity getCityById(int id);
}
