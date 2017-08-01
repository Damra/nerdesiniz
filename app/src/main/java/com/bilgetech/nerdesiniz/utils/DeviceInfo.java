package com.bilgetech.nerdesiniz.utils;

/**
 * Created by DamraKOC on 3.9.2016.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import java.util.Locale;
import java.util.TimeZone;

public class DeviceInfo {

    public DeviceInfo() {
    }

    public static String getOSVersion() {
        return VERSION.RELEASE;
    }

    public static String getDevice() {
        return Build.MODEL;
    }

    public static String getResolution(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.heightPixels + "x" + metrics.widthPixels;
    }

    public static String getCarrier(Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService("phone");
        return manager.getNetworkOperatorName();
    }

    public static String getMCC(Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService("phone");
        return manager.getNetworkCountryIso();
    }

    public static String getLocale() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    public static String getTz() {
        TimeZone timezone = TimeZone.getDefault();
        return timezone.getID();
    }

    public static String getUDID(Context context) {
        @SuppressLint("HardwareIds") String udid = Secure.getString(context.getContentResolver(), "android_id");
        if(udid == null) {
            udid = "";
        }

        return udid;
    }

    public static String appVersion(Context context) {
        String result = "1.0";

        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException var3) {
            ;
        }

        return result;
    }
}