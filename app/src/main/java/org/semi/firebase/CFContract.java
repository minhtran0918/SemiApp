package org.semi.firebase;

public final class CFContract {
    //func1
    public static final class NearbyStores {
        public static final String NAME = "nearbyStores";
        public static final String CENTER_LATITUDE = "centerLat";
        public static final String CENTER_LONGITUDE = "centerLng";
        public static final String FROM = "from";
        public static final String STORE_TYPE = "storeType";
        public static final String SELECTED_FIELDS = "selectedFields";
        public static final String NUM_RESULTS = "numResults";
    }

    public static final class NearbyStoresByKeywords {
        public static final String NAME = "nearbyStoresByKeywords";
        public static final String CENTER_LATITUDE = "centerLat";
        public static final String CENTER_LONGITUDE = "centerLng";
        public static final String FROM = "from";
        public static final String RECT_DIMENSION = "dimen";
        public static final String STORE_TYPE = "storeType";
        public static final String KEYWORDS = "keywords";
        public static final String SELECTED_FIELDS = "selectedFields";
        public static final String NUM_RESULTS = "numResults";
    }

    public static final class StoresByKeywords {
        public static final String NAME = "storesByKeywords";
        public static final String KEYWORDS = "keywords";
        public static final String LAST_STORE_ID = "lastId";
        public static final String STORE_TYPE = "storeType";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String DISTRICT = "district";
        public static final String TOWN = "town";
        public static final String SELECTED_FIELDS = "selectedFields";
        public static final String NUM_RESULTS = "numResults";
    }

    public static final class ProductsByKeywords {
        public static final String NAME = "productsByKeywords";
        public static final String KEYWORDS = "keywords";
        public static final String LAST_PRODUCT_ID = "lastId";
        public static final String PRODUCT_TYPE = "productType";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String DISTRICT = "district";
        public static final String TOWN = "town";
        public static final String SELECTED_FIELDS = "selectedFields";
        public static final String NUM_RESULTS = "numResults";
    }

    public static final class ProductById {
        public static final String NAME = "productById";
        public static final String PRODUCT_ID = "productId";
    }

    public static final class StoreById {
        public static final String NAME = "storeById";
        public static final String STORE_ID = "storeId";
    }

    public static final class ProductsOfStore {
        public static final String NAME = "productsOfStore";
        public static final String STORE_ID = "storeId";
        public static final String LAST_PRODUCT_ID = "lastId";
        public static final String SELECTED_FIELDS = "selectedFields";
        public static final String NUM_RESULTS = "numResults";
    }

    public static final class NearbyProducts {
        public static final String NAME = "nearbyProducts";
        public static final String CENTER_LATITUDE = "centerLat";
        public static final String CENTER_LONGITUDE = "centerLng";
        public static final String FROM = "from";
        public static final String PRODUCT_TYPE = "productType";
        public static final String SELECTED_FIELDS = "selectedFields";
        public static final String NUM_RESULTS = "numResults";
    }

    public static final class NearbyStoresByProducts {
        public static final String NAME = "nearbyStoresByProducts";
        public static final String CENTER_LATITUDE = "centerLat";
        public static final String CENTER_LONGITUDE = "centerLng";
        public static final String FROM = "from";
        public static final String RECT_DIMENSION = "dimen";
        public static final String PRODUCT_TYPE = "productType";
        public static final String KEYWORDS = "keywords";
        public static final String SELECTED_FIELDS = "selectedFields";
        public static final String NUM_RESULTS = "numResults";
    }

    public static final class GetComments {
        public static final String NAME = "getComments";
        public static final String STORE_ID = "storeId";
        public static final String FROM_TIME_SEC= "fromTimeSec";
        public static final String FROM_TIME_NANO= "fromTimeNano";
    }

    public static final class PostComment {
        public static final String NAME = "postComment";
        public static final String STORE_ID = "storeId";
        public static final String COMMENT = "comment";
        public static final String USER_RATING = "userRating";
    }
}
