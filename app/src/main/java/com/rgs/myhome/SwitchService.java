package com.rgs.myhome;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SwitchService extends IntentService {
    public static final String ACTION1 = "Light1";
    public static final String ACTION2 = "Light2";
    public static final String ACTION3 = "Fan";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate() {

        sharedPreferences = getApplicationContext().getSharedPreferences("sp", 0);
        editor = sharedPreferences.edit();
        super.onCreate();
    }

    public SwitchService() {
        super("SwitchService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(intent.getStringExtra("uid"));

        if (ACTION1.equals(action)) {

            Log.d("3114", "onHandleIntent: ..........................."+ sharedPreferences.getInt("L1", 0));

            if (sharedPreferences.getInt("L1", 0) == 0) {

                databaseReference.child("L1").setValue(1);
                editor.putInt("L1" , 1);
                Log.d("3114", "Status: on");

            } else if (sharedPreferences.getInt("L1", 0) == 1) {

                databaseReference.child("L1").setValue(0);
                Log.d("3114", "Status: off");
                editor.putInt("L1" , 0);

            }
            editor.apply();


        } else if (ACTION2.equals(action)) {

            Log.d("3114", "onHandleIntent: ..........................."+ sharedPreferences.getInt("L2", 0));

            if (sharedPreferences.getInt("L2", 0) == 0) {

                databaseReference.child("L2").setValue(1);
                editor.putInt("L2" , 1);
                Log.d("3114", "Status: on");

            } else if (sharedPreferences.getInt("L2", 0) == 1) {

                databaseReference.child("L2").setValue(0);
                Log.d("3114", "Status: off");
                editor.putInt("L2" , 0);

            }
            editor.apply();

        } else if (ACTION3.equals(action)) {

            Log.d("3114", "onHandleIntent: ..........................."+ sharedPreferences.getInt("F", 0));

            if (sharedPreferences.getInt("F", 0) == 0) {

                databaseReference.child("F").setValue(1);
                editor.putInt("F" , 1);
                Log.d("3114", "Status: on");

            } else if (sharedPreferences.getInt("F", 0) == 1) {

                databaseReference.child("F").setValue(0);
                Log.d("3114", "Status: off");
                editor.putInt("F" , 0);

            }
            editor.apply();

        } else {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }


}
