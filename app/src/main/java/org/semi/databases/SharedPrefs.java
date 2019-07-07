package org.semi.databases;

import android.content.Context;
import android.content.SharedPreferences;

import org.semi.object.StoreBookmark;
import org.semi.utils.Contract;
import org.semi.utils.MyApp;

import java.util.ArrayList;
import java.util.List;

public class SharedPrefs {
    private static final String PREFS_NAME = "my_data";
    private static SharedPrefs mInstance;
    private SharedPreferences mSharedPreferences;

    private static final String STORE_ID_KEY = "store_id";
    private static final String LIST_STORE_SAVE = "list_store_save";

    public static final String KEY_ALL_ADDRESS_CITY = "all_address_city";
    public static final String KEY_ALL_ADDRESS_DISTRICT = "all_address_district";
    public static final String KEY_ALL_ADDRESS_WARD = "store_or_product";

    public static final String KEY_OPTION_LOAD_STORE_OR_PRODUCT = "option_store_or_product";
    public static final String KEY_OPTION_CATEGORY_STORE = "option_category_store";
    public static final String KEY_OPTION_CATEGORY_PRODUCT = "option_category_product";
    public static final String KEY_OPTION_RANGE = "option_range";
    public static final String KEY_OPTION_RANGE_VALUE = "option_range_value";
    public static final String KEY_OPTION_SORT = "option_sort";

    private SharedPrefs() {
        mSharedPreferences = MyApp.getContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefs getInstance() {
        if (mInstance == null) {
            mInstance = new SharedPrefs();
        }
        return mInstance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> anonymousClass) {
        if (anonymousClass == String.class) {
            return (T) mSharedPreferences.getString(key, "");
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, false));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, 0));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, 0));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, 0));
        } else {
            return (T) MyApp.getInstance()
                    .getGSon()
                    .fromJson(mSharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> anonymousClass, T defaultValue) {
        if (anonymousClass == String.class) {
            return (T) mSharedPreferences.getString(key, (String) defaultValue);
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, (Boolean) defaultValue));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, (Float) defaultValue));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, (Integer) defaultValue));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, (Long) defaultValue));
        } else {
            return (T) MyApp.getInstance()
                    .getGSon()
                    .fromJson(mSharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    public <T> void put(String key, T data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        } else {
            editor.putString(key, MyApp.getInstance()
                    .getGSon()
                    .toJson(data));
        }
        editor.apply();
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void editLocationPermission(boolean state) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Contract.PERMISSION_LOCATION_GRANTED_KEY, state);
        editor.apply();
    }

    public boolean getPermissionLocation() {
        return mSharedPreferences.getBoolean(Contract.PERMISSION_LOCATION_GRANTED_KEY, false);
    }


    //Bookmark Store
    public List<String> getListSaveStore() {
        List<String> list = new ArrayList<>();
        String str_list = mSharedPreferences.getString(LIST_STORE_SAVE, "");
        if (str_list != "") {
            if (str_list.contains(",")) {
                String[] path = str_list.split(",");
                for (String item : path) {
                    list.add(item);
                }
            } else {
                list.add(str_list);
            }
        }
        return list;
    }

    public boolean isStoreSave(String store_id) {
        String data = get(store_id,String.class,"");

        if (data == "") {
            return false;
        } else {
            return true;
        }
    }

    public void saveStore(StoreBookmark storeBookmark) {
        StringBuilder sb = new StringBuilder();
        sb.append(mSharedPreferences.getString(LIST_STORE_SAVE, ""));
        if (sb.toString() == "") {
            sb.append(storeBookmark.getId());
        } else {
            sb.append(",").append(storeBookmark.getId());
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        //Chứa 1 list các id của cửa hàng -> nằm việc lặp lấy ra các cửa hàng bên bookmark
        //editor.putString(LIST_STORE_SAVE, sb.toString());
        put(LIST_STORE_SAVE,sb.toString());
        //Chứa object với key là id -> thuận tiện việc kiểm tra đã lưu chưa
        //editor.putString(storeBookmark.getId(), storeBookmark.toString());
        put(storeBookmark.getId(), storeBookmark);
    }

    public void removeStore(String store_id) {
        StringBuilder sb = new StringBuilder();
        String list_store = mSharedPreferences.getString(LIST_STORE_SAVE, "");
        if (list_store != "") {
            if (list_store.contains(",")) {
                String[] lists = list_store.split(",");
                for (String item : lists) {
                    if (!item.equals(store_id)) {
                        sb.append(item).append(",");
                    }
                }
                sb.setLength(sb.length() - 1);
            } else {
                if (list_store.equals(store_id)) {
                    sb.append("");
                } else {
                    sb.append(list_store);
                }
            }
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        //Chứa 1 list các id của cửa hàng -> nằm việc lặp lấy ra các cửa hàng bên bookmark
        editor.putString(LIST_STORE_SAVE, sb.toString());
        //Chứa object với key là id -> thuận tiện việc kiểm tra đã lưu chưa
        editor.remove(store_id).commit();
        editor.apply();
    }
}
