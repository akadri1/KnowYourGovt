package com.akshathakadri.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private static boolean running = false;
    private static String locationDisplay;
    private static String postcode;
    private Locator locator;

    List<Official> officials = new ArrayList<Official>();
    GovtAdapter govtAdapter;

    private RecyclerView recyclerView; // Layout's recycler view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        govtAdapter = new GovtAdapter(officials, this);

        recyclerView.setAdapter(govtAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(doNetCheck()) {
            if(locationDisplay != null && !locationDisplay.isEmpty()) {
                String name = postcode==null| postcode.isEmpty()?locationDisplay:postcode;
                doAddress(0, 0, name);
            } else {
                locator = new Locator(this);
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Log.d(TAG, "onClick: Fetching details for: "+officials.get(pos).getName());
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("official",officials.get(pos));
        intent.putExtra("location",locationDisplay);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Toast.makeText(this, "Getting about page", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.mlocation:
                Toast.makeText(this, "Change location", Toast.LENGTH_SHORT).show();
                changeLocation(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData(String name) {
        if(running){
            //Toast.makeText(this, "getData: Process already running. Please try later ", Toast.LENGTH_SHORT).show();
            return;
        }
        loadData(name);
    }

    private void loadData(String name) {
        if(doNetCheck()) {
            AsyncLoader asyncTask = new AsyncLoader(this);
            asyncTask.responseHandler = this;
            running = true;
            asyncTask.execute(name);
        }
    }
    public void processFinish(GovtObject govtObject) {
        officials.clear();
        running = false;
        if(govtObject !=null && govtObject.getOfficialList()!=null && govtObject.getLocation()!=null) {
            officials.addAll(govtObject.getOfficialList());
            govtAdapter.notifyDataSetChanged();
            this.locationDisplay = govtObject.getLocation();
            ((TextView) findViewById(R.id.location)).setText(this.locationDisplay);
        } else
            Toast.makeText(this, "Could not load the data!", Toast.LENGTH_SHORT).show();

    }

    public void changeLocation(View v) {
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);

        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doAddress(0, 0, et.getText().toString());
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Enter a City, State or a ZipCode:");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setData(double lat, double lon) {
        Log.d(TAG, "setData: Lat: " + lat + ", Lon: " + lon);
        doAddress(lat, lon, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: CALL: " + permissions.length);
        Log.d(TAG, "onRequestPermissionsResult: PERM RESULT RECEIVED");

        if (requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
                        locator.setUpLocationManager();
                        locator.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }

    private void doAddress(double latitude, double longitude, String name) {

        Log.d(TAG, "doAddress: Lat: " + latitude + ", Lon: " + longitude);

        List<Address> addresses;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                Log.d(TAG, "doAddress: Getting address now");

                if(name != null && !name.trim().isEmpty()) {
                    addresses = geocoder.getFromLocationName(name, 1);
                    if(addresses !=null && addresses.size()>0) {
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    } else {
                        Toast.makeText(this, "Invalid location!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                Log.d(TAG, "doAddress: Num addresses: " + addresses.size());

                Address ad = addresses.get(0);
                Log.d(TAG, "doLocation: " + ad);
                this.postcode =ad.getPostalCode();
                if(this.postcode != null) {
                    getData(this.postcode);
                    return;
                } else {
                    getData(ad.getAddressLine(ad.getMaxAddressLineIndex()));
                    return;
                }

            } catch (IOException e) {
                Log.d(TAG, "doAddress: " + e.getMessage());

            }
            Toast.makeText(this, "GeoCoder service is slow - please wait", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "GeoCoder service timed out - please try again", Toast.LENGTH_LONG).show();
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    public void noButtonDialogue() {
        // noSymbolDialogue dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/ loaded without an internet connection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean doNetCheck(){
        if(isConnected()) {
            return true;
        } else {
            noButtonDialogue();
            ((TextView)findViewById(R.id.location)).setText("No data for Location");
            return false;
        }
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager==null) return false;
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info !=null && info.isConnectedOrConnecting());
    }
}
