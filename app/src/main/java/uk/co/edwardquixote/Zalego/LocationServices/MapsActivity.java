package uk.co.edwardquixote.Zalego.LocationServices;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity {

    private double dLatitude;
    private double dLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initializeViews();

    }


    private void initializeViews() {

        Intent inLaunchIntent = this.getIntent();
        Bundle bundleLocation = inLaunchIntent.getBundleExtra("KEY_MAPS_EXTRA");
        if (bundleLocation != null) {

            dLatitude = bundleLocation.getDouble("KEY_LATITUDE");
            dLongitude = bundleLocation.getDouble("KEY_LONGITUDE");

        }


        final SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.fragmentMaps);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {

                LatLng latlngNairobi = new LatLng(-1.28333, 36.81667);      //  Latitude & Longitude of Nairobi City

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latlngNairobi);
                markerOptions.title("Nairobi City");

                googleMap.addMarker(markerOptions);


                LatLng latlngUserLocation = new LatLng(dLatitude, dLongitude);

                MarkerOptions userMarkerOptions = new MarkerOptions();
                userMarkerOptions.title("User Location");
                userMarkerOptions.position(latlngUserLocation);

                googleMap.addMarker(userMarkerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlngUserLocation));

            }

        });

    }

}
