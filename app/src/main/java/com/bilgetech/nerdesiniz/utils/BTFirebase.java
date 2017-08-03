package com.bilgetech.nerdesiniz.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.bilgetech.nerdesiniz.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static com.bilgetech.nerdesiniz.utils.Utils.map2log;

/**
 * Created by DamraKOC on 30.7.2017.
 */

/**
 * BilgiTech Firebase Utils
 */

public class BTFirebase {

    private static final String TAG = BTFirebase.class.getSimpleName();
    private static final String KEY_ROOMS = "rooms";

    // Context
    private static Context _context;

    private static BTFirebase instance;

    private static FirebaseDatabase database;

    private static Object LOCK = BTFirebase.class;

    public static BTFirebase getInstance(Context context) {
        if (instance == null && _context == null) {
            synchronized (LOCK) {
                if (instance == null && _context == null) {
                    _context = context;
                    instance = new BTFirebase();
                }
            }
        }
        return instance;
    }

    // Constructor
    private BTFirebase() {

        Log.d(TAG, "Initializing BTFirebase ");

        database = FirebaseDatabase.getInstance();

    }

    public static void logdRefRec(DatabaseReference dbRef){

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                map2log((Map) dataSnapshot.getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        };

        dbRef.addValueEventListener(listener);

    }

    public static DatabaseReference getRooms(){

       return database.getReference(KEY_ROOMS);

    }

    public static DatabaseReference getRoom(String roomNumber){

        return database.getReference(KEY_ROOMS).child(roomNumber);

    }

    public static void registerUserToRoom(String roomNumber, Person person){

        Log.d(TAG, "registerUserToRoom: roomNumber:"+roomNumber+":"+ person.getDeviceId());

        BTFirebase.getRoom(roomNumber).push().setValue(person);

    }

    public static void dropUserFromRoom(final String roomNumber, final String deviceId){

        Query filteredData = BTFirebase.getRoom(roomNumber).orderByChild("deviceId").equalTo(deviceId);

        filteredData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{

                    //logdRefRec(dataSnapshot.getRef()); // filtered data

                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                        snapshot.getRef().removeValue();

                        Log.d(TAG, "dropUserFromRoom: roomNumber:"+roomNumber+":"+ deviceId);

                    }

                }catch (Exception e){

                    Log.d(TAG, "onDataChange: ",e);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }

        });

    }

    public static void setCurrentUserLocation(String roomNumber, final Location location){

        Query filteredData = BTFirebase.getRoom(roomNumber).orderByChild("deviceId")
                .limitToFirst(1).equalTo(DeviceInfo.getUDID(_context));

        filteredData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{

                    //logdRefRec(dataSnapshot.getRef()); // filtered data

                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                        snapshot.getRef().child("completed").setValue(true);
                        snapshot.getRef().child("location").getRef().child("lat").setValue(location.getLatitude());
                        snapshot.getRef().child("location").getRef().child("lng").setValue(location.getLongitude());

                    }

                }catch (Exception e){

                    Log.d(TAG, "onDataChange: ",e);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }

        });


    }

}
