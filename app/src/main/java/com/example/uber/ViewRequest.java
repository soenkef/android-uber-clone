package com.example.uber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ViewRequest extends AppCompatActivity implements LocationListener {

    ListView listView;
    ArrayList<String> listViewContent;
    ArrayAdapter arrayAdapter;
    LocationManager locationManager;
    String provider;
    ArrayList<String> usernames;
    ArrayList<Double> latitudes;
    ArrayList<Double> longitudes;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listViewContent = new ArrayList<String>();
        usernames = new ArrayList<String>();
        latitudes = new ArrayList<Double>();
        longitudes = new ArrayList<Double>();
        listViewContent.add("Test");

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.listViewContent);
        listView.setAdapter(arrayAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(provider, 400, 1, this);

        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            updateLocation(location);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              Intent i = new Intent(getApplicationContext(), ViewRiderLocation.class);
              i.putExtra("username", usernames.get(position));
              i.putExtra("latitude", latitudes.get(position));
              i.putExtra("longitude", longitudes.get(position));
              i.putExtra("userLatitude", location.getLatitude());
              i.putExtra("userLongitude", location.getLongitude());
              startActivity(i);
              //Log.i("MyApp", usernames.get(position) + latitudes.get(position).toString() + longitudes.get(position).toString() );

            }
        });

    }

    public void updateLocation(Location location) {

        final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        ParseUser.getCurrentUser().put("location", userLocation);
        ParseUser.getCurrentUser().saveInBackground();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Requests");
        query.whereDoesNotExist("driverUserName");
        query.whereNear("requesterLocation", userLocation);
        query.setLimit(10);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        listViewContent.clear();
                        usernames.clear();
                        latitudes.clear();
                        longitudes.clear();

                        for (ParseObject object : objects) {

                            Double distanceInKilometers = userLocation.distanceInKilometersTo((ParseGeoPoint) object.get("requesterLocation"));

                            Double distanceOneDP = (double) Math.round(distanceInKilometers * 10 / 10);
                            listViewContent.add(String.valueOf(distanceOneDP) + " km");
                            usernames.add(object.getString("requesterUserName"));
                            latitudes.add(object.getParseGeoPoint("requesterLocation").getLatitude());
                            longitudes.add(object.getParseGeoPoint("requesterLocation").getLongitude());

                        }

                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        updateLocation(location);

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

}
