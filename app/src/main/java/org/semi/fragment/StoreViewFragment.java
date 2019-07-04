package org.semi.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.semi.R;
import org.semi.object.IHaveIdAndName;
import org.semi.object.Location;
import org.semi.object.Store;
import org.semi.adapter.OnBottomReachedListener;
import org.semi.adapter.OnItemClickListener;
import org.semi.adapter.StoreAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoreViewFragment extends Fragment {

    private View mRootView;
    private RecyclerView mRecyclerView;
    private StoreAdapter mRcvAdapter;
    private IInteractionWithList<IHaveIdAndName<String>> listener;

    public StoreViewFragment() {
        mRcvAdapter = new StoreAdapter(new ArrayList<Store>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view_fragment_home_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mRootView.getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRcvAdapter);

        if (listener != null) {
            mRcvAdapter.setOnBottomReachedListener(new OnBottomReachedListener<Store>() {
                @Override
                public void onBottomReached(Store obj, int position) {
                    listener.onScrollToLimit(obj, position);
                }
            });
            mRcvAdapter.setOnItemClickListener(new OnItemClickListener<Store>() {
                @Override
                public void onItemClick(Store obj) {
                    listener.onItemClick(obj);
                }
            });
        }
        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IInteractionWithList) {
            listener = (IInteractionWithList<IHaveIdAndName<String>>) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updateDataSet(List<Store> stores, Location location) {
        mRcvAdapter.setDataSet(stores, location);
    }

    public void addDataSet(List<Store> stores, Location location) {
        mRcvAdapter.addDataSet(stores, location);
    }

    public void updateLocation(Location location) {
        mRcvAdapter.setLocation(location);
    }

    public int getItemCount() {
        return mRcvAdapter.getItemCount();
    }
}
