package org.semi.minh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.semi.R;
import org.semi.custom.OnItemClickListener;
import org.semi.object.Product;
import org.semi.util.ObjectUtils;

import java.nio.LongBuffer;
import java.util.Collection;
import java.util.List;

public class StoreDetailProductAdapter extends RecyclerView.Adapter<StoreDetailProductAdapter.ViewHolder> {
    private Context mRootContext;
    private List<Product> mData;
    private OnItemClickListener<Product> listener;

    // data is passed into the constructor
    StoreDetailProductAdapter(Context mRootContext, List<Product> data) {
        this.mRootContext = mRootContext;
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mRootContext);
        View view = mInflater.inflate(R.layout.minh_store_detail_list_product_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setProduct(mData.get(position));
        if (listener == null) {
            return;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(mData.get(position));
                }
            }
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    public void addData(Collection<Product> data) {
        int length = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(length, data.size());
    }

    public void addData(Product data) {
        int length = mData.size();
        mData.add(data);
        notifyItemRangeInserted(length, 1);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView store_detail_product_name;
        AppCompatImageView store_detail_product_image;
        CardView product_item;
        private ItemClickListener itemClickListener;
        private LongBuffer lastCallId;

        private ViewHolder(View itemView) {
            super(itemView);
            store_detail_product_name = itemView.findViewById(R.id.store_detail_product_name);
            store_detail_product_image = itemView.findViewById(R.id.store_detail_product_image);
            product_item = itemView.findViewById(R.id.product_item);
            lastCallId = LongBuffer.allocate(1);
        }

        private void setProduct(Product product) {
            store_detail_product_name.setText(product.getTitle());
            store_detail_product_image.setImageResource(R.drawable.ic_packing);
            ObjectUtils.setBitmapToImage(product.getImageURL(), store_detail_product_image, lastCallId);
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public String getLastProductId() {
        if (mData.size() > 0) {
            return mData.get(mData.size() - 1).getId();
        }
        return "";
    }

    public void setOnItemClickListener(OnItemClickListener<Product> listener) {
        this.listener = listener;
    }
}
