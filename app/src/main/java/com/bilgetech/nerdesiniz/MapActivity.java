package com.bilgetech.nerdesiniz;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilgetech.nerdesiniz.utils.BTFirebase;
import com.bilgetech.nerdesiniz.utils.DeviceInfo;
import com.bilgetech.nerdesiniz.utils.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * TODO: implement
 */
public class MapActivity extends LocationAwareActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    private AbsActivity absActivity; // theme
    private String roomNumber = "";
    private String userName = "";


    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */
    private Marker mLastSelectedMarker;

    private final Map<String, Marker> mMarkers = new ConcurrentHashMap<String, Marker >();

    private GoogleMap mMap;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */

    @Override
    protected void onEnableLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){
            //Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }else if (mMap != null) {

            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onLocationUpdated(final Task<Location> location) {
        //Log.d(TAG, "onLocationUpdated:"+ location.getLatitude() + ", " + location.getLongitude());

        try {

            if(location!=null && location.getResult()!=null)
                handleNewLocation(location.getResult());

        }catch (Exception e){

            Log.d(TAG, "onLocationUpdated: "+e.getLocalizedMessage(),e);

        }

    }

    @Override
    protected void onAddressFound(String address) {
        Log.d(TAG, "onAddressFound:" + address);

    }

    @BindView(R.id.ivUserColor)
    ImageView ivUserColor;

    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @BindView(R.id.tvDistance)
    TextView tvDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userName = !TextUtils.isEmpty(extras.getString("userName")) ?  extras.getString("userName") : "";
            roomNumber = !TextUtils.isEmpty(extras.getString("roomNumber")) ?  extras.getString("roomNumber") : "";
        }

        BTFirebase.getInstance(this);

        absActivity = new AbsActivity(this) {
            @Override
            void loadPropertyData() {
                ivUserColor.setBackgroundColor( getThemeColor() );
                tvUserName.setText(userName);
            }
        };

        absActivity.setThemeConfig();

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        ButterKnife.bind(this); // inject view

        mapFragment.getMapAsync(this);

        absActivity.loadPropertyData();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.setOnMyLocationButtonClickListener(this);

        onEnableLocation();

        childEventListener();

        loadAllRoomUser();

    }

    private void loadAllRoomUser() {

        BTFirebase.getRoom(roomNumber)
                .orderByChild("completed")
                .equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                try {

                    for (DataSnapshot ds: dataSnapshot.getChildren()) {

                        Person person = ds.getValue(Person.class);

                        if (person!=null && person.isCompleted() && person.getLocation()!=null){

                            LatLng newLocation = new LatLng(
                                    person.getLocation().getLatitude(),
                                    person.getLocation().getLongitude()
                            );

                            MarkerOptions options = new MarkerOptions()
                                    .position(newLocation)
                                    .icon(BitmapDescriptorFactory.defaultMarker())
                                    .title(person.getName());


                            if (person.getColor().matches("^[0-9a-fA-F]+$")){

                                options.icon(getMarkerIcon(person.getColor()));

                            }

                            addMarker(person.getDeviceId(),options);

                        }

                    }

                } catch (IllegalArgumentException e) {


                } catch (Exception e){

                    Log.d(TAG, "onDataChange: ",e);

                } finally {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void childEventListener() {

        BTFirebase.getRoom(roomNumber)
                .orderByChild("completed")
                .equalTo(true).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                onMapNewUserInteraction(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                onMapNewUserInteraction(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                try{

                    Log.d(TAG, "onChildRemoved: "+dataSnapshot.getValue());

                    String deviceId = dataSnapshot.getRef().child("deviceId").toString();

                    if (deviceId!=null)
                        removeMarker(deviceId);


                }catch (Exception e){

                    Log.d(TAG, "onChildRemoved: ",e);

                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {

        BTFirebase.dropUserFromRoom(roomNumber, DeviceInfo.getUDID(this));

        super.onPause();

    }



    private void addMarker(final String deviceId, final MarkerOptions markerOptions) {

        Marker marker;

        if(mMarkers.get(deviceId)==null){

            marker = mMap.addMarker(markerOptions);

            mMarkers.put(deviceId, marker);

        }else{

            marker = mMarkers.get(deviceId);

            if (markerOptions!=null && markerOptions.getPosition()!=null)
                marker.setPosition(markerOptions.getPosition());

        }
    }

    private void removeMarker(final String deviceId) {

        if(mMarkers.get(deviceId)!=null){

            final Marker marker = mMarkers.get(deviceId);

            mMarkers.remove(deviceId);

            marker.remove();

        }

    }

    private void onMapNewUserInteraction(DataSnapshot dataSnapshot) {

        try{

            Person person = dataSnapshot.getValue(Person.class);

            if(person!=null && person.isCompleted() && person.getLocation()!=null){

                LatLng newLocation = new LatLng(
                    person.getLocation().getLatitude(),
                    person.getLocation().getLongitude()
                );

                Log.d(TAG, "onChildAdded: "+person.isCompleted());

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(newLocation)
                        .title(person.getName());


                MarkerOptions options = new MarkerOptions()
                        .position(newLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker())
                        .title(person.getName());

                if (person.getColor().matches("^[0-9a-fA-F]+$")){

                    options.icon(getMarkerIcon(person.getColor()));

                }

                addMarker(person.getDeviceId(),markerOptions);

            }

        }catch (Exception e){

            Log.d(TAG, "onChildAdded: ",e);
        }
    }

    private void handleNewLocation(final Location location) {

        if(location==null) return;

        // Report to the firebase that the location was updated

        BTFirebase.setCurrentUserLocation(roomNumber,location);

        BTFirebase.logdRefRec(BTFirebase.getRooms().getRef()); // log all room data
        BTFirebase.logdRefRec(BTFirebase.getRoom(roomNumber)); // log current room data

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        String hexColor = Integer.toHexString(absActivity.getThemeColor());

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(getMarkerIcon("#"+hexColor ))
                .title(userName);

        addMarker(DeviceInfo.getUDID(this),options);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        mLastSelectedMarker = marker;

        if(mLastLocation!=null){

            final String distance = getDistance(
                    mLastLocation.getLatitude(),mLastLocation.getLatitude(),
                    marker.getPosition().latitude,marker.getPosition().longitude);

            tvDistance.setText(distance);

        }

        return false;
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */

    @Override
    public void onConnectionSuspended(int i) {
        // Display the connection status
        //Toast.makeText(this, "Disconnected. Please re-connect.",
        //        Toast.LENGTH_SHORT).show();
        isConnected = false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        return false;
    }
}
