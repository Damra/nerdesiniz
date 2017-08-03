package com.bilgetech.nerdesiniz;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.bilgetech.nerdesiniz.utils.BTFirebase;
import com.bilgetech.nerdesiniz.utils.DeviceInfo;
import com.bilgetech.nerdesiniz.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * TODO: implement
 */



public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Context context;

    private AbsActivity absActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = getApplicationContext();

        if (getSupportActionBar()!=null)
            getSupportActionBar().hide(); // action bar gizlendi

        BTFirebase.getInstance(context);

        //BTFirebase.getRooms().removeValue(); // tüm odaları temizle

        absActivity = new AbsActivity(this) {
            @Override
            void loadPropertyData() {

                CompoundButton cb = ((CompoundButton) findViewById(SessionManager.getInstance(context).getThemeRBResId()));

                if (cb!=null)
                    cb.setChecked(true);

                clMain.setBackgroundColor( getThemeColor() );

                btnSubmit.setBackgroundColor( getThemeColor() );

            }
        };

        absActivity.setThemeConfig();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this); // inject view

        absActivity.loadPropertyData();

    }

    @BindView(R.id.clMain)
    public ConstraintLayout clMain;

    @BindView(R.id.etUserName)
    public EditText etUserName;

    @BindView(R.id.etRoomNumber)
    public EditText etRoomNumber;

    @BindView(R.id.rgColor)
    public RadioGroup rgColor;

    @BindView(R.id.btnSubmit)
    public Button btnSubmit;

    @OnClick(R.id.btnSubmit)
    public void doLogin(){

        if(etUserName.getText().toString().trim().equals("")){

            Snackbar.make(findViewById(R.id.clMain), getResources().getString(R.string.str_required_user_name), Snackbar.LENGTH_SHORT).show();

            return;

        }

        if(etRoomNumber.getText().toString().trim().equals("")){

            Snackbar.make(findViewById(R.id.clMain),getResources().getString(R.string.str_required_room_number), Snackbar.LENGTH_SHORT).show();

            return;

        }

/*
        Person person = new Person();

        person.setDeviceId(DeviceInfo.getUDID(this));
        person.setColor(absActivity.getHexColor());
        person.setName(etUserName.getText().toString());

        BTFirebase.registerUserToRoom(etRoomNumber.getText().toString(),person);
*/

        createActivity();

        // odaya kayıt

    }

    @OnCheckedChanged({R.id.rbRed, R.id.rbPurple, R.id.rbBlue, R.id.rbGreen})
    public void radioButtonCheckChanged(CompoundButton radioButton, boolean checked){

        final int themeId;

        int oldId = SessionManager.getInstance(context).getThemeStyle();

        if(checked) {

            switch (radioButton.getId()){
                default:
                case R.id.rbRed: themeId = R.style.PrimaryColorRed; break;
                case R.id.rbBlue: themeId = R.style.PrimaryColorBlue; break;
                case R.id.rbGreen: themeId = R.style.PrimaryColorGreen; break;
                case R.id.rbPurple: themeId = R.style.PrimaryColorPurple; break;
            }

            Log.d(TAG, "radioButtonCheckChanged: oldThemeId: "+oldId +", themeId:"+themeId +",  isEqual:"+ (oldId==themeId));

            SessionManager.getInstance(context)
                .setThemeStyle(themeId)
                .setThemeRBResId(radioButton.getId());

            Log.d(TAG, "radioButtonCheckChanged: registered themeId: "+ SessionManager.getInstance(context).getThemeStyle());

            Log.i(TAG , "radioButtonCheckChanged: Retrieved themeId: "+  Integer.toHexString(themeId));

            absActivity.setThemeConfig();

            absActivity.loadPropertyData();

        }

        // temayı değiştir.

    }


    private void createActivity(){

        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("roomNumber", etRoomNumber.getText().toString());
        intent.putExtra("userName", etUserName.getText().toString());

        TaskStackBuilder sBuilder = TaskStackBuilder.create(MainActivity.this);
        sBuilder.addNextIntent(intent);
        sBuilder.startActivities();

    }

}
