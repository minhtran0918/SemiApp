package org.semi.databases.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

//foreignKeys = @ForeignKey(entity = AddressCity.class, parentColumns = "id", childColumns = "city_id")
@Entity(tableName = "district",
        indices = {@Index(value = {"name"})})
public class AddressDistrict {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "type")
    private String mType;

    @NonNull
    @ColumnInfo(name = "city_id")
    private int mCityId;

    public AddressDistrict(int id, String name, String type, int cityId) {
        this.mId = id;
        this.mName = name;
        this.mType = type;
        this.mCityId = cityId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
    }

    @NonNull
    public String getType() {
        return mType;
    }

    public void setType(@NonNull String type) {
        this.mType = type;
    }

    public int getCityId() {
        return mCityId;
    }

    public void setCityId(int cityId) {
        this.mCityId = cityId;
    }

    @Override
    public String toString() {
        return mType + " " + mName;
    }

    public String toLiteName() {
        if (mType.contains("Thành phố")) {
            return "TP. " + mName;
        } else if (mType.contains("Quận")) {
            if (mName.length() < 10) {
                return "Quận " + mName;
            } else {
                return "Q. " + mName;
            }
        } else if (mType.contains("Huyện")) {
            return "H. " + mName;
        } else if (mType.contains("Thị xã")) {
            return "TX. " + mName;
        }
        return mName;
    }
}
