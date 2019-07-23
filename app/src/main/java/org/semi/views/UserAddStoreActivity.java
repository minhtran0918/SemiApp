package org.semi.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.esafirm.imagepicker.model.Image;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.semi.R;
import org.semi.ViewModel.AddStoreViewModel;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;
import org.semi.dialog.AddStoreSelectTimeDialog;
import org.semi.dialog.SelectCategoryStoreDialog;
import org.semi.object.VerifyStore;
import org.semi.object.VerifyStoreAddress;
import org.semi.utils.Contract;
import org.semi.utils.MyApp;
import org.semi.utils.SignInUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;

public class UserAddStoreActivity extends AppCompatActivity implements SelectCategoryStoreDialog.BottomSheetListener, TimePickerDialog.OnTimeSetListener {

    private static final int RC_SELECT_STORE_ADDRESS = 1;
    private static final int RC_SELECT_STORE_LOCATION = 2;
    private static final int RC_SELECT_IMAGE_GALLERY = 3;
    private static final int RC_SELECT_IMAGE_CAMERA = 4;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int RC_SIGN_IN = 1789;
    private int RC_SELECT_IMAGE = 7;
    private AddStoreViewModel mAddStoreViewModel;
    //private int mUserSelectCategory = 0;    //0: nothing

    //Name
    private EditText add_store_name;
    //Category
    private TextView select_store_category;
    //Address
    private EditText add_store_address;
    private TextView add_store_select_city, add_store_select_district, add_store_select_ward;
    //Location
    private TextView add_store_select_location;
    //Tel number
    private EditText add_store_phone_number;
    //Start-End
    private TextView add_store_select_time_open, add_store_select_time_close;
    //Description
    private EditText add_store_description;
    //Select Image
    private TextView select_image_count;
    private int mIDTimePicker;

