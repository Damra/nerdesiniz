package com.bilgetech.nerdesiniz;

/**
 * Created by DamraKOC on 30.7.2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.bilgetech.nerdesiniz.utils.SessionManager;


abstract class AbsActivity {

    final AppCompatActivity activity;
    final Context context;

    int themeId;
    int themeColor;
    String hexColor;

    abstract void loadPropertyData();

    public AbsActivity(AppCompatActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    public void setThemeConfig() {

        if (activity==null) return; // check nullptr

        themeId = SessionManager.getInstance(context).getThemeStyle();

        themeColor = context.getColor(currentThemeAttrResId(themeId,R.attr.colorAccent));

        hexColor = Integer.toHexString(themeColor);

        context.setTheme(themeId);

        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(themeColor));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor( themeColor );
        }

    }

    private int currentThemeAttrResId(int themeResId, int attr){

        TypedArray a = context.getTheme().obtainStyledAttributes(themeResId, new int[] { attr });

        return a.getResourceId(0, 0);

    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }
}
