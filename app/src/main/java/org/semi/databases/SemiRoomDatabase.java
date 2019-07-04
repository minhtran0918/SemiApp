package org.semi.databases;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.semi.databases.DAO.CityDAO;
import org.semi.databases.DAO.DistrictDAO;
import org.semi.databases.DAO.WardDAO;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;

@Database(entities = {AddressCity.class, AddressDistrict.class, AddressWard.class},
        version = 1,
        exportSchema = false)
public abstract class SemiRoomDatabase extends RoomDatabase {
    //volatile: tránh lấy giá trị cũ (truy cập vào bộ nhớ lấy giá trị hiện tại)
    private static volatile SemiRoomDatabase sInstance;

    public static final String DB_NAME = "Semi.DB.Address.Lite.db";
    public static final int DB_VERSION = 1;

    public abstract CityDAO cityDAO();

    public abstract DistrictDAO districtDAO();

    public abstract WardDAO wardDAO();

    //public abstract StoreDAO storeDAO();

    public static SemiRoomDatabase getInstance(Context context) {

        //call method that check if database not exists and copy prepopulated file from assets
        if (sInstance == null) {
            //Tránh gọi database khi chưa tạo kịp
            synchronized (SemiRoomDatabase.class) {
                if (sInstance == null) {
                    //copyAttachedDatabase(context.getApplicationContext(), DB_NAME);
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            SemiRoomDatabase.class,
                            DB_NAME)
                            .openHelperFactory(new AssetSQLiteOpenHelperFactory())
                            // recreate the database if necessary
                            //readmore migration
                             .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return sInstance;
    }

    //Read database from assets folder without use lib
    private static void copyAttachedDatabase(Context context, String databaseName) {
        final File dbPath = context.getDatabasePath(databaseName);

        // If the database already exists, return
        if (dbPath.exists()) {
            Log.d("Semi", "db Path Exists");
            return;
        }

        // Make sure we have a path to the file
        dbPath.getParentFile().mkdirs();

        // Try to copy database file
        try {
            final InputStream inputStream = context.getAssets().open("databases/" + databaseName);
            final OutputStream output = new FileOutputStream(dbPath);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            inputStream.close();
        }
        catch (IOException e) {
            Log.d("Semi", "Failed to open file", e);
            e.printStackTrace();
        }
    }
}
