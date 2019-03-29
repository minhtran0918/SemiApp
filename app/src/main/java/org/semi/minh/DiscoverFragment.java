package org.semi.minh;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.semi.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {
    View mViewRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mViewRoot == null){
            mViewRoot = inflater.inflate(R.layout.minh_fragment_discover, container, false);
        }
        return mViewRoot;
    }
}
