package org.semi.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.semi.MyApp;
import org.semi.R;

public class ErrorFragment extends Fragment {
    public static final String IMAGE_RESOURCE = "imageResource";
    public static final String MESSAGE = "message";

    public ErrorFragment() {
        // Required empty public constructor
    }

    public static ErrorFragment newInstance(@DrawableRes int imageResource, @StringRes int message){
        Bundle bundle = new Bundle();
        bundle.putInt(IMAGE_RESOURCE, imageResource);
        bundle.putString(MESSAGE, MyApp.getContext().getString(message));
        ErrorFragment fragment = new ErrorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        AppCompatImageView imageView = view.findViewById(R.id.errorImageView);
        TextView textView= view.findViewById(R.id.errorTextView);
        Bundle args = getArguments();
        int imageResource = args.getInt(IMAGE_RESOURCE);
        String message = args.getString(MESSAGE);
        imageView.setImageResource(imageResource);
        textView.setText(message);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
