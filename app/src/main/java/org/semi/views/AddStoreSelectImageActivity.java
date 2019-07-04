package org.semi.views;

import android.Manifest;
import androidx.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.semi.R;
import org.semi.adapter.ImageListAdapter;
import org.semi.utils.Contract;

import java.util.ArrayList;
import java.util.List;

public class AddStoreSelectImageActivity extends AppCompatActivity {

    private ArrayList<Image> mListImage;
    private RecyclerView mRootRecyclerView;
    private ImageListAdapter mRcvAdapter;
    private View.OnClickListener mOnItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store_select_image);
        initView();
    }

    private void initView() {
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add_store_choose_image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        final TextView txt_count_image = findViewById(R.id.toolbar_add_store_choose_image_title);

        mListImage = new ArrayList<>();

        Intent intent = getIntent();
        ArrayList<Image> temp = intent.getParcelableArrayListExtra(Contract.LIST_IMAGE_EXTRA);
        if (temp != null) {
            mListImage.addAll(temp);
        }

        mRcvAdapter = new ImageListAdapter();
        mRootRecyclerView = findViewById(R.id.recyclerview_add_store_select_img);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        //Use when item = width & height
        mRootRecyclerView.setHasFixedSize(true);

        mRootRecyclerView.setLayoutManager(layoutManager);
        mOnItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                Toast.makeText(AddStoreSelectImageActivity.this, mListImage.get(position).getPath(), Toast.LENGTH_SHORT).show();
            }
        };
        mRcvAdapter.setOnItemClickListener(mOnItemClickListener);
        mRcvAdapter.setListImage(mListImage);
        mRootRecyclerView.setAdapter(mRcvAdapter);
        mRcvAdapter.countImage.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == 0) {
                    txt_count_image.setText(getString(R.string.add_store_select_image_toolbar_title));
                } else {
                    txt_count_image.setText("Đã chọn " + integer + " hình");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_store_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_store_done:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Contract.LIST_IMAGE_EXTRA, mRcvAdapter.getListImage());
                setResult(RESULT_OK, resultIntent);
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSelectImageFromGallery(View view) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            ImagePicker.create(AddStoreSelectImageActivity.this)
                                    .returnMode(ReturnMode.CAMERA_ONLY) // set whether pick and / or camera action should return immediate result or not.
                                    //.folderMode(true) // folder mode (false by default)
                                    //.toolbarFolderTitle("Thư viện hình ảnh") // folder selection title
                                    .toolbarImageTitle("Chọn hình ảnh") // image selection title
                                    .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                                    .multi() // multi mode (default mode)
                                    //.limit(20) // max images can be selected (99 by default)
                                    .showCamera(true) // show camera or not (true by default)
                                    //.imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                                    .origin(mListImage) // original selected images, used in multi mode
                                    .theme(R.style.AppTheme)
                                    .start(); // start image picker activity with request code
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
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            mListImage.clear();
            mListImage.addAll(ImagePicker.getImages(data));
            mRcvAdapter.setListImage(mListImage);
            mRcvAdapter.notifyDataSetChanged();
        }
    }

    public void onClickSelectImageDelete(View view) {
        if (mRcvAdapter.countImage.getValue() != 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa tất cả ảnh đã chọn")
                    .setPositiveButton("XÓA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListImage.clear();
                            mRcvAdapter.setListImage(mListImage);
                            mRcvAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("QUAY LẠI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
}