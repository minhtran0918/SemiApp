package org.semi.databases.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "store_type")
public class StoreType {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int mID;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    public StoreType(int mID, @NonNull String mName) {
        this.mID = mID;
        this.mName = mName;
    }

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
    }
}
