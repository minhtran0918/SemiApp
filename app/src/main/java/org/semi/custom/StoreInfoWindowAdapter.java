package org.semi.custom;

import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.semi.MyApp;
import org.semi.R;
import org.semi.object.Store;

public class StoreInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private ThreadLocal<Bitmap> bitmapHolder;

    public StoreInfoWindowAdapter(ThreadLocal<Bitmap> bitmapHolder) {
        this.bitmapHolder = bitmapHolder;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        final View view = LayoutInflater.from(
                MyApp.getContext()).inflate(R.layout.store_info_window, null);
        final AppCompatImageView imageImageView = view.findViewById(R.id.imageImageView);
        final TextView titleTextView = view.findViewById(R.id.titleTextView);
        final TextView addressTextView = view.findViewById(R.id.addressTextView);
        final Store store = (Store) marker.getTag();
        titleTextView.setText(store.getTitle());
        addressTextView.setText(store.getAddress().toString());
        Bitmap bitmap = bitmapHolder.get();
        if(bitmap != null) {
            imageImageView.setImageBitmap(bitmap);
            bitmapHolder.set(null);
        }
        return view;
    }
}
