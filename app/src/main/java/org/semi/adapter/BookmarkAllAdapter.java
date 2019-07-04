package org.semi.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.semi.R;
import org.semi.firebase.GlideApp;
import org.semi.firebase.StorageConnector;
import org.semi.object.StoreBookmark;
import org.semi.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BookmarkAllAdapter extends RecyclerView.Adapter<BookmarkAllAdapter.BookmarkViewHolder> {

    public interface BookmarkAdapterListener {
        void itemOnClick(View v, int position);

        void optionOnClick(View v, int position);
    }

    private List<StoreBookmark> mListStore;

    private BookmarkAdapterListener mOnClickListener;

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_bookmark_all, viewGroup, false);
        return new BookmarkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookmarkViewHolder bookmarkViewHolder, int i) {
        final StoreBookmark storeBookmark = mListStore.get(i);
        if (!storeBookmark.getUrlImg().equals("")) {
            List<String> a = StringUtils.getListPath(storeBookmark.getUrlImg());
            String path_img = a.get(0);
            GlideApp.with(bookmarkViewHolder.itemView)
                    .load(StorageConnector.getReference(path_img))
                    .centerCrop()
                    .override(100, 100)
                    .placeholder(R.drawable.all_ic_loading_black_24)
                    .into(bookmarkViewHolder.mImgAvatarStore);
        } else {
            bookmarkViewHolder.mImgAvatarStore.setImageResource(R.drawable.all_ic_store_128);
        }

        bookmarkViewHolder.mTxtNameStore.setText(storeBookmark.getName());
        bookmarkViewHolder.mTxtAddressStore.setText(storeBookmark.getAddress());
        bookmarkViewHolder.mTxtSaveTime.setText(storeBookmark.getDate().toString());
    }

    public List<StoreBookmark> getListStore() {
        return mListStore;
    }

    public void setListStore(List<StoreBookmark> stores) {
        if (mListStore == null) {
            mListStore = new ArrayList<>();
        }
        mListStore.clear();
        mListStore.addAll(stores);
        notifyDataSetChanged();
    }

    public void setOnClickListener(BookmarkAdapterListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public void removeItemInList(int position) {
        mListStore.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        if (mListStore != null) {
            return mListStore.size();
        } else {
            return 0;
        }
    }

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgAvatarStore;
        private TextView mTxtNameStore, mTxtAddressStore, mTxtSaveTime;
        private ImageButton mImgOptionMenu;
        private ConstraintLayout mItemLayout;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            mImgAvatarStore = itemView.findViewById(R.id.img_bookmark_all_avatar_store);
            mTxtNameStore = itemView.findViewById(R.id.txt_bookmark_all_name_store);
            mTxtAddressStore = itemView.findViewById(R.id.txt_bookmark_all_address_store);
            mTxtSaveTime = itemView.findViewById(R.id.txt_me_bookmark_save_time);
            mItemLayout = itemView.findViewById(R.id.me_bookmar_item);
            mItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.itemOnClick(v, getAdapterPosition());
                }
            });

            mImgOptionMenu = itemView.findViewById(R.id.btn_me_bookmark_option);
            mImgOptionMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.optionOnClick(v, getAdapterPosition());
                }
            });
        }
    }
}