package org.semi.minh;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.semi.R;
import org.semi.StoreMapActivity;
import org.semi.contract.Contract;
import org.semi.custom.OnItemClickListener;
import org.semi.custom.SignInDialog;
import org.semi.firebase.IResult;
import org.semi.firebase.ProductConnector;
import org.semi.firebase.StoreConnector;
import org.semi.minh.object.Comment;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.util.DialogUtils;
import org.semi.util.LocationUtils;
import org.semi.util.MathUtils;
import org.semi.util.ObjectUtils;
import org.semi.util.SignInUtils;
import org.semi.util.StringUtils;

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
    private AppCompatImageView storeImage;
    private LinearLayout store_detail_utilities;
    private StoreDetailProductAdapter mProductAdapter;
    private StoreDetailCommentAdapter mCommentAdapter;
    private Dialog mDialogUtility;
    private Store store;
    private Product product;
    private long lastCallId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minh_activity_store_detail);
        initView();
        getStoreAndProductIdFromIntent();
        getStoreData();
        getProducts();
    }

    private void getStoreAndProductIdFromIntent() {
        final Intent intent = getIntent();
        final String storeId = intent.getStringExtra(Contract.BUNDLE_STORE_KEY);
        final String productId = intent.getStringExtra(Contract.BUNDLE_PRODUCT_KEY);
        store = new Store();
        store.setId(storeId);
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
        //normal view
        toolbar_store_detail_name = findViewById(R.id.toolbar_store_detail_name);
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
        mListComment.add(new Comment("1", "Minh", R.drawable.minh_ic_home_around_me, "comment 1 đây", 5, "01/12/2018 23:22"));
        mListComment.add(new Comment("2", "Nam", R.drawable.ic_default, "comment 2 đây", 4, "02/12/2018 23:22"));
        mListComment.add(new Comment("1", "Minh", R.drawable.minh_ic_home_around_me, "comment 1 đây", 2, "01/12/2018 23:22"));
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
        connector.getStoreById(store.getId(), new IResult<Store>() {
            @Override
            public void onResult(Store result) {
                if (result != null) {
                    store = result;
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

    private void putDataToView() {
        toolbar_store_detail_name.setText(store.getName());
        store_detail_name.setText(store.getName());
        store_detail_address.setText(store.getAddress().toString());
        store_detail_type.setText(String.format(" - %s", store.getType().getName()));
        store_detail_time.setText(store.getStartEnd());
        store_detail_description.setText(store.getDescription());
        store_detail_total_rating.setText(String.format("%.1f", store.getRating()));
        store_detail_total_product.setText(String.valueOf(store.getNumProducts()));
        store_detail_total_comment.setText(String.valueOf(store.getNumComments()));
        //set color for opened or closed
        if (isOpened(store.getStartEnd())) {
            store_detail_state.setText(getResources().getString(R.string.store_detail_state_open));
            store_detail_state.setTextColor(getResources().getColor(R.color.material_green_500));
        } else {
            store_detail_state.setText(getResources().getString(R.string.store_detail_state_close));
            store_detail_state.setTextColor(getResources().getColor(R.color.material_red_a200));
        }
        //set color for rating value
        float rating = store.getRating();
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
                            store.getGeo());
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
        final List<Store.Utility> utilities = store.getUtilities();
        final int numberOfUtilities = utilities.size();
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        for (int i = 0; i < numberOfUtilities; i++) {
            ImageView img_utility = new ImageView(this);
            img_utility.setImageResource(Contract.UTILITY_RESOURCES[utilities.get(i).getId()]);
            img_utility.setLayoutParams(lp);
            store_detail_utilities.addView(img_utility);
        }
        //set store image
        ObjectUtils.setBitmapToImage(store.getImageURL(), storeImage);
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
        if(product != null) {
            getProductsWhenHavePreferred();
        } else {
            getProductsWhenNoPreferred();
        }
    }

    private void getProductsWhenNoPreferred() {
        final long currentCallId = ++lastCallId;
        ProductConnector.getInstance().getProductsOfStore(store.getId(),
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
        if(mProductAdapter.getItemCount() == 0) {
            getPreferredProduct();
            return;
        }
        String lastProductId = mProductAdapter.getLastProductId();
        if (lastProductId.equals(product.getId())) {
            lastProductId = "";
        }
        final long currentCallId = ++lastCallId;
        ProductConnector.getInstance().getProductsOfStore(store.getId(),
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
            public void onFailure(@NonNull Exception exp) { }
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
        Uri url = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + store.getGeo());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void actionSaveStore(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    public void actionCommentToStore(View view) {
        if(SignInUtils.getCurrentUser() == null) {
            showSignInDialog();
        } else {
            SignInUtils.getIdToken(new IResult<GetTokenResult>() {
                @Override
                public void onResult(GetTokenResult result) {
                    if(result != null) {
                        launchCommentActivity();
                    } else {
                        showSignInDialog();
                    }
                }

                @Override
                public void onFailure(@NonNull Exception exp) {
                    showSignInDialog();
                }
            });
        }
    }

    private void showSignInDialog() {
        SignInDialog signInDialog = new SignInDialog(this);
        signInDialog.show();
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
        mapIntent.putExtra(Contract.BUNDLE_STORE_KEY, store);
        startActivity(mapIntent);
    }

    public void loadMoreUtility(View view) {
        List<Store.Utility> utilities = store.getUtilities();
        if (utilities.size() == 0) {
            return;
        }
        if (mDialogUtility == null) {
            mDialogUtility = new Dialog(this);
        }
        mDialogUtility.setContentView(R.layout.minh_store_detail_dialog_utility);
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
                store.getContact(), null)));
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
        final Dialog mDialogProduct = new Dialog(StoreDetailActivity.this);
        mDialogProduct.setContentView(R.layout.minh_store_detail_dialog_product);
        AppCompatImageView img_product = mDialogProduct.findViewById(R.id.store_detail_product_img_dialog);
        TextView txt_product_name = mDialogProduct.findViewById(R.id.store_detail_product_name);
        TextView txt_product_type = mDialogProduct.findViewById(R.id.store_detail_product_type);
        TextView txt_product_price = mDialogProduct.findViewById(R.id.store_detail_product_price);
        TextView txt_product_description = mDialogProduct.findViewById(R.id.store_detail_product_description);
        Button btn_close_dialog_product = mDialogProduct.findViewById(R.id.store_detail_btn_close_product);
        txt_product_name.setText(product.getTitle());
        txt_product_type.setText(product.getType().getName());
        txt_product_price.setText(StringUtils.toVNDCurrency(product.getCost()));
        txt_product_description.setText(product.getDescription());
        ObjectUtils.setBitmapToImage(product.getImageURL(), img_product);
        btn_close_dialog_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogProduct.dismiss();
            }
        });
        mDialogProduct.show();
    }

    @Override
    public void onActivityResult(final int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == SignInUtils.SIGN_IN_CODE) {
            if (result == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = accountTask.getResult();
                if (account != null) {
                    SignInUtils.firebaseAuthWithGoogle(account, new IResult<AuthResult>() {
                        @Override
                        public void onResult(AuthResult result) {
                            if (result != null && result.getUser() != null) {
                                String hello = getString(R.string.helloLabel) + result.getUser().getDisplayName();
                                Toast.makeText(StoreDetailActivity.this, hello,
                                        Toast.LENGTH_SHORT).show();
                                launchCommentActivity();
                            } else {
                                Toast.makeText(StoreDetailActivity.this, getString(R.string.failToSignInLabel),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Exception exp) {
                            Toast.makeText(StoreDetailActivity.this, getString(R.string.failToSignInLabel),
                                    Toast.LENGTH_SHORT).show();
                            Log.w("my_error", exp.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(StoreDetailActivity.this, getString(R.string.failToSignInLabel),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void launchCommentActivity() {
        Intent commentIntent = new Intent(this, CommentActivity.class);
        commentIntent.putExtra(Contract.BUNDLE_STORE_KEY, store);
        startActivity(commentIntent);
    }
}
