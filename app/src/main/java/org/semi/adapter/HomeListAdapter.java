package org.semi.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.semi.R;
import org.semi.firebase.GlideApp;
import org.semi.firebase.StorageConnector;
import org.semi.object.Location;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.utils.Contract;
import org.semi.utils.MathUtils;
import org.semi.utils.ObjectUtils;
import org.semi.utils.StringUtils;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.HomeViewHolder> {


    private int mModeCurrentLoad = 0;   //Store default
    private List<Store> mListStore = new ArrayList<>();
    private List<Product> mListProduct = new ArrayList<>();

    private Location location;
    private View.OnClickListener mOnItemClickListener;
    private LongBuffer lastCallId = LongBuffer.allocate(1);


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.store_recyclerview_item, viewGroup, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder homeViewHolder, int i) {
        if (mModeCurrentLoad == Contract.MODE_HOME_LOAD_STORE) {
            float ratingValue = mListStore.get(i).getRating();
            homeViewHolder.titleTextView.setText(mListStore.get(i).getTitle());
            homeViewHolder.addressTextView.setText(mListStore.get(i).getAddress().toString());
            homeViewHolder.ratingTextView.setText(String.format("%.1f", ratingValue));
            if (!mListStore.get(i).getImageURL().equals("")) {
                List<String> a = StringUtils.getListPath(mListStore.get(i).getImageURL());
                String path_img = a.get(0);
                GlideApp.with(homeViewHolder.itemView)
                        .load(StorageConnector.getReference(path_img))
                        .override(80, 80)
                        .placeholder(R.drawable.all_ic_loading_black_24)
                        .error(R.drawable.all_ic_store_128)
                        .into(homeViewHolder.logoImageView);
            } else {
                homeViewHolder.logoImageView.setImageResource(R.drawable.all_ic_store_128);
            }
            String distanceStr = "";
            //
            if (location != null) {
                double distance = MathUtils.haversine(location, mListStore.get(i).getGeo());
                distanceStr = StringUtils.toDistanceFormat(distance);
            }
            homeViewHolder.distanceTextView.setText(distanceStr);
            //set rating color
            int loopLimit = Contract.RATING_LEVELS.length - 1;
            for (int j = 0; j < loopLimit; j++) {
                if (Contract.RATING_LEVELS[j] <= ratingValue && Contract.RATING_LEVELS[j + 1] >= ratingValue) {
                    homeViewHolder.ratingTextView.setTextColor(Contract.RATING_COLORS[j]);
                    break;
                }
            }
            //Set Image
            //ObjectUtils.setBitmapToImage(mListStore.get(i).getImageURL(), homeViewHolder.logoImageView, lastCallId);
        } else {
            homeViewHolder.titleTextView.setText(mListProduct.get(i).getTitle());
            homeViewHolder.addressTextView.setText(mListProduct.get(i).getStore().getAddress().toString());
            homeViewHolder.ratingTextView.setText(String.valueOf(mListProduct.get(i).getCost()));
            homeViewHolder.ratingTextView.setTextColor(Contract.RATING_COLORS[1]);
            if (!mListProduct.get(i).getImageURL().equals("")) {
                List<String> a = StringUtils.getListPath(mListProduct.get(i).getImageURL());
                String path_img = a.get(0);
                GlideApp.with(homeViewHolder.itemView)
                        .load(StorageConnector.getReference(path_img))
                        .override(80, 80)
                        .placeholder(R.drawable.all_ic_loading_black_24)
                        .error(R.drawable.all_ic_product_128)
                        .into(homeViewHolder.logoImageView);
            } else {
                homeViewHolder.logoImageView.setImageResource(R.drawable.all_ic_product_128);
            }
            String distanceStr = "";
            //
            if (location != null) {
                double distance = MathUtils.haversine(location, mListProduct.get(i).getStore().getGeo());
                distanceStr = StringUtils.toDistanceFormat(distance);
            }
            homeViewHolder.distanceTextView.setText(distanceStr);
        }

    }

    public void setDataSetStore(List<Store> stores, @Nullable Location location) {
        mModeCurrentLoad = Contract.MODE_HOME_LOAD_STORE;
        mListProduct.clear();
        notifyDataSetChanged();
        if (mListStore != null) {
            mListStore.clear();
        }
        if (stores != null) {
            mListStore.addAll(stores);
            notifyDataSetChanged();
        } else {
            mListStore.clear();
            notifyDataSetChanged();
        }
        this.location = location;
    }

    public void setDataSetProduct(List<Product> products, @Nullable Location location) {
        mModeCurrentLoad = Contract.MODE_HOME_LOAD_PRODUCT;
        mListStore.clear();
        notifyDataSetChanged();

        if (mListProduct != null) {
            mListProduct.clear();
        }
        if (products != null) {
            mListProduct.addAll(products);
            notifyDataSetChanged();
        } else {
            mListProduct.clear();
            notifyDataSetChanged();
        }
        this.location = location;
    }

    public void addDataSet(Collection<Store> stores, @Nullable Location location) {
        int start = getItemCount();
        this.mListStore.addAll(stores);
        this.location = location;
        notifyItemRangeInserted(start, stores.size());
    }

    @Override
    public int getItemCount() {
        if (mModeCurrentLoad == Contract.MODE_HOME_LOAD_STORE) {
            if (mListStore != null) {
                return mListStore.size();
            } else
                return 0;
        } else {
            if (mListProduct != null) {
                return mListProduct.size();
            } else
                return 0;
        }

    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView addressTextView;
        private TextView ratingTextView;
        private TextView distanceTextView;
        private ImageView logoImageView;
        private LongBuffer lastCallId;

        private HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.storeRVTitle);
            addressTextView = itemView.findViewById(R.id.storeRVAddress);
            ratingTextView = itemView.findViewById(R.id.storeRVRating);
            distanceTextView = itemView.findViewById(R.id.storeRVDistance);
            logoImageView = itemView.findViewById(R.id.storeRVLogo);
            lastCallId = LongBuffer.allocate(1);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
