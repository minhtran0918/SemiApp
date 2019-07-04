package org.semi.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.semi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkCollectionFragment extends Fragment {

    private static BookmarkCollectionFragment mInstance;

    public static BookmarkCollectionFragment getInstance() {
        if (mInstance == null) {
            mInstance = new BookmarkCollectionFragment();
        }
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark_collection, container, false);
    }

}
