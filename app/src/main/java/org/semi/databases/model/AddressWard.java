package org.semi.databases.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

//foreignKeys = @ForeignKey(entity = AddressDistrict.class, parentColumns = "id", childColumns = "district_id")
@Entity(tableName = "ward",
        indices = {@Index(value = {"name"})})
public class AddressWard {

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
    @ColumnInfo(name = "district_id")
    private int mDistrictId;

    public AddressWard(int id, String name, String type, int districtId) {
        this.mId = id;
        this.mName = name;
        this.mType = type;
        this.mDistrictId = districtId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String mName) {
        this.mName = mName;
    }

    @NonNull
    public String getType() {
        return mType;
    }

    public void setType(@NonNull String mType) {
        this.mType = mType;
    }

    public int getDistrictId() {
        return mDistrictId;
    }

    public void setDistrictId(int mDistrictId) {
        this.mDistrictId = mDistrictId;
    }

    @Override
    public String toString() {
        return mType + " " + mName;
    }

    public String toLiteName() {
        if (mType.contains("Phường")) {
            if (mName.length() < 10) {
                return "Phường " + mName;
            } else {
                return "P. " + mName;
            }
        } else if (mType.contains("Xã")) {
            return "Xã " + mName;
        } else if (mType.contains("Thị trấn")) {
            return "TT. " + mName;
        }/*else if (mType.contains("Không rõ")) {
            return mName;
        }*/
        return mName;
    }
}
