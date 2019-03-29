package org.semi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.semi.R;
import org.semi.object.IHaveIdAndName;
import org.semi.object.Location;
import org.semi.object.Store;
import org.semi.custom.OnBottomReachedListener;
import org.semi.custom.OnItemClickListener;
import org.semi.custom.StoreAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoreViewFragment extends Fragment {

    private StoreAdapter recyclerViewAdapter;
    private IInteractionWithList<IHaveIdAndName<String>> listener;

    public StoreViewFragment() {
        recyclerViewAdapter = new StoreAdapter(new ArrayList<Store>());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_view, container, false);
        RecyclerView storeRecyclerView = view.findViewById(R.id.storeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        storeRecyclerView.setHasFixedSize(true);
        storeRecyclerView.setNestedScrollingEnabled(false);
        storeRecyclerView.setLayoutManager(layoutManager);
        storeRecyclerView.setAdapter(recyclerViewAdapter);
        if(listener != null) {
            recyclerViewAdapter.setOnBottomReachedListener(new OnBottomReachedListener<Store>() {
                @Override
                public void onBottomReached(Store obj, int position) {
                    listener.onScrollToLimit(obj, position);
                }
            });
            recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener<Store>() {
                @Override
                public void onItemClick(Store obj) {
                    listener.onItemClick(obj);
                }
            });;
        }

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

    public void updateDataSet(List<Store> stores, Location location) {
        recyclerViewAdapter.setDataSet(stores, location);
    }

    public void addDataSet(List<Store> stores, Location location) {
        recyclerViewAdapter.addDataSet(stores, location);
    }

    public void updateLocation(Location location) {
        recyclerViewAdapter.setLocation(location);
    }

    public int getItemCount() {
        return recyclerViewAdapter.getItemCount();
    }
}