    private int mAddressCityId, mAddressDistrictId, mAddressWardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_store);
        //Check Login
        if (!SignInUtils.userAuthenticated()) {
            startActivityForResult(SignInUtils.intentCallLogin(), RC_SIGN_IN);
        }
        initView();

        mAddStoreViewModel = ViewModelProviders.of(this).get(AddStoreViewModel.class);

        mAddStoreViewModel.categoryStore.setValue(0);

        //79 - HCM, 762 - Thủ Đức, 0 - Không rõ
        mAddStoreViewModel.chooseCityId.setValue(79);
        mAddStoreViewModel.chooseDistrictId.setValue(762);
        mAddStoreViewModel.chooseWardId.setValue(0);

        //Set time
        mAddStoreViewModel.timeStoreOpenHour.setValue(7);
        mAddStoreViewModel.timeStoreOpenMinute.setValue(0);
        mAddStoreViewModel.timeStoreCloseHour.setValue(22);
        mAddStoreViewModel.timeStoreCloseMinute.setValue(0);

        //Set Default Location
        mAddStoreViewModel.latStore.setValue(0.0);
        mAddStoreViewModel.lngStore.setValue(0.0);

        //Observer
        //Category
        mAddStoreViewModel.categoryStore.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                int set_color_text;
                if (integer != 0) {
                    select_store_category.setText(setTitleCategory(integer));
                    set_color_text = ContextCompat.getColor(MyApp.getContext(), android.R.color.black);
                } else {
                    set_color_text = ContextCompat.getColor(MyApp.getContext(), android.R.color.tab_indicator_text);
                }
                select_store_category.setTextColor(set_color_text);
            }
        });
        //Address
        mAddStoreViewModel.chooseCityId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mAddressCityId = integer;
                AddressCity addressCity = mAddStoreViewModel.getCityByID(integer);
                if (addressCity != null) {
                    add_store_select_city.setText(addressCity.toLiteName());
                }
            }
        });
        mAddStoreViewModel.chooseDistrictId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mAddressDistrictId = integer;
                AddressDistrict addressDistrict = mAddStoreViewModel.getDistrictByID(integer);
                if (addressDistrict != null) {
                    add_store_select_district.setText(addressDistrict.toLiteName());

                }
            }
        });
        mAddStoreViewModel.chooseWardId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mAddressWardId = integer;
                AddressWard addressWard = mAddStoreViewModel.getWardByID(integer);
                if (addressWard != null) {
                    add_store_select_ward.setText(addressWard.toLiteName());
                }
            }
        });
        //Time
        mAddStoreViewModel.timeStoreOpenHour.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String time = integer + ":" + mAddStoreViewModel.timeStoreOpenMinute.getValue();
                add_store_select_time_open.setText(time);
            }
        });
        mAddStoreViewModel.timeStoreOpenMinute.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String time_minute;
                if (integer < 10) {
                    time_minute = "0" + integer;
                } else {
                    time_minute = String.valueOf(integer);
                }
                String time = mAddStoreViewModel.timeStoreOpenHour.getValue() + ":" + time_minute;
                add_store_select_time_open.setText(time);
            }
        });
        mAddStoreViewModel.timeStoreCloseHour.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String time = integer + ":" + mAddStoreViewModel.timeStoreCloseMinute.getValue();
                add_store_select_time_close.setText(time);
            }
        });
        mAddStoreViewModel.timeStoreCloseMinute.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                String time_minute;
                if (integer < 10) {
                    time_minute = "0" + integer;
                } else {
                    time_minute = String.valueOf(integer);
                }
                String time = mAddStoreViewModel.timeStoreCloseHour.getValue() + ":" + time_minute;
                add_store_select_time_close.setText(time);
            }
        });
        //Location
        mAddStoreViewModel.titleLocationStore.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (!s.isEmpty()) {
                    add_store_select_location.setText(s);
                }
            }
        });
        //Image
        mAddStoreViewModel.listImageSelect.observe(this, new Observer<ArrayList<Image>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Image> images) {
                if (images == null) {
                    select_image_count.setText(0);
                } else {
                    select_image_count.setText(String.valueOf(images.size()));
                }
            }
        });
    }

    private void initView() {
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add_store);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        //Name store
        add_store_name = findViewById(R.id.txt_add_name_store);

        //Select Category Store
        select_store_category = findViewById(R.id.txt_select_store_category);
        select_store_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If not hide Keybroad -> The dialog interface is not correct
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Create dialog
                        SelectCategoryStoreDialog selectCategoryStoreDialog = new SelectCategoryStoreDialog();
                        selectCategoryStoreDialog.setUserSelectCategory(mAddStoreViewModel.categoryStore.getValue());
                        selectCategoryStoreDialog.show(getSupportFragmentManager(), "BottomSheet");
                    }
                }, 100);

            }
        });
        //Select Address: City - District - Ward
        add_store_address = findViewById(R.id.txt_add_store_address);
        add_store_select_city = findViewById(R.id.txt_home_option_city);
        add_store_select_district = findViewById(R.id.txt_home_option_district);
        add_store_select_ward = findViewById(R.id.txt_home_option_ward);
        //Location TextView
        add_store_select_location = findViewById(R.id.txt_select_location);
        //Select Time Store Open - Close
        add_store_select_time_open = findViewById(R.id.txt_add_store_select_time_open);
        add_store_select_time_close = findViewById(R.id.txt_add_store_select_time_close);
        //PHone number
        add_store_phone_number = findViewById(R.id.txt_add_store_phone_number);
        //Description
        add_store_description = findViewById(R.id.txt_add_store_description);
        //select image
        select_image_count = findViewById(R.id.txt_add_store_select_image_count);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_store_backpress_dialog_title))
                .setMessage(getString(R.string.add_store_backpress_dialog_message))
                .setPositiveButton(R.string.add_store_backpress_dialog_positive_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton(getString(R.string.add_store_backpress_dialog_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.all_menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (!checkDataCorrect()) return false;
                //Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.add_store_send_data_dialog_title))
                        .setMessage(getString(R.string.add_store_send_data_dialog_message))
                        .setPositiveButton(getString(R.string.add_store_send_data_dialog_positive_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendDataToFirebase();
                            }
                        }).setNegativeButton(getString(R.string.add_store_send_data_dialog_negative_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Call BottomSheetDialog - Select Category Store
    @Override
    public void onSelectCategoryClicked(int itemId) {
        mAddStoreViewModel.categoryStore.setValue(itemId);
    }

    private String setTitleCategory(int itemId) {
        String title = "";
        int id = 1;
        switch (itemId) {
            case (R.id.btn_dialog_item_store):
                title = getString(R.string.all_title_store_store);
                id = 1;
                break;

            case (R.id.btn_dialog_item_convenience):
                title = getString(R.string.all_title_convenience);
                id = 2;
                break;

            case (R.id.btn_dialog_item_super_market):
                title = getString(R.string.all_title_super_market);
                id = 3;
                break;


            case (R.id.btn_dialog_item_mall):
                title = getString(R.string.all_title_mall);
                id = 4;
                break;

            case (R.id.btn_dialog_item_market):
                title = getString(R.string.all_title_market);
                id = 5;
                break;

            case (R.id.btn_dialog_item_atm):
                title = getString(R.string.all_title_atm);
                id = 6;
                break;

            case (R.id.btn_dialog_item_pharmacy):
                title = getString(R.string.all_title_pharmacy);
                id = 7;
                break;
        }
        mAddStoreViewModel.typeStoreID.setValue(id);
        return title;
    }

    //Listener 3 TextView Select Address (City - District - Ward)
    public void onClickSelectAddress(View v) {
        Intent callSelectAddress = new Intent(this, SelectAddressActivity.class);
        switch (v.getId()) {
            case R.id.txt_home_option_city:

                callSelectAddress.putExtra(Contract.MODE_CHOOSE_ADDRESS_KEY, Contract.SELECT_CITY_ADDRESS_MODE);
                callSelectAddress.putExtra(Contract.CITY_ADDRESS_ID_KEY, mAddressCityId);
                callSelectAddress.putExtra(Contract.DISTRICT_ADDRESS_ID_KEY, mAddressDistrictId);
                callSelectAddress.putExtra(Contract.WARD_ADDRESS_ID_KEY, mAddressWardId);

                startActivityForResult(callSelectAddress, RC_SELECT_STORE_ADDRESS);
                break;
            case R.id.txt_home_option_district:
                callSelectAddress.putExtra(Contract.MODE_CHOOSE_ADDRESS_KEY, Contract.SELECT_DISTRICT_ADDRESS_MODE);
                callSelectAddress.putExtra(Contract.CITY_ADDRESS_ID_KEY, mAddressCityId);
                callSelectAddress.putExtra(Contract.DISTRICT_ADDRESS_ID_KEY, mAddressDistrictId);
                callSelectAddress.putExtra(Contract.WARD_ADDRESS_ID_KEY, mAddressWardId);

                startActivityForResult(callSelectAddress, RC_SELECT_STORE_ADDRESS);
                break;
            case R.id.txt_home_option_ward:
                callSelectAddress.putExtra(Contract.MODE_CHOOSE_ADDRESS_KEY, Contract.SELECT_WARD_ADDRESS_MODE);
                callSelectAddress.putExtra(Contract.DISTRICT_ADDRESS_ID_KEY, mAddressDistrictId);
                callSelectAddress.putExtra(Contract.WARD_ADDRESS_ID_KEY, mAddressWardId);

                startActivityForResult(callSelectAddress, RC_SELECT_STORE_ADDRESS);
                break;
        }
    }


    //Location of the store
    //Check service to enable Google Map
    private boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make a map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void onClickSelectLocationOnMap(View view) {
        if (isServicesOK()) {
            Intent callSelectMap = new Intent(this, AddStoreChooseLocationActivity.class);
            if (mAddStoreViewModel.latStore.getValue() == 0.0 || mAddStoreViewModel.lngStore.getValue() == 0.0) {
                callSelectMap.putExtra(Contract.CHECK_HAVE_LOCATION, false);
            } else {
                callSelectMap.putExtra(Contract.CHECK_HAVE_LOCATION, true);
                callSelectMap.putExtra(Contract.LAT_STORE_KEY, mAddStoreViewModel.latStore.getValue());
                callSelectMap.putExtra(Contract.LNG_STORE_KEY, mAddStoreViewModel.lngStore.getValue());
            }
            startActivityForResult(callSelectMap, RC_SELECT_STORE_LOCATION);
        }
    }

    //Uptime of the store
    public void onClickSelectTimeOpen(View view) {
        DialogFragment timePicker = new AddStoreSelectTimeDialog();
        mIDTimePicker = view.getId();
        timePicker.show(getSupportFragmentManager(), "Time_Open");
    }

    public void onClickSelectTimeClose(View view) {
        DialogFragment timePicker = new AddStoreSelectTimeDialog();
        mIDTimePicker = view.getId();
        timePicker.show(getSupportFragmentManager(), "Time_Close");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        switch (mIDTimePicker) {
            case R.id.txt_add_store_select_time_open:
                mAddStoreViewModel.timeStoreOpenHour.setValue(hourOfDay);
                mAddStoreViewModel.timeStoreOpenMinute.setValue(minute);
                break;

            case R.id.txt_add_store_select_time_close:
                mAddStoreViewModel.timeStoreCloseHour.setValue(hourOfDay);
                mAddStoreViewModel.timeStoreCloseMinute.setValue(minute);
                break;
        }
    }

    private boolean checkCorrectTimeStore() {
        if (mAddStoreViewModel.timeStoreOpenHour.getValue() < mAddStoreViewModel.timeStoreCloseHour.getValue()) {
            return true;
        } else if (mAddStoreViewModel.timeStoreOpenHour.getValue() == mAddStoreViewModel.timeStoreCloseHour.getValue()) {
            return mAddStoreViewModel.timeStoreOpenMinute.getValue() < mAddStoreViewModel.timeStoreCloseMinute.getValue();
        }
        return false;
    }

    public void onClickSelectImage(View view) {
        Intent intent = new Intent(this, AddStoreSelectImageActivity.class);
        if (mAddStoreViewModel.listImageSelect.getValue() != null) {
            intent.putExtra(Contract.LIST_IMAGE_EXTRA, mAddStoreViewModel.listImageSelect.getValue());
        }
        startActivityForResult(intent, RC_SELECT_IMAGE);
    }

    //Select Image
    public void onClickSelectImageFromGallery(View view) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, RC_SELECT_IMAGE_GALLERY);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
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
            finish();
            return;
        }
        if (requestCode == RC_SELECT_STORE_ADDRESS && resultCode == RESULT_OK) {
            int city_id = data.getIntExtra(Contract.CITY_ADDRESS_ID_KEY, -1);
            if (city_id != -1) {
                mAddStoreViewModel.chooseCityId.setValue(city_id);
            }
            int district_id = data.getIntExtra(Contract.DISTRICT_ADDRESS_ID_KEY, -1);
            if (district_id != -1) {
                mAddStoreViewModel.chooseDistrictId.setValue(district_id);
            }
            int ward_id = data.getIntExtra(Contract.WARD_ADDRESS_ID_KEY, -1);
            if (ward_id != -1) {
                mAddStoreViewModel.chooseWardId.setValue(ward_id);
            }
        }
        if (requestCode == RC_SELECT_STORE_LOCATION && resultCode == RESULT_OK) {
            mAddStoreViewModel.latStore.setValue(data.getDoubleExtra(Contract.LAT_STORE_KEY, 0.0));
            mAddStoreViewModel.lngStore.setValue(data.getDoubleExtra(Contract.LNG_STORE_KEY, 0.0));
            mAddStoreViewModel.setTitleLocationStore();
        }
        if (requestCode == RC_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<Image> list = data.getParcelableArrayListExtra(Contract.LIST_IMAGE_EXTRA);
                mAddStoreViewModel.listImageSelect.setValue(list);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Finish
    private boolean checkDataCorrect() {
        if (add_store_name.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Tên cửa hàng còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mAddStoreViewModel.categoryStore.getValue() == 0) {
            Toast.makeText(this, "Bạn chưa chọn loại cửa hàng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (add_store_address.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Địa chỉ cửa hàng còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkCorrectTimeStore()) {
            Toast.makeText(this, "Thời gian mở cửa của cửa hàng chưa chính xác", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendDataToFirebase() {
        if (SignInUtils.userAuthenticated()) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference newStoreRef = db.collection("verify_stores").document();
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            VerifyStore store = new VerifyStore();

            store.setUser_id(user_id);
            store.setStore_id(newStoreRef.getId());
            store.setTitle(add_store_name.getText().toString().trim());
            store.setType(mAddStoreViewModel.typeStoreID.getValue());

            //Address
            VerifyStoreAddress address = new VerifyStoreAddress();
            address.setStreet(add_store_address.getText().toString().trim());
            address.setWard(mAddStoreViewModel.chooseWardId.getValue());
            address.setDistrict(mAddStoreViewModel.chooseDistrictId.getValue());
            address.setCity(mAddStoreViewModel.chooseCityId.getValue());

            store.setAddress(address);

            //Location
            GeoPoint location = new GeoPoint(mAddStoreViewModel.latStore.getValue(), mAddStoreViewModel.lngStore.getValue());
            store.setGeo(location);
            store.setContact(add_store_phone_number.getText().toString().trim());
            //Time Store: 00:00-00:00
            StringBuilder sb_time = new StringBuilder();
            sb_time
                    .append(add_store_select_time_open.getText().toString().trim())
                    .append("-")
                    .append(add_store_select_time_close.getText().toString().trim());
            store.setStartEnd(sb_time.toString());
            store.setDescription(add_store_description.getText().toString().trim());
            //Image
            if (mAddStoreViewModel.listImageSelect.getValue() != null) {
                //List image from Library ImagePicker
                ArrayList<Image> imageArrayList = mAddStoreViewModel.listImageSelect.getValue();

                //Upload from File:image.path to storage_path: verify_store/store_id/user_id/list_name
                uploadImage(imageArrayList, newStoreRef.getId(), user_id);

                //Get list name: verify_store/store_id/user_id/IMG_0_Time.extension_file + "##"
                StringBuilder sb = new StringBuilder();

                for (String storage_location : convertPathFileToStorage(imageArrayList, newStoreRef.getId(), user_id)) {
                    sb.append(storage_location).append("?");
                }
                sb.setLength(sb.length() - 2);
                store.setImageURL(sb.toString());

            } else {
                store.setImageURL("");
            }

            //Task upload
            newStoreRef.set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Semi", "Document add to: ID = " + newStoreRef.getId());
                        Toast.makeText(UserAddStoreActivity.this, "Thực hiện thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UserAddStoreActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.e(Contract.TAG + UserAddStoreActivity.class.getSimpleName(), task.getException().getMessage());
                    }
                }
            });
        } else {
            startActivityForResult(SignInUtils.intentCallLogin(), RC_SIGN_IN);
        }
    }

    private List<String> convertPathFileToStorage(@NonNull ArrayList<Image> listImage, String store_id, String user_id) {
        List<String> result = new ArrayList<>();
        //Time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        String root = Contract.FIREBASE_PATH_VERIFY_STORE + store_id + "/" + user_id + "/";

        for (int i = 0; i < listImage.size(); i++) {
            Uri file = Uri.fromFile(new File(listImage.get(i).getPath()));
            String fileExt = MimeTypeMap.getFileExtensionFromUrl(file.toString());
            String storage_location = root + "IMG_" + i + "_" + sdf.format(c.getTime()) + "." + fileExt;
            result.add(storage_location);
        }
        return result;
    }

    private void uploadImage(ArrayList<Image> list, String store_id, String user_id) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        List<String> storage_location = convertPathFileToStorage(list, store_id, user_id);

        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getPath());

            if (file != null) {
                Uri uri;
                try {
                    /*File compressedImage = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(file);
                    //Ảnh gốc to*/
                    File compressedImage = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(60)
                            .compressToFile(file);
                    uri = Uri.fromFile(compressedImage);
                    Log.d("Semi", "up load ảnh đã compress: " + file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    uri = Uri.fromFile(file);
                    Log.d("Semi", "không compress được: sử dụng file - " + file.getAbsolutePath());
                }
                StorageReference uploadRef =
                        storageRef.child(storage_location.get(i));
                uploadRef.putFile(uri);
            }
        }

/*        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(Contract.TAG, exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d(Contract.TAG, "Xong: " + taskSnapshot.getMetadata().toString());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(Contract.TAG, "Upload is " + progress + "% done");
            }
        });*/
    }
}
