package org.semi.firebase;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.semi.sqlite.AddressDBConnector;
import org.semi.object.Address;
import org.semi.object.DBContract;
import org.semi.object.Location;
import org.semi.object.Store;
import org.semi.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.semi.firebase.CFContract.*;

public class StoreConnector {
    private static StoreConnector instance;
    private StoreConnector() { }

    public void getNearbyStores(Location location, int from, int numResults, int storeType,
                               final IResult<List<Store>> IResult) {
        String[] selectedFields = {
                DBContract.Store.TITLE, DBContract.Store.IMAGE_URL, DBContract.Store.ADDRESS,
                DBContract.Store.GEO, DBContract.Store.RATING
        };
        //Cloud functions data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(NearbyStores.CENTER_LATITUDE, location.getLatitude());
        data.put(NearbyStores.CENTER_LONGITUDE, location.getLongitude());
        data.put(NearbyStores.FROM, from);
        data.put(NearbyStores.STORE_TYPE, storeType);
        data.put(NearbyStores.SELECTED_FIELDS, Arrays.asList(selectedFields));
        data.put(NearbyStores.NUM_RESULTS, numResults);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(NearbyStores.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Store> stores = new ArrayList<Store>();
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (mapList.size() > 0) {
                            for (Map map : mapList) {
                                /*GET id, title, address, imageURL, rating, geo*/
                                //Init store
                                Store store = new Store();
                                store.setId((String) map.get(DBContract.ID));
                                store.setTitle((String) map.get(DBContract.Store.TITLE));
                                store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                                store.setAddress(getAddress(map));
                                //Error occurs when casting Integer to Float
                                store.setRating(((Number) map.get(DBContract.Store.RATING)).floatValue());
                                store.setGeo(getGeo(map));
                                stores.add(store);
                            }
                        }
                        IResult.onResult(stores);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    public void getNearbyStoresByKeywords(Location location,
                                          int from, int numResults, int storeType, String keywords, double dimen,
                                          final IResult<List<Store>> IResult) {
        String[] selectedFields = {
                DBContract.Store.TITLE, DBContract.Store.IMAGE_URL, DBContract.Store.ADDRESS,
                DBContract.Store.GEO, DBContract.Store.RATING
        };
        //Cloud function data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(NearbyStoresByKeywords.CENTER_LATITUDE, location.getLatitude());
        data.put(NearbyStoresByKeywords.CENTER_LONGITUDE, location.getLongitude());
        data.put(NearbyStoresByKeywords.FROM, from);
        data.put(NearbyStoresByKeywords.STORE_TYPE, storeType);
        data.put(NearbyStoresByKeywords.KEYWORDS, keywords);
        data.put(NearbyStoresByKeywords.RECT_DIMENSION, dimen);
        data.put(NearbyStoresByKeywords.SELECTED_FIELDS, Arrays.asList(selectedFields));
        data.put(NearbyStoresByKeywords.NUM_RESULTS, numResults);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(NearbyStoresByKeywords.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        /*GET id, title, address, imageURL, rating, geo*/
                        List<Store> stores = new ArrayList<Store>();
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (mapList.size() > 0) {
                            for (Map map : mapList) {
                                /*GET id, title, address, imageURL, rating, geo*/
                                //Init store
                                Store store = new Store();
                                store.setId((String) map.get(DBContract.ID));
                                store.setTitle((String) map.get(DBContract.Store.TITLE));
                                store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                                store.setAddress(getAddress(map));
                                //Error occurs when casting Integer to Float
                                store.setRating(((Number) map.get(DBContract.Store.RATING)).floatValue());
                                store.setGeo(getGeo(map));
                                stores.add(store);
                            }
                        }
                        IResult.onResult(stores);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    //get stores containing desired products
    public void getNearbyStoresByProducts(Location location,
                                          int from, int numResults, int productType, String keywords, double dimen,
                                          final IResult<List<Store>> IResult) {
        String[] selectedFields = {
                DBContract.Store.TITLE, DBContract.Store.IMAGE_URL, DBContract.Store.ADDRESS,
                DBContract.Store.GEO
        };
        //Cloud function data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(NearbyStoresByProducts.CENTER_LATITUDE, location.getLatitude());
        data.put(NearbyStoresByProducts.CENTER_LONGITUDE, location.getLongitude());
        data.put(NearbyStoresByProducts.FROM, from);
        data.put(NearbyStoresByProducts.PRODUCT_TYPE, productType);
        data.put(NearbyStoresByProducts.KEYWORDS, keywords);
        data.put(NearbyStoresByProducts.RECT_DIMENSION, dimen);
        data.put(NearbyStoresByProducts.SELECTED_FIELDS, Arrays.asList(selectedFields));
        data.put(NearbyStoresByProducts.NUM_RESULTS, numResults);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(NearbyStoresByProducts.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        /*GET id, title, address, imageURL, rating, geo*/
                        List<Store> stores = new ArrayList<Store>();
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (mapList.size() > 0) {
                            for (Map map : mapList) {
                                /*GET id, title, address, imageURL, rating, geo*/
                                //Init store
                                Store store = new Store();
                                store.setId((String) map.get(DBContract.ID));
                                store.setTitle((String) map.get(DBContract.Store.TITLE));
                                store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                                store.setAddress(getAddress(map));
                                //Error occurs when casting Integer to Float
                                store.setGeo(getGeo(map));
                                stores.add(store);
                            }
                        }
                        IResult.onResult(stores);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("my_error", e.getMessage());
                IResult.onFailure(e);
            }
        });
    }

    public void getStoresByKeywords(int storeType, String keywords, String lastId, int numResults,
                                    Object[] addressIds,
                                    final IResult<List<Store>> IResult) {
        Map<String, Object> data = new HashMap<String, Object>();
        String[] selectedFields = {
                DBContract.Store.TITLE, DBContract.Store.IMAGE_URL,
                DBContract.Store.ADDRESS, DBContract.Store.GEO, DBContract.Store.RATING
        };
        //Cloud function data
        data.put(StoresByKeywords.STORE_TYPE, storeType);
        data.put(StoresByKeywords.KEYWORDS, keywords);
        data.put(StoresByKeywords.LAST_STORE_ID, lastId);
        data.put(StoresByKeywords.COUNTRY, addressIds[0]);
        data.put(StoresByKeywords.CITY, addressIds[1]);
        data.put(StoresByKeywords.DISTRICT, addressIds[2]);
        data.put(StoresByKeywords.TOWN, addressIds[3]);
        data.put(StoresByKeywords.SELECTED_FIELDS, Arrays.asList(selectedFields));
        data.put(StoresByKeywords.NUM_RESULTS, numResults);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(StoresByKeywords.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Store> stores = new ArrayList<Store>();
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (mapList.size() > 0) {
                            for (Map map : mapList) {
                                /*GET id, title, address, imageURL, geo, rating*/
                                //Init store
                                Store store = new Store();
                                store.setId((String) map.get(DBContract.ID));
                                store.setTitle((String) map.get(DBContract.Store.TITLE));
                                store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                                store.setAddress(getAddress(map));
                                store.setGeo(getGeo(map));
                                //Error occurs when casting Integer to Float
                                store.setRating(((Number) map.get(DBContract.Store.RATING)).floatValue());
                                stores.add(store);
                            }
                        }
                        IResult.onResult(stores);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    public void getStoreById(String storeId, final IResult<Store> IResult) {
        //Cloud function data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(StoreById.STORE_ID, storeId);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(StoreById.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Map<String, Object> map = (Map<String, Object>) httpsCallableResult.getData();
                        if(map != null) {
                            //Easy to get those data
                            Store store = new Store();
                            store.setId((String) map.get(DBContract.ID));
                            store.setTitle((String) map.get(DBContract.Store.TITLE));
                            store.setName((String) map.get(DBContract.Store.FULL_NAME));
                            store.setImageURL((String) map.get(DBContract.Store.IMAGE_URL));
                            store.setDescription((String) map.get(DBContract.Store.DESCRIPTION));
                            store.setContact((String) map.get(DBContract.Store.CONTACT));
                            store.setRating(((Number)map.get(DBContract.Store.RATING)).floatValue());
                            store.setStartEnd((String)map.get(DBContract.Store.START_END));
                            store.setAddress(getAddress(map));
                            store.setType(
                                    ObjectUtils.getStoreType(
                                            ((Number)map.get(DBContract.Store.TYPE)).intValue()
                                            ));
                            store.setUtilities(getStoreUtilities(map));
                            store.setGeo(getGeo(map));
                            store.setNumProducts(((Number)map.get(DBContract.Store.NUM_PRODUCTS)).intValue());
                            store.setNumComments(((Number)map.get(DBContract.Store.NUM_COMMENTS)).intValue());
                            store.setNumPoints(((Number)map.get(DBContract.Store.NUM_POINTS)).intValue());
                            IResult.onResult(store);
                        } else {
                            IResult.onResult(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    private List<Store.Utility> getStoreUtilities(Map<String, Object> map) {
        List<Integer> ids = (List<Integer>)map.get(DBContract.Store.UTILITIES);
        List<Store.Utility> utilities = new ArrayList<>();
        for(int id : ids) {
            utilities.add(ObjectUtils.getStoreUtility(id));
        }
        return utilities;
    }

    private Address getAddress(Map<String, Object> map) {
        Map<String, Object> addressMap = (Map<String, Object>)map.get(DBContract.Store.ADDRESS);
        if(addressMap == null) {
            return null;
        }
        int countryId = (int)addressMap.get(DBContract.Store.ADDRESS_COUNTRY);
        int cityId = (int)addressMap.get(DBContract.Store.ADDRESS_CITY);
        int districtId = (int)addressMap.get(DBContract.Store.ADDRESS_DISTRICT);
        int townId = (int)addressMap.get(DBContract.Store.ADDRESS_TOWN);
        String street = (String)addressMap.get(DBContract.Store.ADDRESS_STREET);
        AddressDBConnector connector = AddressDBConnector.getInstance();
        Address address = new Address();
        address.setCountry(connector.getCountry(countryId));
        address.setCity(connector.getCity(cityId));
        address.setDistrict(connector.getDistrict(districtId));
        address.setTown(connector.getTown(townId));
        address.setStreet(street);
        return address;
    }

    private Location getGeo(Map<String, Object> map) {
        Map<String, Double> geoMap = (Map<String, Double>)map.get(DBContract.Store.GEO);
        if(geoMap == null) {
            return null;
        }
        double latitude = geoMap.get(DBContract.Store.GEO_LATITUDE);
        double longitude = geoMap.get(DBContract.Store.GEO_LONGITUDE);
        return new Location(latitude, longitude);
    }

    public static StoreConnector getInstance() {
        if(instance == null) {
            instance = new StoreConnector();
        }
        return instance;
    }
}
