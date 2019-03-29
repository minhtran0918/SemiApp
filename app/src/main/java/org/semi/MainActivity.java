package org.semi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent toHomeIntent = new Intent(this, HomeActivity.class);
        startActivity(toHomeIntent);
        finish();
    }
}
