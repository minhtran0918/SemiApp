package org.semi.databases.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import org.semi.databases.model.AddressDistrict;

@Dao
public interface DistrictDAO {
    @Query("SELECT * FROM district ORDER BY name ASC")
    List<AddressDistrict> getListAllDistrict();

    @Query("SELECT * from district WHERE city_id = :city_id ORDER BY type DESC, name ASC")
    LiveData<List<AddressDistrict>> getListDistrictByCity(int city_id);

    @Query("SELECT * FROM district WHERE id = :district_id")
    AddressDistrict getDistrictById(int district_id);
}
