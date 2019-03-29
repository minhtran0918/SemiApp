package org.semi.object;

//Contract for database containing constants representing collection name, field name,...
public class DBContract {
    public static final String ID = "id";
    private DBContract() {}
    public static class Store {
        private Store() {}
        public static final String COLLECTION = "store";
        public static final String TITLE = "title";
        public static final String FULL_NAME = "fullName";
        public static final String DESCRIPTION = "description";
        public static final String IMAGE_URL = "imageURL";
        public static final String ADDRESS = "address";
        public static final String ADDRESS_COUNTRY = "country";
        public static final String ADDRESS_CITY = "city";
        public static final String ADDRESS_DISTRICT = "district";
        public static final String ADDRESS_TOWN = "town";
        public static final String ADDRESS_STREET = "street";
        public static final String TYPE = "type";
        public static final String UTILITIES = "utilities";
        public static final String START_END = "startEnd";
        public static final String GEO = "geo";
        public static final String GEO_LATITUDE = "_latitude";
        public static final String GEO_LONGITUDE = "_longitude";
        public static final String GRID_NUMBER = "gridNumber";
        public static final String RATING = "rating";
        public static final String CONTACT = "contact";
        public static final String NUM_PRODUCTS = "numProducts";
        public static final String NUM_COMMENTS = "numComments";
        public static final String NUM_POINTS = "numPoints";
    }

    public static class Product {
        private Product() {}
        public static final String COLLECTION = "product";
        public static final String TITLE = "title";
        public static final String FULL_NAME = "fullName";
        public static final String DESCRIPTION = "description";
        public static final String IMAGE_URL = "imageURL";
        public static final String COST = "cost";
        public static final String TYPE = "type";
        public static final String STORE_ID = "storeId";
    }

    public static class Comment {
        private Comment() { }
        public static final String STORE_ID = "storeId";
        public static final String COMMENT = "comment";
        public static final String TIME = "time";
        public static final String EDIT_TIME = "editTime";
        public static final String TIME_SEC = "_seconds";
        public static final String TIME_NANO = "_nanoseconds";
        public static final String USER_DISPLAY_NAME = "userDisplayName";
        public static final String USER_PHOTO_URL = "userPhotoURL";
        public static final String USER_RATING = "userRating";
    }
}
