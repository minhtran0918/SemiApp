package org.semi.views;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import org.semi.utils.Contract;
import org.semi.R;
import org.semi.firebase.CommentConnector;
import org.semi.firebase.IResult;
import org.semi.object.Store;
import org.semi.utils.DialogUtils;

public class CommentActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText commentEditText;
    private TextView storeNameTextView;
    private TextView storeAddressTextView;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_need_edit);
        getStoreFromIntent();
        setupToolbar();
        initView();
    }

    private void initView() {
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.comment_content_input);
        storeNameTextView = findViewById(R.id.store_detail_name);
        storeAddressTextView = findViewById(R.id.store_detail_address);
        storeNameTextView.setText(store.getName());
        storeAddressTextView.setText(store.getAddress().toString());
    }

    private void setupToolbar(){
        Toolbar toolbar_store_detail = findViewById(R.id.toolbar_store_detail);
        toolbar_store_detail.setTitle("");
        setSupportActionBar(toolbar_store_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getStoreFromIntent() {
        Intent intent = getIntent();
        store = (Store) intent.getSerializableExtra(Contract.BUNDLE_STORE_KEY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onCancelled(View view) {
        finish();
    }

    public void onComment(View view){
        float rating = ratingBar.getRating();
        String comment = commentEditText.getText().toString();
        openLoading();
        CommentConnector.getInstance().postComment(store.getId(), comment, rating, new IResult<Timestamp>() {
            @Override
            public void onResult(Timestamp result) {
                closeLoading();
                if(result != null) {
                    DialogUtils.showInfoDialog(CommentActivity.this, "Đã thêm bình luận.");
                } else {
                    DialogUtils.showInfoDialog(CommentActivity.this, "Có lỗi xảy ra.");
                }

            }

            @Override
            public void onFailure(@NonNull Exception exp) {
                closeLoading();
                DialogUtils.showInfoDialog(CommentActivity.this, "Có lỗi xảy ra.");
            }
        });
    }

    private void closeLoading() {
        findViewById(R.id.loadingTextView).setVisibility(View.GONE);
    }
    private void openLoading() {
        findViewById(R.id.loadingTextView).setVisibility(View.VISIBLE);
    }
}
