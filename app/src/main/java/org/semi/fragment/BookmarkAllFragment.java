package org.semi.fragment;


import androidx.lifecycle.MutableLiveData;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.semi.R;
import org.semi.adapter.BookmarkAllAdapter;
import org.semi.databases.SharedPrefs;
import org.semi.object.StoreBookmark;
import org.semi.utils.Contract;
import org.semi.views.StoreDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class BookmarkAllFragment extends Fragment {

    private static BookmarkAllFragment mInstance;
    private RecyclerView mRecyclerView;
    private BookmarkAllAdapter mRcvAdapter;
    private BookmarkAllAdapter.BookmarkAdapterListener mOnClickRcvListener;
    private List<StoreBookmark> mListStore;

    public static BookmarkAllFragment getInstance() {
        if (mInstance == null) {
            mInstance = new BookmarkAllFragment();
        }
        return mInstance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bookmark_all, container, false);
        mRecyclerView = v.findViewById(R.id.recycler_view_fragment_bookmark_all);
        Log.d("Semi", "On Create View");
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRcvAdapter = new BookmarkAllAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRcvAdapter);

        mOnClickRcvListener = new BookmarkAllAdapter.BookmarkAdapterListener() {
            @Override
            public void itemOnClick(View v, int position) {
                String store_id = mRcvAdapter.getListStore().get(position).getId();
                showStoreDetail(store_id);
            }

            @Override
            public void optionOnClick(View v, int position) {
                final int pos = position;
                final String[] listItems = {"Thêm vào Collection", "Xóa"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Thực hiện");

                builder.setItems(listItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            mRcvAdapter.removeItemInList(pos);
                        } else {
                            dialog.dismiss();
                        }
                    }
                }).show();
            }
        };
        mRcvAdapter.setOnClickListener(mOnClickRcvListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mListStore = new ArrayList<>();
        //LoadList
        if (SharedPrefs.getInstance().getListSaveStore() != null) {
            List<String> list = SharedPrefs.getInstance().getListSaveStore();
            for (String item : list) {
                StoreBookmark storeBookmark = new StoreBookmark(
                        SharedPrefs.getInstance().get(item, StoreBookmark.class).getId(),
                        SharedPrefs.getInstance().get(item, StoreBookmark.class).getName(),
                        SharedPrefs.getInstance().get(item, StoreBookmark.class).getAddress(),
                        SharedPrefs.getInstance().get(item, StoreBookmark.class).getUrlImg(),
                        SharedPrefs.getInstance().get(item, StoreBookmark.class).getDate()
                );
                mListStore.add(storeBookmark);
            }
        } else {
            //User not save Nothing
        }
        mRcvAdapter.setListStore(mListStore);
    }

    private void showStoreDetail(String store_id) {
        Intent storeIntent = new Intent(getActivity(), StoreDetailActivity.class);
        storeIntent.putExtra(Contract.BUNDLE_STORE_KEY, store_id);
        startActivity(storeIntent);
    }
}
