package com.johnrthurlby.fourparkchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class locationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Circle circle;

    LocationManager locationManager;
    LocationListener locationListener;

    TextView textViewMsg;

    double lat1 = 0;
    double lng1 = 0;
    double lat2 = 0;
    double lng2 = 0;

    float [] dist = new float[1];

    Boolean insidePark    = false;

    String parkName;
    String userNamePrefix;

    Boolean cameraIntent  = false;

    Intent intent ;

    public void determineEpcot () {

        parkName = "Epcot";

        insidePark  = false;

        lat1 = 28.371;
        lng1 = -81.55;

        dist[0] = 0f;

        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);

        float outPut = dist[0] * 0.000621371192f;

        if (outPut < 0.5f) {

            insidePark  = true;

        }

        String outPutDist = Float.toString(outPut);

        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat1, lng1))
                .radius(700)
                .strokeColor(Color.RED));

    }

    public void determineMagic () {

        parkName = "Magic Kingdom";

        insidePark  = false;

        lat1 = 28.4177;
        lng1 = -81.5812;

        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);

        float outPut = dist[0] * 0.000621371192f;

        if (outPut < 1f) {

            insidePark  = true;

        }

        String outPutDist = Float.toString(outPut);

        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat1, lng1))
                .radius(700)
                .strokeColor(Color.RED));

    }

    public void determineAnimal () {

        parkName = "Animal Kingdom";

        insidePark    = false;

        lat1 = 28.358;
        lng1 = -81.59;

        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);

        float outPut = dist[0] * 0.000621371192f;

        if (outPut < 1f) {

           insidePark  = true;

        }
        String outPutDist = Float.toString(outPut);

        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat1, lng1))
                .radius(700)
                .strokeColor(Color.RED));

    }

    public void determineHolly () {

        insidePark    = false;

        parkName = "Hollywood Studios";

        lat1 = 28.357;
        lng1 = -81.5561;

        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);

        float outPut = dist[0] * 0.000621371192f;

        if (outPut < 1f) {

           insidePark  = true;

        }
        String outPutDist = Float.toString(outPut);

        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat1, lng1))
                .radius(700)
                .strokeColor(Color.RED));

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        textViewMsg = (TextView)findViewById(R.id.textViewMsg);

        intent = getIntent();

        userNamePrefix = intent.getStringExtra("USERNAME_PREFIX");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                mMap.clear();
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));

                lat2 = location.getLatitude();
                lng2 = location.getLongitude();

                if (!insidePark) {

                    determineEpcot();

                }

                if (!insidePark) {

                    determineMagic();

                }

                if (!insidePark) {

                    determineAnimal();

                }

                if (!insidePark) {

                    determineHolly();

                }

                if (!insidePark) {

                    cameraIntent = true;

                    textViewMsg.setText("You have to be in a Disney park to continue");

                    locationManager.removeUpdates(locationListener);

                    //Intent intent = new Intent(getApplicationContext(), MainActivity.class );

                    //startActivity(intent);

                    //finish();

                } else {

                    if (!cameraIntent) {

                      cameraIntent = true;

                      locationManager.removeUpdates(locationListener);

                      Log.i("Before camera activity",userNamePrefix );

                      Intent intent = new Intent(getApplicationContext(), CameraActivity.class );

                      intent.putExtra("PARK_NAME", parkName );
                      intent.putExtra("USERNAME_PREFIX", userNamePrefix );

                      startActivity(intent);

                      finish();

                    }

                }

                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            mMap.clear();
            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.rules_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.show_rules) {

            new AlertDialog.Builder(getApplicationContext())

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("4 Park Challenge Rules.")
                    .setMessage("Rules: Visit all 4 parks in the same day. Take a picture of an icon in each park. Do one ride in each park. Buy something that has the name of the park on the receipt. Use the app to take the pics. You will get a certification of completion sent to the email you signed up with. Hit Yes to continue")

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }

                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and go back to the main activity

                            SharedPrefManager.getInstance(getApplicationContext()).logout();

                            //starting the location activity
                            finish();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

                            startActivity(intent);

                        }})
                    .show();

            return true;
        }

        if (item.getItemId() == R.id.contact) {

            Intent intent = new Intent(getApplicationContext(), ContactActivity.class );

            intent.putExtra("USERNAME_PREFIX", userNamePrefix );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.help) {

            Intent intent = new Intent(getApplicationContext(), HelpActivity.class );

            intent.putExtra("USERNAME_PREFIX", userNamePrefix );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.logout) {

            SharedPrefManager.getInstance(getApplicationContext()).logout();

            finish();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

            startActivity(intent);

        }



        return true;

    }

}
