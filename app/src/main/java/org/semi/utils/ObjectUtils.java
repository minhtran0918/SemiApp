package org.semi.utils;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import org.semi.R;
import org.semi.firebase.IResult;
import org.semi.firebase.StorageConnector;
import org.semi.object.Location;
import org.semi.object.Product;
import org.semi.object.Store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

//getting stote types, store utilities, product utilites, address + geo from map, utilities,...
public final class ObjectUtils {
    private static List<Store.Type> storeTypes;
    private static List<Store.Utility> storeUtilities;
    private static List<Product.Type> productTypes;

    static {
        storeTypes = getNewStoreTypes();
        storeUtilities = getNewStoreUtilities();
        productTypes = getNewProductTypes();
    }

    /**
     * Get All store types from CSV file.
     *
     * @return A Map<id, name> of store types.
     */
    public static List<Store.Type> getNewStoreTypes() {
        List<Store.Type> types = new ArrayList<>();
        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                MyApp.getContext().getResources().openRawResource(R.raw.store_type)
        ))) {
            String line;
            int id = 0;
            while ((line = bufReader.readLine()) != null) {
                String[] arr = line.split(",");
                types.add(new Store.Type(id++, arr[1]));
            }
            Store.Type lastType = types.get(types.size() - 1);
            lastType.setId(-1);
            return types;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return types;
    }

    /**
     * Get all store utilities from CSV file.
     *
     * @return A Map<id, name> of store utilities
     */
    public static List<Store.Utility> getNewStoreUtilities() {
        List<Store.Utility> utilities = new ArrayList<>();
        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                MyApp.getContext().getResources().openRawResource(R.raw.utilities)
        ))) {
            String line;
            int id = 0;
            while ((line = bufReader.readLine()) != null) {
                String[] arr = line.split(",");
                utilities.add(new Store.Utility(id++, arr[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return utilities;
    }

    /**
     * Get new all product types from CSV file.
     *
     * @return A Map<id, name> of product types
     */
    public static List<Product.Type> getNewProductTypes() {
        List<Product.Type> types = new ArrayList<>();
        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                MyApp.getContext().getResources().openRawResource(R.raw.product_type)
        ))) {
            String line;
            int id = 0;
            while ((line = bufReader.readLine()) != null) {
                String[] arr = line.split(",");
                types.add(new Product.Type(id++, arr[1]));
            }
            Product.Type lastType = types.get(types.size() - 1);
            lastType.setId(-1);
            return types;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return types;
    }

    public static Store.Type getStoreType(int id) {
        if (storeTypes == null || storeTypes.size() == 0) {
            return null;
        }
        return storeTypes.get(id);
    }

    public static Store.Utility getStoreUtility(int id) {
        if (storeUtilities == null || storeUtilities.size() == 0) {
            return null;
        }
        return storeUtilities.get(id);
    }

    public static Product.Type getProductType(int id) {
        if (productTypes == null || productTypes.size() == 0) {
            return null;
        }
        return productTypes.get(id);
    }

    public static List<Store.Type> getStoreTypes() {
        return storeTypes;
    }

    /**
     * Get all store utilities.
     *
     * @return A Map<id, name> of store utilities
     */
    public static List<Store.Utility> getStoreUtilities() {
        return storeUtilities;
    }

    /**
     * Get all product types.
     *
     * @return A Map<id, name> of product types
     */
    public static List<Product.Type> getProductTypes() {
        return productTypes;
    }

    public static Location toMyLocation(android.location.Location location) {
        return new Location(location.getLatitude(), location.getLongitude());
    }

    public static void setBitmapToImage(final String storagePath, final AppCompatImageView imageView) {
        if (!storagePath.trim().equals("")) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    StorageConnector.getInstance().getBitmap(storagePath,
                            imageView.getWidth(), new IResult<Bitmap>() {
                                @Override
                                public void onResult(Bitmap result) {
                                    if (result != null) {
                                        imageView.setImageBitmap(result);
                                    }
                                }
                                @Override
                                public void onFailure(@NonNull Exception exp) {

                                }
                            });
                }
            };
            if (imageView.getWidth() > 0) {
                runnable.run();
            } else {
                imageView.post(runnable);
            }
        }
    }

    public static void setBitmapToImage(@NonNull final String storagePath, final AppCompatImageView imageView,
                                        @NonNull final LongBuffer lastCallIdHolder) {
        final long currentCallId = lastCallIdHolder.get(0) + 1;
        lastCallIdHolder.put(0, currentCallId);
        if (!storagePath.trim().equals("")) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    StorageConnector.getInstance().getBitmap(storagePath,
                            imageView.getWidth(), new IResult<Bitmap>() {
                                @Override
                                public void onResult(Bitmap result) {
                                    if (result != null && currentCallId == lastCallIdHolder.get(0)) {
                                        imageView.setImageBitmap(result);
                                    }
                                }
                                @Override
                                public void onFailure(@NonNull Exception exp) { }
                            });
                }
            };
            if (imageView.getWidth() > 0) {
                runnable.run();
            } else {
                imageView.post(runnable);
            }
        }
    }
}
