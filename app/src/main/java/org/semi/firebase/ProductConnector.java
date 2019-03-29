package org.semi.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.semi.object.Location;
import org.semi.object.Store;
import org.semi.sqlite.AddressDBConnector;
import org.semi.object.Address;
import org.semi.object.DBContract;
import org.semi.object.Product;
import org.semi.util.ObjectUtils;
import static org.semi.firebase.CFContract.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductConnector {
    private static ProductConnector instance;
    private ProductConnector() {

    }

    public void getNearbyProducts(Location location, int from, int numResults, int productType,
                                final IResult<List<Product>> IResult) {
        String[] selectedFields = {
                DBContract.Product.TITLE, DBContract.Product.IMAGE_URL, DBContract.Store.ADDRESS,
                DBContract.Store.GEO, DBContract.Product.COST, DBContract.Product.STORE_ID
        };
        //Cloud functions data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(NearbyProducts.CENTER_LATITUDE, location.getLatitude());
        data.put(NearbyProducts.CENTER_LONGITUDE, location.getLongitude());
        data.put(NearbyProducts.FROM, from);
        data.put(NearbyProducts.PRODUCT_TYPE, productType);
        data.put(NearbyProducts.SELECTED_FIELDS, Arrays.asList(selectedFields));
        data.put(NearbyProducts.NUM_RESULTS, numResults);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(NearbyProducts.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Product> products = new ArrayList<Product>();
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (mapList.size() > 0) {
                            for (Map map : mapList) {
                                /*GET id, title, address, imageURL, cost*/
                                //Init store
                                Store store = new Store();
                                Product product = new Product();
                                product.setId((String) map.get(DBContract.ID));
                                product.setTitle((String) map.get(DBContract.Product.TITLE));
                                product.setImageURL((String) map.get(DBContract.Product.IMAGE_URL));
                                store.setId((String) map.get(DBContract.Product.STORE_ID));
                                store.setAddress(getAddress(map));
                                store.setGeo(getGeo(map));
                                product.setStore(store);
                                //Error occurs when casting Integer to Long
                                product.setCost(((Number) map.get(DBContract.Product.COST)).longValue());
                                products.add(product);
                            }
                        }
                        IResult.onResult(products);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("my_error", e.getMessage());
                IResult.onFailure(e);
            }
        });
    }

    public void getProductById(String productId, final IResult<Product> IResult) {
        //Cloud function data
        Map<String, Object> data = new HashMap<>();
        data.put(ProductById.PRODUCT_ID, productId);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(CFContract.ProductById.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Map<String, Object> map = (Map<String, Object>) httpsCallableResult.getData();
                        if(map != null) {
                            Store store = new Store();
                            Product product = new Product();
                            product.setId((String) map.get(DBContract.ID));
                            product.setTitle((String) map.get(DBContract.Product.TITLE));
                            product.setName((String) map.get(DBContract.Product.FULL_NAME));
                            product.setDescription((String) map.get(DBContract.Product.DESCRIPTION));
                            product.setImageURL((String) map.get(DBContract.Product.IMAGE_URL));
                            product.setCost(((Number)map.get(DBContract.Product.COST)).longValue());
                            product.setType(
                                    ObjectUtils.getProductType(
                                            ((Number)map.get(DBContract.Product.TYPE)).intValue()
                                    ));
                            store.setId((String) map.get(DBContract.Product.STORE_ID));
                            store.setAddress(getAddress(map));
                            product.setStore(store);
                            IResult.onResult(product);
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

    public void getProductsOfStore(String storeId, String lastId, int numResults, final IResult<List<Product>> IResult) {
        String[] selectedFields = {
                DBContract.Product.TITLE, DBContract.Product.IMAGE_URL, DBContract.Product.COST
        };
        //Cloud function data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(ProductsOfStore.STORE_ID, storeId);
        data.put(ProductsOfStore.LAST_PRODUCT_ID, lastId);
        data.put(ProductsOfStore.SELECTED_FIELDS, Arrays.asList(selectedFields));
        data.put(ProductsOfStore.NUM_RESULTS, numResults);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(CFContract.ProductsOfStore.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Product> products = new ArrayList<Product>();
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (mapList.size() > 0) {
                            for (Map map : mapList) {
                                /*GET id, title, imageURL, cost*/
                                //Init store
                                Product product = new Product();
                                product.setId((String) map.get(DBContract.ID));
                                product.setTitle((String) map.get(DBContract.Product.TITLE));
                                product.setImageURL((String) map.get(DBContract.Product.IMAGE_URL));
                                //Error occurs when casting Integer to Long
                                product.setCost(((Number) map.get(DBContract.Product.COST)).longValue());
                                products.add(product);
                            }
                        }
                        IResult.onResult(products);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    public void getProductsByKeywords(int productType, String keywords, String lastId, int numResults,
                                      Object[] addressIds,
                                    final IResult<List<Product>> IResult) {
        String[] selectedFields = {
                DBContract.Product.TITLE, DBContract.Product.IMAGE_URL, DBContract.Store.ADDRESS,
                DBContract.Store.GEO, DBContract.Product.COST, DBContract.Product.STORE_ID
        };
        //Cloud function data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(ProductsByKeywords.PRODUCT_TYPE, productType);
        data.put(ProductsByKeywords.KEYWORDS, keywords);
        data.put(ProductsByKeywords.LAST_PRODUCT_ID, lastId);
        data.put(ProductsByKeywords.COUNTRY, addressIds[0]);
        data.put(ProductsByKeywords.CITY, addressIds[1]);
        data.put(ProductsByKeywords.DISTRICT, addressIds[2]);
        data.put(ProductsByKeywords.TOWN, addressIds[3]);
        data.put(ProductsByKeywords.SELECTED_FIELDS, Arrays.asList(selectedFields));
        data.put(ProductsByKeywords.NUM_RESULTS, numResults);
        //Call cloud function
        FirebaseFunctions.getInstance().getHttpsCallable(CFContract.ProductsByKeywords.NAME).call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Product> products = new ArrayList<Product>();
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) httpsCallableResult.getData();
                        if (mapList.size() > 0) {
                            for (Map map : mapList) {
                                /*GET id, title, address, imageURL, cost*/
                                //Init store
                                Store store = new Store();
                                Product product = new Product();
                                product.setId((String) map.get(DBContract.ID));
                                product.setTitle((String) map.get(DBContract.Product.TITLE));
                                product.setImageURL((String) map.get(DBContract.Product.IMAGE_URL));
                                store.setId((String) map.get(DBContract.Product.STORE_ID));
                                store.setAddress(getAddress(map));
                                store.setGeo(getGeo(map));
                                product.setStore(store);
                                //Error occurs when casting Integer to Long
                                product.setCost(((Number) map.get(DBContract.Product.COST)).longValue());
                                products.add(product);
                            }
                        }
                        IResult.onResult(products);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("my_error", e.getMessage());
                        IResult.onFailure(e);
                    }
                });
    }

    public static ProductConnector getInstance() {
        if(instance == null) {
            instance = new ProductConnector();
        }
        return instance;
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
}
