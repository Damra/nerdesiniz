package com.bilgetech.nerdesiniz.utils;

import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;

import java.util.List;
import java.util.Map;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by DamraKOC on 30.7.2017.
 */

public class Utils {

    public static final boolean LOG = true;

    public static final String TAG = Utils.class.getSimpleName();

    public final static void map2log(Map<String,Long> map){

        if(map!=null && map.entrySet()!=null){

            Log.d(TAG, "-------------BEGIN-------------------");
            Log.d(TAG, "map2log: elem size:"+ map.size());

            for (Map.Entry<String, Long> entry : map.entrySet()) {

                Log.d(TAG, "map2log: "+entry.getKey() + ": " + entry.getValue());

            }

            //Integer sum = map.values().stream().mapToInt(i-> i.intValue()).sum();

            Log.d(TAG, "-------------END-------------------");

        }

    }

    public final static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static String getGeocodedAddress(List<Address> geocodedAddresses) {
        if (geocodedAddresses == null || geocodedAddresses.size() == 0) {
            return null;
        }

        Address address = geocodedAddresses.get(0);

        StringBuffer sb = new StringBuffer();
        sb.append(address.getAddressLine(0));

        String ps = address.getPostalCode();
        if (ps != null && ps.length() > 0) {
            sb.append(" ");
            sb.append(ps);
        }

        String locality = address.getLocality();
        if (locality != null && locality.length() > 0) {
            sb.append(", ");
            sb.append(locality);
        }

        String countryName = address.getCountryName();
        if (countryName != null && countryName.length() > 0) {
            sb.append(" ");
            sb.append(countryName);
        }

        String result = sb.toString();
        if (result.length() == 0) {
            return null;
        }
        return result;
    }

    public static float convertDip(final Context ctx, int pixels) {
        Resources r = ctx.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, r.getDisplayMetrics());
    }

    public static void logNetworkError(Exception ex) {
        if (LOG) Log.e("NETWORK ERROR", ex.toString());
    }

    public static void logNetworkRequest(String url) {
        logNetworkRequest(url, "");
    }

    public static void logNetworkRequest(String url, String args) {
        if (LOG) Log.d("NETWORK REQUEST", url + (args.equals("")?" [ARGS] " + args:""));
    }

}
