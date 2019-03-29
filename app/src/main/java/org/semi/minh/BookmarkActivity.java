package org.semi.minh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.semi.R;
import org.semi.minh.object.StoreBookmark;

import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {
    Toolbar mRootToolbar;
    RecyclerView mRecyclerStore;
    List<StoreBookmark> mListStore;
    BookmarkStoreAdapter mBookmarkStoreAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minh_activity_bookmark);
        initView();
    }

    private void initView() {
        mRootToolbar = findViewById(R.id.toolbar_store_detail);
        mRootToolbar.setTitle("");
        setSupportActionBar(mRootToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mListStore = new ArrayList<>();
        mListStore.add(new StoreBookmark("ID1","Cửa hàng lưu 1","Địa chỉ 1"));
        mListStore.add(new StoreBookmark("ID2","Cửa hàng lưu 2","Địa chỉ 2"));

        if (mListStore.size() == 0) {
            TextView txt_error_comment = findViewById(R.id.txt_error_comment);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 20, 10, 20);
            txt_error_comment.setLayoutParams(params);

            txt_error_comment.setText(R.string.comment_no_save_mess);
            txt_error_comment.setTextSize(getResources().getDimension(R.dimen.store_detail_error_list_text_size));
            txt_error_comment.setLines(2);
        }else{
            mRecyclerStore = findViewById(R.id.store_bookmark_list);
            mRecyclerStore.setLayoutManager(new LinearLayoutManager(this));
            mBookmarkStoreAdapter = new BookmarkStoreAdapter(this, mListStore);
            mRecyclerStore.setAdapter(mBookmarkStoreAdapter);
        }
    }
    private void loadData(){

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
}
