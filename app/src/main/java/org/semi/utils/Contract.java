package org.semi.utils;

import com.google.android.gms.location.LocationRequest;

import org.semi.BuildConfig;
import org.semi.R;

public final class Contract {

    public static final String FIREBASE_STORAGE_ROOT_BUCKET = "gs://semiapp-6d5bb.appspot.com/";
    //Get Location Key
    public static final String PERMISSION_LOCATION_GRANTED_KEY = "LocationPermissionGranted";
    //LocationToAddress Handler Bundle key
    public static final String LOCATION_TO_ADDRESS_KEY = "location_to_address";

    //ALL ADDRESS
    public static final int ALL_ADDRESS_CITY_DEFAULT = 79;
    public static final int ALL_ADDRESS_DISTRICT_DEFAULT = 762;
    public static final int ALL_ADDRESS_WARD_DEFAULT = 0;

    //
    public static final int MODE_MENU_HOME = 1111;
    public static final int MODE_MENU_DISCOVER = 2222;
    public static final int MODE_MENU_BOOKMARK = 3333;
    public static final int MODE_MENU_ME = 4444;

    //In menu Home,
    public static final int MODE_HOME_LOAD_STORE = 0;
    public static final int MODE_HOME_LOAD_STORE_TYPE_ALL = -1;
    public static final int MODE_LOAD_STORE_TYPE_STORE = 0;
    public static final int MODE_LOAD_STORE_TYPE_CONVENIENCE = 1;
    public static final int MODE_LOAD_STORE_TYPE_SUPER_MARKET = 2;
    public static final int MODE_LOAD_STORE_TYPE_MALL = 3;
    public static final int MODE_LOAD_STORE_TYPE_MARKET = 4;
    public static final int MODE_LOAD_STORE_TYPE_ATM = 5;
    public static final int MODE_LOAD_STORE_TYPE_PHARMACY = 6;
    public static final int MODE_LOAD_STORE_TYPE_ORTHER = 7;


    public static final int MODE_HOME_LOAD_PRODUCT = 1;
    //TODO: LOAD PRODUCT - SETUP TYPE VARIABLE
    public static final int MODE_LOAD_PRODUCT_TYPE_ = 0;

    //Menu home -> option select
    //Range
    public static final int MODE_LOAD_RANGE_AROUND = 0;
    public static final float MODE_LOAD_RANGE_AROUND_VALUE_DEFAULT = 0.5f;
    public static final int MODE_LOAD_RANGE_ALL = 1;
    //Sort
    public static final int MODE_LOAD_SORT_RANGE = 0;
    public static final int MODE_LOAD_SORT_POPULAR = 1;


    public static final float[] RATING_LEVELS = {0, 2.5f, 5};
    public static final int[] RATING_COLORS = {0xffdd0000, 0xff00aa00};
    public static final int[] UTILITY_RESOURCES = {
            R.drawable.utility_wifi_black,            //0 - wifi
            R.drawable.utility_deliver_black,         //1 - deliver
            R.drawable.utility_local_parking,
            R.drawable.utility_cool,
            R.drawable.utility_vip_register,
            R.drawable.utility_credit_card,
            R.drawable.utility_24_hour//2 - bike guard
    };
    //spinner position of Store and Product
    public static final int STORE_MODE = 0;
    public static final int PRODUCT_MODE = 1;
    public static final String BUNDLE_MODE_KEY = "mode";
    public static final String BUNDLE_SEARCH_HINT_KEY = "string";
    public static final String BUNDLE_ACTION_LOGO_KEY = "logo";
    public static final String BUNDLE_MODE_TYPE_KEY = "type";
    public static final String BUNDLE_STORE_KEY = "storeId";
    public static final String BUNDLE_PRODUCT_KEY = "productId";
    //currency
    public static final String VN_CURRENCY = "đ";
    //location
    public static final int LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static final int LOCATION_INTERVAL = 3000; //ms
    public static final int LOCATION_FASTEST_INTERVAL = 3000; //ms
    //box
    public static final double VISIBLE_BOX_MIN_DIMEN = 0.125; //km
    public static final int DIMEN_SCALE_FACTOR_MAX = 5;
    public static final double VISIBLE_BOX_MAX_DIMEN = VISIBLE_BOX_MIN_DIMEN * (1 << DIMEN_SCALE_FACTOR_MAX); //km
    public static final double HIDING_BOX_DIMEN = 16;
    public static final int HIDING_BOX_COLOR = 0x33000000;
    public static final int VISIBLE_BOX_PADDING = 100; //px
    //shared preferences
    public static final String SHARED_MY_STATE = "myState";
    public static final String SHARED_COUNTRY_KEY = "country";
    public static final String SHARED_CITY_KEY = "city";
    public static final String SHARED_DISTRICT_KEY = "district";
    public static final String SHARED_TOWN_KEY = "town";
    //Default Number of requested item in RecyclerView
    public static final int NUM_STORES_PER_REQUEST = 20;
    public static final int NUM_PRODUCTS_PER_REQUEST = 20;
    //new
    public static final String TAG = "Semi";

    //Add store features
    public static final int SELECT_CITY_ADDRESS_MODE = 1;
    public static final int SELECT_DISTRICT_ADDRESS_MODE = 2;
    public static final int SELECT_WARD_ADDRESS_MODE = 3;
    public static final String MODE_CHOOSE_ADDRESS_KEY = "mode_choose_address";
    public static final String CITY_ADDRESS_ID_KEY = "city_address_id";
    public static final String DISTRICT_ADDRESS_ID_KEY = "district_address_id";
    public static final String WARD_ADDRESS_ID_KEY = "ward_address_id";
    //location
    public static final String LAT_STORE_KEY = "lat_store";
    public static final String LNG_STORE_KEY = "lng_store";
    public static final String CHECK_HAVE_LOCATION = "check_have_location";
    public static final String ADDRESS_LOCATION_KEY = "address_location";
    //time
    public static final int TIME_PICKER_START = 1;
    public static final int TIME_PICKER_END = 2;
    //img
    public static final String IMAGE_DIRECTORY = "semi_gallery";
    public static final String CAPTURE_IMAGE_FILE_PROVIDER = BuildConfig.APPLICATION_ID + ".fileprovider";
    public static final String PATH_USER_CAPTURE_IMG = "user_capture_image/add_store";
    public static final String LIST_IMAGE_EXTRA = "add_store_select_image_list_extra";
    public static final String FIREBASE_PATH_VERIFY_STORE = "verify_store/";

    //Contructor
    private Contract() {
    }

}
