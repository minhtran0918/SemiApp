package org.semi.databases.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import org.semi.databases.model.AddressWard;

@Dao
public interface WardDAO {

    @Query("SELECT * from ward WHERE district_id = :district_id UNION SELECT * FROM ward WHERE id = 0 ORDER BY type, name ASC")
    LiveData<List<AddressWard>> getListWardByDistrict(Integer district_id);
    @Query("SELECT * from ward WHERE id = :ward_id")
    AddressWard getWardById(int ward_id);
}
