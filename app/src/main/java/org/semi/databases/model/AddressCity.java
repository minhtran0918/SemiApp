package org.semi.databases.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

//indices = {@Index(value = {"name"}, unique = true)
@Entity(tableName = "city")
public class AddressCity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "type")
    private String mType;

    public AddressCity(int id, String name, String type) {
        this.mId = id;
        if (name.contains("DD")) {
            this.mName = name.replace("DD", "Đ");
        } else {
            this.mName = name;
        }
        this.mType = type;
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    @NonNull
    public String getName() {
        return this.mName;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
    }

    @NonNull
    public String getType() {
        return this.mType;
    }

    public void setType(@NonNull String type) {
        this.mType = type;
    }

    @Override
    public String toString() {
        if (mType.contains("Thành phố")) {
            return "TP. " + mName;
        } else {
            return mName;
        }
    }

    public String toLiteName() {
        if (mType.contains("Thành phố")) {
            return "TP. " + mName;
        } else {
            return "T. " + mName;
        }
    }
}
