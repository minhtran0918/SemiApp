package org.semi.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.semi.R;
import org.semi.object.IHaveIdAndName;
import org.semi.object.Location;
import org.semi.object.Product;
import org.semi.adapter.OnBottomReachedListener;
import org.semi.adapter.OnItemClickListener;
import org.semi.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProductViewFragment extends Fragment {

    private ProductAdapter recyclerViewAdapter;
    private IInteractionWithList<IHaveIdAndName<String>> listener;

    public ProductViewFragment() {
        recyclerViewAdapter = new ProductAdapter(new ArrayList<Product>());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_view, container, false);
        RecyclerView productRecyclerView = view.findViewById(R.id.productRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setNestedScrollingEnabled(false);
        productRecyclerView.setLayoutManager(layoutManager);
        productRecyclerView.setAdapter(recyclerViewAdapter);
        if(listener != null) {
            recyclerViewAdapter.setOnBottomReachedListener(new OnBottomReachedListener<Product>() {
                @Override
                public void onBottomReached(Product obj, int position) {
                    listener.onScrollToLimit(obj, position);
                }
            });
            recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener<Product>() {
                @Override
                public void onItemClick(Product obj) {
                    listener.onItemClick(obj);
                }
            });
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IInteractionWithList) {
            listener = (IInteractionWithList<IHaveIdAndName<String>>) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updateDataSet(List<Product> products, Location location) {
        recyclerViewAdapter.setDataSet(products, location);
    }

    public void addDataSet(List<Product> products, Location location) {
        recyclerViewAdapter.addDataSet(products, location);
    }

    public void updateLocation(Location location) {
        recyclerViewAdapter.setLocation(location);
    }

    public int getItemCount() {
        return recyclerViewAdapter.getItemCount();
    }
}
