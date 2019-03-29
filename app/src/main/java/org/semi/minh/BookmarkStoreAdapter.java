package org.semi.minh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.semi.R;
import org.semi.minh.object.StoreBookmark;

import java.util.List;

public class BookmarkStoreAdapter extends RecyclerView.Adapter<BookmarkStoreAdapter.ViewHolder> {
    private Context mRootContext;
    private List<StoreBookmark> mData;


    // data is passed into the constructor

    BookmarkStoreAdapter(Context mRootContext, List<StoreBookmark> data) {
        this.mRootContext = mRootContext;
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public BookmarkStoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mRootContext);
        View view = mInflater.inflate(R.layout.minh_bookmark_store_list_item, parent, false);
        return new BookmarkStoreAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull BookmarkStoreAdapter.ViewHolder holder, int position) {
        //holder.store_bookmark_img.setImageResource(mData.get(position).get());
        holder.store_bookmark_title.setText(mData.get(position).getName());
        holder.store_bookmark_address.setText(mData.get(position).getAddress());
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }
    /*
    public void addData(Collection<Comment> data) {
        int lenght = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(lenght, data.size());
    }*/

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView store_bookmark_title, store_bookmark_address;
        ImageView store_bookmark_img;

        public ViewHolder(View itemView) {
            super(itemView);
            store_bookmark_title = itemView.findViewById(R.id.store_bookmark_title);
            store_bookmark_address = itemView.findViewById(R.id.store_bookmark_address);
            store_bookmark_img = itemView.findViewById(R.id.store_bookmark_img);

        }
    }
}
