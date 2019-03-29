package org.semi.contract;

import com.google.android.gms.location.LocationRequest;

import org.semi.R;

public final class Contract {
    public static final float[] RATING_LEVELS = {0, 2.5f, 5};
    public static final int[] RATING_COLORS =  {0xffdd0000, 0xff00aa00};
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
    private Contract() {}
}
