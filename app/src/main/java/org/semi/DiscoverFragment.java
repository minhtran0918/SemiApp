package org.semi;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {

    private static DiscoverFragment mDiscoverFragmentInstance;
    private View mRootView;

    public static DiscoverFragment getInstance(){
        if(mDiscoverFragmentInstance == null){
            mDiscoverFragmentInstance = new DiscoverFragment();
        }
        return mDiscoverFragmentInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_discover, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){

    }

}
