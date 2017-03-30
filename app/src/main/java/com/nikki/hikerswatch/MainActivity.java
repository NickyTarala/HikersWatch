package com.nikki.hikerswatch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize location manager and listener class
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d("Location ", location.toString());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //check the version of SDK first and then locate
        if (Build.VERSION.SDK_INT < 23) {

            startListening();
        } else {

            //self check if the permission is given in the Manifest.xml
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                //if permissions are declared, requesting for location using GPS PROVIDER
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {

                    //if no location details are provided, we are updating it
                    updateLocationInfo(location);

                }
            }
        }
    }

    // method to check if the permission is declared , if yes to access the location using LOCATION_SERVICE
    public void startListening() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }


    // to show lat, long, accuracy, altitude of the given location
    public void updateLocationInfo(Location location) {

        Log.e("Location", location.toString());

        TextView tvLat, tvLong, tvAlt, tvAcc;

        tvLat = (TextView) findViewById(R.id.tv_lat);
        tvLong = (TextView) findViewById(R.id.tv_long);
        tvAlt = (TextView) findViewById(R.id.tv_alt);
        tvAcc = (TextView) findViewById(R.id.tv_acc);

        tvLat.setText("Latitude : " + location.getLatitude());
        tvLong.setText("Longitude : " + location.getLongitude());
        tvAlt.setText("Altitude : " + location.getAltitude());
        tvAcc.setText("Accuracy : " + location.getAccuracy());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            String address = "Could not find address";

            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (listAddresses != null && listAddresses.size() > 0) {

                Log.i("PlaceInfo", listAddresses.get(0).toString());

                address = "Address: \n";

                if (listAddresses.get(0).getSubThoroughfare() != null) {

                    address += listAddresses.get(0).getSubThoroughfare() + " ";

                }

                if (listAddresses.get(0).getThoroughfare() != null) {

                    address += listAddresses.get(0).getThoroughfare() + "\n";

                }

                if (listAddresses.get(0).getLocality() != null) {

                    address += listAddresses.get(0).getLocality() + "\n";

                }

                if (listAddresses.get(0).getPostalCode() != null) {

                    address += listAddresses.get(0).getPostalCode() + "\n";

                }

                if (listAddresses.get(0).getCountryName() != null) {

                    address += listAddresses.get(0).getCountryName() + "\n";

                }

            }

            TextView addressTextView = (TextView) findViewById(R.id.tv_address);

            addressTextView.setText(address);


        } catch (IOException e) {

            e.printStackTrace();

        }

    }


    //if permission was not declared in Manifest.xml, method to request the required permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               startListening();
            }
        }
    }
}
