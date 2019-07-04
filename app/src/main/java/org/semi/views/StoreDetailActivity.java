package org.semi.views;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.semi.Comment;
import org.semi.databases.SharedPrefs;
import org.semi.firebase.GlideApp;
import org.semi.firebase.StorageConnector;
import org.semi.object.StoreBookmark;
import org.semi.utils.Contract;
import org.semi.R;
import org.semi.adapter.OnItemClickListener;
import org.semi.adapter.StoreDetailCommentAdapter;
import org.semi.adapter.StoreDetailProductAdapter;
import org.semi.firebase.IResult;
import org.semi.firebase.ProductConnector;
import org.semi.firebase.StoreConnector;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.utils.DialogUtils;
import org.semi.utils.LocationUtils;
import org.semi.utils.MathUtils;
import org.semi.utils.ObjectUtils;
import org.semi.utils.SignInUtils;
import org.semi.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreDetailActivity extends AppCompatActivity implements OnItemClickListener<Product> {
    private static final int NUM_PRODUCTS_PER_REQUEST = 6;
    private Toolbar toolbar_store_detail;
    private TextView toolbar_store_detail_name;
    private TextView store_detail_name, store_detail_address, store_detail_type, store_detail_state, store_detail_time,
            store_detail_distance, store_detail_description, store_detail_total_product, store_detail_total_comment,
            store_detail_total_rating;
    private Button btn_save;
    private ImageView mImgIcCategory;
    private AppCompatImageView storeImage;
    private LinearLayout store_detail_utilities;
    private StoreDetailProductAdapter mProductAdapter;
    private StoreDetailCommentAdapter mCommentAdapter;
    private Dialog mDialogUtility;
    private Store mStore;
    private Product product;
    private long lastCallId;
    private int RC_SIGN_IN = 1234;

    private String mStoreID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        getStoreAndProductIdFromIntent();
        initView();
        getStoreData();
        getProducts();
    }

    private void getStoreAndProductIdFromIntent() {
        final Intent intent = getIntent();
        mStoreID = intent.getStringExtra(Contract.BUNDLE_STORE_KEY);
        final String productId = intent.getStringExtra(Contract.BUNDLE_PRODUCT_KEY);
        mStore = new Store();
        mStore.setId(mStoreID);
        if (productId != null) {
            product = new Product();
            product.setId(productId);
        }
    }

    private void initView() {
        //Action bar
        toolbar_store_detail = findViewById(R.id.toolbar_store_detail);
        toolbar_store_detail.setTitle("");
        setSupportActionBar(toolbar_store_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //4 Button Direct
        btn_save = findViewById(R.id.btn_save);
        if (SharedPrefs.getInstance().isStoreSave(mStoreID)) {
            //Đã lưu
            btn_save.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_store_detail_save_24, 0, 0);
            btn_save.setText("Đã lưu");
        } else {
            //Chưa lưu
            btn_save.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_store_detail_unsave_24, 0, 0);
            btn_save.setText("Lưu lại");
        }
        //normal view
        toolbar_store_detail_name = findViewById(R.id.toolbar_store_detail_name);
        mImgIcCategory = findViewById(R.id.store_detail_ic_category);
        store_detail_type = findViewById(R.id.store_detail_type);
        store_detail_name = findViewById(R.id.store_detail_name);
        store_detail_address = findViewById(R.id.store_detail_address);
        store_detail_total_product = findViewById(R.id.store_detail_total_product);
        store_detail_total_comment = findViewById(R.id.store_detail_total_comment);
        store_detail_total_rating = findViewById(R.id.store_detail_total_rating);
        store_detail_state = findViewById(R.id.store_detail_state);
        store_detail_time = findViewById(R.id.store_detail_time);
        store_detail_distance = findViewById(R.id.store_detail_distance);
        store_detail_description = findViewById(R.id.store_detail_description);
        store_detail_utilities = findViewById(R.id.store_detail_utilities);
        storeImage = findViewById(R.id.img_store);
        //RecyclerView product
        final RecyclerView mRecyclerProduct = findViewById(R.id.store_detail_list_product);
        mRecyclerProduct.setLayoutManager(new GridLayoutManager(this, 3));
        mProductAdapter = new StoreDetailProductAdapter(this, new ArrayList<Product>());
        mProductAdapter.setOnItemClickListener(this);
        mRecyclerProduct.setAdapter(mProductAdapter);

        //region init list comment
        List<Comment> mListComment = new ArrayList<>();
        mListComment.add(new Comment("1", "Minh", R.drawable.ic_home_around_me, "comment 1 đây", 5, "01/12/2018 23:22"));
        mListComment.add(new Comment("2", "Nam", R.drawable.ic_default, "comment 2 đây", 4, "02/12/2018 23:22"));
        mListComment.add(new Comment("1", "Minh", R.drawable.ic_home_around_me, "comment 1 đây", 2, "01/12/2018 23:22"));
        if (mListComment.size() == 0) {
            /*Toast.makeText(this, "a", Toast.LENGTH_SHORT).show();
            img_error_list = findViewById(R.id.store_detail_error_list);
            img_error_list.setImageResource(R.drawable.ic_blank);*/
            TextView txt_error_comment = findViewById(R.id.txt_error_comment);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 20, 10, 20);
            txt_error_comment.setLayoutParams(params);

            txt_error_comment.setText(R.string.store_detail_error_comment_mess);
            txt_error_comment.setTextSize(getResources().getDimension(R.dimen.store_detail_error_list_text_size));
            txt_error_comment.setLines(2);
        } else {
            RecyclerView mRecyclerComment = findViewById(R.id.store_detail_list_comment);
            mRecyclerComment.setLayoutManager(new LinearLayoutManager(this));
            mCommentAdapter = new StoreDetailCommentAdapter(this, mListComment);
            mRecyclerComment.setAdapter(mCommentAdapter);
        }
        //endregion

    }

    private void setError() {
        final TextView txt_error_list = findViewById(R.id.txt_error_list);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 20, 10, 20);
        txt_error_list.setLayoutParams(params);
        txt_error_list.setText(R.string.store_detail_error_list_mess);
        txt_error_list.setTextSize(getResources().getDimension(R.dimen.store_detail_error_list_text_size));
        txt_error_list.setLines(2);
    }

    private void getStoreData() {
        StoreConnector connector = StoreConnector.getInstance();
        connector.getStoreById(mStore.getId(), new IResult<Store>() {
            @Override
            public void onResult(Store result) {
                if (result != null) {
                    mStore = result;
                    putDataToView();
                    closeLoading();
                } else {
                    DialogUtils.showInfoDialog(StoreDetailActivity.this, "Không thể tải dữ liệu");
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Exception exp) {
                DialogUtils.showInfoDialog(StoreDetailActivity.this, "Không thể tải dữ liệu");
                finish();
            }
        });
    }

    private void closeLoading() {
        findViewById(R.id.loadingTextView).setVisibility(View.GONE);
    }

    private void openLoading() {
        findViewById(R.id.loadingTextView).setVisibility(View.VISIBLE);
    }

    public void putDataToView() {
        toolbar_store_detail_name.setText(mStore.getTitle());
        store_detail_name.setText(mStore.getName());
        store_detail_address.setText(mStore.getAddress().toString());
        //Set category
        switch (mStore.getType().getId()) {
            case 0:
                mImgIcCategory.setImageResource(R.drawable.all_ic_menu_store_40);
                break;
            case 1:
                mImgIcCategory.setImageResource(R.drawable.all_ic_menu_convenience_40);
                break;
            case 2:
                mImgIcCategory.setImageResource(R.drawable.all_ic_menu_super_market_40);
                break;
            case 3:
                mImgIcCategory.setImageResource(R.drawable.ic_home_mall);
                break;
            case 4:
                mImgIcCategory.setImageResource(R.drawable.all_ic_menu_market_40);
                break;
            case 5:
                mImgIcCategory.setImageResource(R.drawable.all_ic_menu_atm_40);
                break;
            case 6:
                mImgIcCategory.setImageResource(R.drawable.all_ic_menu_pharmacy_40);
                break;
            default:
                mImgIcCategory.setImageResource(R.drawable.all_ic_menu_store_40);
                break;
        }
        store_detail_type.setText(String.format(" - %s", mStore.getType().getName()));
        store_detail_time.setText(mStore.getStartEnd());
        store_detail_description.setText(mStore.getDescription());
        store_detail_total_rating.setText(String.format("%.1f", mStore.getRating()));
        store_detail_total_product.setText(String.valueOf(mStore.getNumProducts()));
        store_detail_total_comment.setText(String.valueOf(mStore.getNumComments()));
        //set color for opened or closed
        if (isOpened(mStore.getStartEnd())) {
            store_detail_state.setText(getResources().getString(R.string.store_detail_state_open));
            store_detail_state.setTextColor(getResources().getColor(R.color.material_green_500));
        } else {
            store_detail_state.setText(getResources().getString(R.string.store_detail_state_close));
            store_detail_state.setTextColor(getResources().getColor(R.color.material_red_a200));
        }
        //set color for rating value
        float rating = mStore.getRating();
        int loopLimit = Contract.RATING_LEVELS.length - 1;
        for (int i = 0; i < loopLimit; i++) {
            if (Contract.RATING_LEVELS[i] <= rating && Contract.RATING_LEVELS[i + 1] > rating) {
                store_detail_total_rating.setTextColor(Contract.RATING_COLORS[i]);
            }
        }
        //set distance
        LocationUtils.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double distance = MathUtils.haversine(ObjectUtils.toMyLocation(location),
                            mStore.getGeo());
                    store_detail_distance.setText(StringUtils.toDistanceFormat(distance));
                } else {
                    store_detail_distance.setText("");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        //set utilities
        final List<Store.Utility> utilities = mStore.getUtilities();
        final int numberOfUtilities = utilities.size();
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        for (int i = 0; i < numberOfUtilities; i++) {
            ImageView img_utility = new ImageView(this);
            img_utility.setImageResource(Contract.UTILITY_RESOURCES[utilities.get(i).getId()]);
            img_utility.setLayoutParams(lp);
            store_detail_utilities.addView(img_utility);
        }
        //set store image
        //ObjectUtils.setBitmapToImage(mStore.getImageURL(), storeImage);
        if (!mStore.getImageURL().equals("")) {
            List<String> a = StringUtils.getListPath(mStore.getImageURL());
            String path_img = a.get(0);
            GlideApp.with(this)
                    .load(StorageConnector.getReference(path_img))
                    .centerCrop()
                    .error(R.drawable.all_background_error_load)
                    .into(storeImage);
        } else {
            storeImage.setImageResource(R.drawable.all_background_error_load);
        }
    }

    private boolean isOpened(String startEndStr) {
        final String[] array = startEndStr.split("-");
        final String startStr = array[0];
        final String endStr = array[1];
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        final String nowStr = formatter.format(Calendar.getInstance().getTime());
        try {
            final Date now = formatter.parse(nowStr);
            final Date startTime = formatter.parse(startStr);
            final Date endTime = formatter.parse(endStr);
            return now.after(startTime) && now.before(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void getProducts() {
        if (product != null) {
            getProductsWhenHavePreferred();
        } else {
            getProductsWhenNoPreferred();
        }
    }

    private void getProductsWhenNoPreferred() {
        final long currentCallId = ++lastCallId;
        ProductConnector.getInstance().getProductsOfStore(mStore.getId(),
                mProductAdapter.getLastProductId(), NUM_PRODUCTS_PER_REQUEST,
                new IResult<List<org.semi.object.Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        if (currentCallId != lastCallId || result.size() == 0) {
                            return;
                        }
                        mProductAdapter.addData(result);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        setError();
                    }
                });
    }

    private void getProductsWhenHavePreferred() {
        if (mProductAdapter.getItemCount() == 0) {
            getPreferredProduct();
            return;
        }
        String lastProductId = mProductAdapter.getLastProductId();
        if (lastProductId.equals(product.getId())) {
            lastProductId = "";
        }
        final long currentCallId = ++lastCallId;
        ProductConnector.getInstance().getProductsOfStore(mStore.getId(),
                lastProductId, NUM_PRODUCTS_PER_REQUEST,
                new IResult<List<org.semi.object.Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        if (currentCallId != lastCallId || result.size() == 0) {
                            return;
                        }
                        removePreferredProduct(result);
                        mProductAdapter.addData(result);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        setError();
                    }
                });
    }

    private void removePreferredProduct(List<Product> products) {
        int productIndex = Collections.binarySearch(products, product, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        if (productIndex >= 0) {
            products.remove(productIndex);
        }
    }

    private void getPreferredProduct() {
        final long currentCallId = ++lastCallId;
        ProductConnector.getInstance().getProductById(product.getId(), new IResult<Product>() {
            @Override
            public void onResult(Product result) {
                if (result == null || currentCallId != lastCallId) {
                    return;
                }
                product = result;
                mProductAdapter.addData(result);
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadMoreProduct(View view) {
        getProducts();
    }

    public void hideAllProduct(View view) {
        mProductAdapter.clear();
    }

    public void actionDirectToStore(View view) {
        //open google map (can be app or browser)
        Uri url = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + mStore.getGeo());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void actionSaveStore(View view) {
        if (SharedPrefs.getInstance().isStoreSave(mStoreID)) {
            //Đã lưu -> bỏ lưu
            SharedPrefs.getInstance().removeStore(mStoreID);
            DialogUtils.showSnackBar(this, null, "Bỏ lưu cửa hàng");
            btn_save.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_store_detail_unsave_24, 0, 0);
            btn_save.setText("Lưu lại");
        } else {
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormatter.format(Calendar.getInstance().getTime());
            StoreBookmark store = new StoreBookmark(
                    mStoreID,
                    mStore.getName(),
                    mStore.getAddress().toString(),
                    mStore.getImageURL(),
                    date);
            //Chưa lưu -> lưu lại
            SharedPrefs.getInstance().saveStore(store);
            DialogUtils.showSnackBar(this, null, "Đã lưu cửa hàng");
            btn_save.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_store_detail_save_24, 0, 0);
            btn_save.setText("Đã lưu");
        }
    }

    public void actionCommentToStore(View view) {
        //Check Login
        if (!SignInUtils.userAuthenticated()) {
            startActivityForResult(SignInUtils.intentCallLogin(), RC_SIGN_IN);
        } else {
            launchCommentActivity();
        }
    }

    public void actionShareStore(View view) {
        //Uri imageUri = Uri.parse("https://www.naturehills.com/media/catalog/product/n/o/norway-spruce-tree-1-600x600.jpg");
        String str_mess = store_detail_name.getText() + "\n" + store_detail_address.getText() + ".";
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, str_mess);
        shareIntent.setType("text/plain");
        //shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        //shareIntent.setType("image/*");
        //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.store_detail_product_share_mess)));
    }

    public void actionShowMap(View view) {
        final Intent mapIntent = new Intent(this, StoreMapActivity.class);
        mapIntent.putExtra(Contract.BUNDLE_STORE_KEY, mStore);
        startActivity(mapIntent);
    }

    public void loadMoreUtility(View view) {
        List<Store.Utility> utilities = mStore.getUtilities();
        if (utilities.size() == 0) {
            return;
        }
        if (mDialogUtility == null) {
            mDialogUtility = new Dialog(this);
        }
        mDialogUtility.setContentView(R.layout.store_detail_dialog_utility);
        ListView list_utility = mDialogUtility.findViewById(R.id.store_detail_list_utility);
        ArrayAdapter<Store.Utility> arrayAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, utilities);
        list_utility.setAdapter(arrayAdapter);
        Button btn_close_dialog_product = mDialogUtility.findViewById(R.id.store_detail_btn_close_utility);
        btn_close_dialog_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogUtility.dismiss();
            }
        });
        mDialogUtility.show();
    }

    public void loadMoreComments(View view) {

    }

    public void actionContactToStore(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                mStore.getContact(), null)));
    }

    @Override
    public void onItemClick(final Product product) {
        ProductConnector.getInstance().getProductById(product.getId(), new IResult<Product>() {
            @Override
            public void onResult(Product result) {
                if (result == null) {
                    return;
                }
                showProductInfo(result);
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
            }
        });

    }

    private void showProductInfo(Product product) {
        final Dialog dialog = new Dialog(StoreDetailActivity.this);
        dialog.setContentView(R.layout.store_detail_dialog_product);
        AppCompatImageView img_product = dialog.findViewById(R.id.store_detail_product_img_dialog);
        TextView txt_product_name = dialog.findViewById(R.id.store_detail_product_name);
        TextView txt_product_type = dialog.findViewById(R.id.store_detail_product_type);
        TextView txt_product_price = dialog.findViewById(R.id.store_detail_product_price);
        TextView txt_product_description = dialog.findViewById(R.id.store_detail_product_description);
        Button btn_close_dialog_product = dialog.findViewById(R.id.store_detail_btn_close_product);
        txt_product_name.setText(product.getTitle());
        txt_product_type.setText(product.getType().getName());
        txt_product_price.setText(StringUtils.toVNDCurrency(product.getCost()));
        txt_product_description.setText(product.getDescription());
        ObjectUtils.setBitmapToImage(product.getImageURL(), img_product);
        btn_close_dialog_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                launchCommentActivity();
                return;
            }
            if (response == null) {
                // User pressed back button
                Toast.makeText(this, "Xin vui lòng thực hiện đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "Kết nối có vấn đề, xin vui lòng kiểm tra internet của bạn", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(Contract.TAG, "Sign-in error: ", response.getError());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void launchCommentActivity() {
        Intent commentIntent = new Intent(this, CommentActivity.class);
        commentIntent.putExtra(Contract.BUNDLE_STORE_KEY, mStore);
        startActivity(commentIntent);
    }
}
