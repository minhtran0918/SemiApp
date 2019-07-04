package org.semi.fragment;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.semi.listener.CallBackSelectAddressListener;
import org.semi.R;
import org.semi.adapter.AddressListAdapter;
import org.semi.ViewModel.AddressViewModel;
import org.semi.databases.model.AddressCity;

import java.util.ArrayList;
import java.util.List;

public class SelectCityFragment extends Fragment {

    private AddressViewModel mAddressViewModel;

    private View mRootView;
    private RecyclerView mRecyclerViewAddress;
    private AddressListAdapter mListAddressAdapter;
    private int mModeSelect;
    private List<AddressCity> mListCity;

    private View.OnClickListener mOnClickListener;
    private CallBackSelectAddressListener mCallBackListener;

    public SelectCityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_select_city, container, false);
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
                mModeSelect = integer;
                mListAddressAdapter.setModeSelect(mModeSelect);
            }
        });
        mAddressViewModel.getListAllCities().observe(this, new Observer<List<AddressCity>>() {
            @Override
            public void onChanged(@Nullable List<AddressCity> addressCities) {
                mListCity = addressCities;
                if (mListCity.isEmpty() == false) {
                    mListAddressAdapter.setListCity(mListCity);

                    int city_id = mAddressViewModel.cityId.getValue();
                    if (city_id != 0) {
                        mListAddressAdapter.changeCheckedState(city_id);
                        mRecyclerViewAddress.scrollToPosition(mListAddressAdapter.getIndexFromList(city_id));
                    }
                }
            }
        });
        //RecyclerView
        mRecyclerViewAddress = mRootView.findViewById(R.id.recycler_view_select_address_city);
        mRecyclerViewAddress.setLayoutManager(new LinearLayoutManager(getContext()));

        mListAddressAdapter = new AddressListAdapter();
        mRecyclerViewAddress.setAdapter(mListAddressAdapter);

        //EditText Search
        EditText txt_select_store_search_city = mRootView.findViewById(R.id.txt_select_store_search_city);
        txt_select_store_search_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        //Set OnListener for Recyclerview
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide keyboard Search
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();

                AddressCity item = mListAddressAdapter.mListCity.get(position);
                int old_id = mAddressViewModel.cityId.getValue();
                if (mCallBackListener != null) {
                    if (old_id != item.getId()) {
                        //Set new address: new city -> delete old district & ward
                        mAddressViewModel.cityId.setValue(item.getId());
                        mAddressViewModel.districtId.setValue(0);
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

    private void filter(String keySearch) {
        List<AddressCity> filterList = new ArrayList<>();
        for (AddressCity item : mListCity) {
            if (item.getName().toLowerCase().contains(keySearch.toLowerCase())) {
                filterList.add(item);
            }
        }
        mListAddressAdapter.filterList(filterList);
    }
}
