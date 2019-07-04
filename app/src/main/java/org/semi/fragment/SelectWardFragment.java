package org.semi.fragment;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.semi.listener.CallBackSelectAddressListener;
import org.semi.R;
import org.semi.adapter.AddressListAdapter;
import org.semi.ViewModel.AddressViewModel;
import org.semi.databases.model.AddressWard;

import java.util.List;


public class SelectWardFragment extends Fragment {

    private AddressViewModel mAddressViewModel;

    private View mRootView;
    private RecyclerView mRecyclerViewAddress;
    private AddressListAdapter mListAddressAdapter;

    //private int mModeSelect, mDistrictID;
    private List<AddressWard> mListWard;

    private View.OnClickListener mOnClickListener;
    private CallBackSelectAddressListener mCallBackListener;

    public SelectWardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_select_ward, container, false);
        initView();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getActivity() is fully created in onActivityCreated and instanceOf differentiate it between different Activities
        if (getActivity() instanceof CallBackSelectAddressListener)
            mCallBackListener = (CallBackSelectAddressListener) getActivity();
    }

    private void initView() {

        //Get the value from the ViewModel
        mAddressViewModel = ViewModelProviders.of(getActivity()).get(AddressViewModel.class);
        mAddressViewModel.modeSelect.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mListAddressAdapter.setModeSelect(integer);
            }
        });
        mAddressViewModel.getListWardByID().observe(this, new Observer<List<AddressWard>>() {
            @Override
            public void onChanged(@Nullable List<AddressWard> addressWards) {
                mListWard = addressWards;
                //setList
                mListAddressAdapter.setListWard(mListWard);
                int ward_id = mAddressViewModel.wardId.getValue();
                if (ward_id != -1) {
                    mListAddressAdapter.changeCheckedState(ward_id);
                    int itemPos = mListAddressAdapter.getIndexFromList(ward_id);
                    if (itemPos != -1) {
                        mRecyclerViewAddress.scrollToPosition(mListAddressAdapter.getIndexFromList(ward_id));
                    }
                }
            }
        });
        //RecyclerView
        mRecyclerViewAddress = mRootView.findViewById(R.id.recycler_view_select_address_ward);
        mRecyclerViewAddress.setLayoutManager(new LinearLayoutManager(getContext()));

        mListAddressAdapter = new AddressListAdapter();
        mRecyclerViewAddress.setAdapter(mListAddressAdapter);

        //Set OnListener for Recyclerview
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                AddressWard item = mAddressViewModel.mListWards.getValue().get(position);

                int old_id = mAddressViewModel.wardId.getValue();

                if (mCallBackListener != null) {
                    if (old_id != item.getId()) {
                        mAddressViewModel.wardId.setValue(item.getId());

                        mListAddressAdapter.changeCheckedState(item.getId());
                        mCallBackListener.onCallBackReturnAddress(true);
                    } else {
                        mCallBackListener.onCallBackReturnAddress(false);
                    }
                }
            }
        };
        mListAddressAdapter.setOnItemClickListener(mOnClickListener);
    }
}
