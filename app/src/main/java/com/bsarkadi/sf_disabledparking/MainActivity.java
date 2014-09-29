package com.bsarkadi.sf_disabledparking;

/**
 *
 * Created by Balazs Sarkadi for the Hired SF Open Data Coding Contest, Sep 2014.
 *
 *
 * An app to help you find disabled parking places in San Francisco based on your location.
 * The needed space length for your car and the max distance should be set for a search.
 * Results are sorted by distance. Click the links in the list items and Google Maps opens
 * with a marker at the coordinates.
 *
 * Dataset used for project: Disabled Parking
 *
 */


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;


import android.location.Location;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class MainActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener
{
    //Declare variables we'll need for getting location
    LocationClient mLocationClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;






    //Declare variables for your coordinates
    double yourLat =0, yourLng=0;

    public boolean isLocationOn(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    //Check if user is online
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Get find button
        Button btnFind = (Button) findViewById(R.id.btnFind);


        //Warn user to go online
        if (!isOnline()) { Toast.makeText(getApplicationContext(), "Please connect to the Internet!", Toast.LENGTH_LONG).show();}

        //Create location request
        mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 20 seconds
        mLocationRequest.setInterval(1000 * 20);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(10000);


        //Set listener for Find button
        btnFind.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                EditText spacelength = (EditText) findViewById(R.id.spacelgt);
                String space = spacelength.getText().toString();

                //Get the radius we are searching in
                EditText distance = (EditText) findViewById(R.id.distance);
                String radius = distance.getText().toString();

                //Turn radius into integer and convert it to meters
                int radius_mile=0;
                int spaceneeded =0;

                try {
                    spaceneeded = Integer.parseInt(space);
                    radius_mile = Integer.parseInt(radius);
                    radius_mile = (int)Math.round(radius_mile * 1609.344);
                }
                catch(Exception e) {
                    //Error message for no distance set
                    Toast.makeText(getApplicationContext(), "Please set both parameters!", Toast.LENGTH_LONG).show();
                }

                boolean flag = false;


                // making SODA request
                makeSODARequest(spaceneeded,radius_mile, flag);

            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // connect the client.
        mLocationClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        // disconnecting the client invalidates it.
        mLocationClient.disconnect();
    }


    //Warn user to turn on Location services if not connected
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "No location data available", Toast.LENGTH_SHORT).show();
    }

    // GooglePlayServicesClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle arg0) {



        if(mLocationClient != null){
            // get location
            mCurrentLocation = mLocationClient.getLastLocation();
            try{

                yourLat = mCurrentLocation.getLatitude();
                yourLng = mCurrentLocation.getLongitude();

            }catch(NullPointerException npe){
                Toast.makeText(this, "No location data available", Toast.LENGTH_SHORT).show();


            }
        }

    }
    @Override
    public void onDisconnected() {
        Toast.makeText(this, "No Location", Toast.LENGTH_SHORT).show();

    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = mLocationClient.getLastLocation();

        yourLat = mCurrentLocation.getLatitude();
        yourLng = mCurrentLocation.getLongitude();
    }

    //Calculate distance between you and the parking place in miles
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        //get distance in feet
        double dist = earthRadius * c;

        return (double)Math.round(dist*100)/100;


    }


    private static String TAG = MainActivity.class.getSimpleName();
    private Button btnFind;



    // temporary string to show the parsed response
    private String jsonResponse;



    //Query the SODA API for parking place data
    public void makeSODARequest(int space, int radius, final boolean flag) {




        final int lspace = space;





        //Create URL for SODA SoQL query
        String urlSODAQuery =  "http://data.sfgov.org/resource/wc6f-brai.json?$where=within_circle(location,%20" + Double.toString(yourLat) +",%20" + Double.toString(yourLng) +",%20" + radius +")";


        //Make SODA query to get JSON Array
        JsonArrayRequest req = new JsonArrayRequest(urlSODAQuery,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        //local flag showing if there are results received. Set to false with each call.
                        boolean loc_flag = flag;





                        try {

                            //Initialize local variables
                            jsonResponse = "";


                            double lat2 = 0;
                            double lng2 = 0;
                            double dist = 0;
                            String address ="";
                            String locationdesc = "";
                            int spaceleng = 0;
                            ArrayList arlist = new ArrayList();

                            // Loop through each json object in API response
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject place = (JSONObject) response.get(i);
                                JSONObject location = place.getJSONObject("location");
                                //Avoid errors if value is missing
                                try{
                                 spaceleng = place.getInt("spaceleng");
                                 address = place.getString("address");
                                 locationdesc = place.getString("sitedetail");

                                 lat2 = location.getDouble("latitude");
                                 lng2 = location.getDouble("longitude");




                                }
                                 catch (Exception e) {
                                     //No need for an error message here

                                 }



                                //Get distance from truck
                                if (lat2!= 0 && lng2 != 0){
                                  dist = distFrom(yourLat,yourLng,lat2,lng2);}

                                //Create geo uri for Google Maps
                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f", lat2,lng2,lat2,lng2);


                                // "<a href=\"" + uri + "\">"
                                //Create response string
                                if(spaceleng >= lspace ){
                                    jsonResponse = "<br/>" + "<a href=\"" + uri + "\">" + address + "</a>" + "<br/>" + locationdesc + "<br/>" + "Distance: " + dist + " miles"+ "<br/>" + "Space length: " + spaceleng+ "<br/>";
                                loc_flag = true;

                                //Create result objects in every iteration and put them in an arraylist
                                Result record = new Result(jsonResponse,dist);
                                //record.setResult(jsonResponse,dist);
                                if (!arlist.contains(record)){
                                    arlist.add(record);}


                                }
                            }


                            //sort the results
                            Collections.sort(arlist, new Compare());

                            //No results, request user to search again
                            if (!loc_flag) {
                                if (!isOnline()) { Toast.makeText(getApplicationContext(), "Please connect to the Internet!", Toast.LENGTH_LONG).show();}
                                else if (!isLocationOn()) { Toast.makeText(getApplicationContext(), "Please turn on Location!", Toast.LENGTH_LONG).show();}
                                else {Toast.makeText(getApplicationContext(), "Nothing found. Try a new search!", Toast.LENGTH_LONG).show();}}

                            else {sendMessage(arlist);}




                        } catch (JSONException e) {

                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            //Print an error message if request fails due to lack of Internet connection
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "No Internet connection!", Toast.LENGTH_SHORT).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    public void sendMessage(ArrayList arlist) {
        Intent intent = new Intent(this, MyListActivity.class);
        intent.putExtra("array_list", arlist);
        startActivity(intent);


    }

}