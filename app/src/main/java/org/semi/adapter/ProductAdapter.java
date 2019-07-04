package org.semi.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.semi.R;
import org.semi.object.Location;
import org.semi.object.Product;
import org.semi.utils.MathUtils;
import org.semi.utils.ObjectUtils;
import org.semi.utils.StringUtils;

import java.nio.LongBuffer;
import java.util.Collection;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    private List<Product> products;
    private Location location;
    private OnBottomReachedListener<Product> bottomReachedListener;
    private OnItemClickListener<Product> itemClickListener;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    public static class ProductHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView addressTextView;
        private TextView costTextView;
        private TextView distanceTextView;
        private AppCompatImageView logoImageView;
        private LongBuffer lastCallId;

        private ProductHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.productRVTitle);
            addressTextView = view.findViewById(R.id.productRVAddress);
            costTextView = view.findViewById(R.id.productRVCost);
            distanceTextView = view.findViewById(R.id.productRVDistance);
            logoImageView = view.findViewById(R.id.productRVLogo);
            lastCallId = LongBuffer.allocate(1);
        }

        private void setProduct(final Product product, String distanceStr) {
            titleTextView.setText(product.getTitle());
            addressTextView.setText(product.getStore().getAddress().toString());
            costTextView.setText(StringUtils.toVNDCurrency(product.getCost()));
            distanceTextView.setText(distanceStr);
            logoImageView.setImageResource(R.drawable.ic_packing);
            ObjectUtils.setBitmapToImage(product.getImageURL(), logoImageView, lastCallId);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder viewHolder, int i) {
        final Product product = products.get(i);
        String distanceStr = "";
        if (location != null) {
            double distance = MathUtils.haversine(location, product.getStore().getGeo());
            distanceStr = StringUtils.toDistanceFormat(distance);
        }
        viewHolder.setProduct(product, distanceStr);
        if (bottomReachedListener != null && i == products.size() - 1) {
            bottomReachedListener.onBottomReached(products.get(i), i);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(product);
            }
        });
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.product_recyclerview_item1, viewGroup, false);
        ProductHolder holder = new ProductHolder(view);
        return holder;
    }

    public void setDataSet(List<Product> products, @Nullable Location location) {
        this.products = products;
        this.location = location;
        notifyDataSetChanged();
    }

    public void addDataSet(Collection<Product> products, @Nullable Location location) {
        int start = getItemCount();
        this.products.addAll(products);
        this.location = location;
        notifyItemRangeInserted(start, products.size());
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    public void setOnBottomReachedListener(OnBottomReachedListener<Product> listener) {
        this.bottomReachedListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener<Product> listener) {
        this.itemClickListener = listener;
    }
}
