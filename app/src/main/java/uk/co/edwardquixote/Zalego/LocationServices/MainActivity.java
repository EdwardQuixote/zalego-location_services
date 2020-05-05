package uk.co.edwardquixote.Zalego.LocationServices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private TextView txtUserLocationValues;

    private FusedLocationProviderClient locationProviderClient;
    private LocationRequest locationRequest;

    private double dLatitude;
    private double dLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        initializeViews();

        createLocationRequest();

    }


    private void initializeViews() {

        txtUserLocationValues = (TextView) this.findViewById(R.id.txtUserLocationValues);

        Button btnGetLastKnownLocation = (Button) this.findViewById(R.id.btnGetLastKnownLocation);
        btnGetLastKnownLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                checkForAccessLocationPermissions();
                checkForWriteExternalStoragePermissions();

            }

        });

        Button btnStartMaps = (Button) this.findViewById(R.id.btnStartMaps);
        btnStartMaps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((dLatitude <= 0.000000) && (dLongitude <= 0.000000)) {

                    Toast.makeText(MainActivity.this, "Can't start Maps until we get your location!", Toast.LENGTH_LONG).show();

                } else {

                    Bundle bundleLocation = new Bundle();
                    bundleLocation.putDouble("KEY_LATITUDE", dLatitude);
                    bundleLocation.putDouble("KEY_LONGITUDE", dLongitude);

                    Intent inStartMaps = new Intent(MainActivity.this, MapsActivity.class);
                    inStartMaps.putExtra("KEY_MAPS_EXTRA", bundleLocation);
                    startActivity(inStartMaps);

                }


            }

        });

    }


    private void codeToGetUserLastKnownLocation() {

        Task<Location> taskLastKnownLocation = locationProviderClient.getLastLocation();
        taskLastKnownLocation.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {

                Log.e("MainActivity", "codeToGetUserLastKnownLocation() - taskLastKnownLocation - onSuccess() - location: " + location);

                if (location != null) {

                    double dLatitude = location.getLatitude();
                    double dLongitude = location.getLongitude();

                    String sLocation = "[" + dLatitude + ", " + dLongitude + "]";
                    txtUserLocationValues.setText(sLocation);

                }

            }

        });
        taskLastKnownLocation.addOnFailureListener(MainActivity.this, new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception ex) {

                Log.e("MainActivity", "codeToGetUserLastKnownLocation() - taskLastKnownLocation - onFailure() - ex: " + ex);

            }

        });

    }

    private boolean checkForAccessLocationPermissions() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(MainActivity.this, "We need this permission, to access your location.", Toast.LENGTH_LONG).show();

                return false;
            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 10101);

                return true;
            }

        } else {
            return true;
        }

    }

    private boolean checkForWriteExternalStoragePermissions() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(MainActivity.this, "We need this permission, to access your location.", Toast.LENGTH_LONG).show();

                return false;
            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20202);

                return true;
            }

        } else {
            return true;
        }

    }

    private void createLocationRequest() {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }


    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null) {

                Location userCurrentLocation = locationResult.getLocations().get(0);

                Log.e("MainActivity", "locationCallback - onLocationResult() - userCurrentLocation: " + userCurrentLocation);

                if (userCurrentLocation != null) {

                    dLatitude = userCurrentLocation.getLatitude();
                    dLongitude = userCurrentLocation.getLongitude();

                    String sLocation = "[" + dLatitude + ", " + dLongitude + "]";
                    txtUserLocationValues.setText(sLocation);

                }

            }

        }

    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case 10101:     //  Access Fine Location Permission Request Code

                if ((grantResults.length > 0)) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        codeToGetUserLastKnownLocation();

                    } else {

                        Toast.makeText(MainActivity.this, "We need this permission, to access your location.", Toast.LENGTH_LONG).show();

                    }

                }

                break;

            case 20202:     //  Write External Storage Permission Request Code

                Toast.makeText(MainActivity.this, "Write External Storage Permission Granted.", Toast.LENGTH_LONG).show();

                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    @Override
    protected void onPause() {
        super.onPause();

        locationProviderClient.removeLocationUpdates(locationCallback);

    }

}
