package org.semi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class None_PlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_none__place);
    }
/*
    private void init() {
        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.place_api_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place none_place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", none_place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
