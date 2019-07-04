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
import org.semi.databases.model.AddressDistrict;

import java.util.List;


public class SelectDistrictFragment extends Fragment {

    private AddressViewModel mAddressViewModel;

    private View mRootView;
    private RecyclerView mRecyclerViewAddress;
    private AddressListAdapter mListAddressAdapter;
    private List<AddressDistrict> mListDistrict;

    private View.OnClickListener mOnClickListener;
    private CallBackSelectAddressListener mCallBackListener;

    public SelectDistrictFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_select_district, container, false);
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
        //setup data to recycler view
        mAddressViewModel.getListDistrictByID().observe(this, new Observer<List<AddressDistrict>>() {
            @Override
            public void onChanged(@Nullable List<AddressDistrict> addressDistricts) {
                mListDistrict = addressDistricts;
                //setList
                mListAddressAdapter.setListDistrict(mListDistrict);

                int district_id = mAddressViewModel.districtId.getValue();
                if (district_id != 0) {
                    mListAddressAdapter.changeCheckedState(district_id);
                    int itemPos = mListAddressAdapter.getIndexFromList(district_id);
                    if (itemPos != -1) {
                        mRecyclerViewAddress.scrollToPosition(mListAddressAdapter.getIndexFromList(district_id));
                    }
                }
            }
        });
        //RecyclerView init
        mRecyclerViewAddress = mRootView.findViewById(R.id.recycler_view_select_address_district);
        mRecyclerViewAddress.setLayoutManager(new LinearLayoutManager(getContext()));

        mListAddressAdapter = new AddressListAdapter();
        mRecyclerViewAddress.setAdapter(mListAddressAdapter);

        //Set OnListener for Recyclerview
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                AddressDistrict item = mAddressViewModel.mListDistricts.getValue().get(position);
                int old_id = mAddressViewModel.districtId.getValue();

                if (mCallBackListener != null) {
                    if (old_id != item.getId()) {

                        mAddressViewModel.districtId.setValue(item.getId());
                        mAddressViewModel.wardId.setValue(0);

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
