package com.rgs.myhome;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngallazzi.highlightingview.HighlightingView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference databaseReference;
    String TAG = "LMain";
  //  private TextView deviceIp;
    private HighlightingView light1;
    private HighlightingView light2;
    String L1 = "", L2 = "", F = "";
    private HighlightingView fan,allon;
    String user_name, user_email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("sp", 0);
        editor = sharedPreferences.edit();
         databaseReference = FirebaseDatabase.getInstance().getReference(sharedPreferences.getString("uid", "..."));

        {
           // deviceIp = findViewById(R.id.device_ip);
            light1 = findViewById(R.id.light1);
            light2 = findViewById(R.id.light2);
            fan = findViewById(R.id.fan);
            allon = findViewById(R.id.allon);

        }

        {
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            navView = findViewById(R.id.nav_view);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navView.setNavigationItemSelectedListener(this);
            user_name = sharedPreferences.getString("name", "..");
            user_email = sharedPreferences.getString("email", "..");
        }





        getdata();
        light1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (light1.isHighlighted()){
                    light1.setHighlighted(false); light1.setBottomText("Off");
                    databaseReference.child("L1").setValue(0);
                    editor.putInt("L1", 0);
                } else {
                    light1.setHighlighted(true); light1.setBottomText("On");
                    databaseReference.child("L1").setValue(1);
                    editor.putInt("L1", 1);
                }
                editor.apply();

            }
        });

        light2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (light2.isHighlighted()){
                    light2.setHighlighted(false); light2.setBottomText("Off");
                    databaseReference.child("L2").setValue(0);
                } else {

                    light2.setHighlighted(true); light2.setBottomText("On");
                    databaseReference.child("L2").setValue(1);
                }

            }
        });

        fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fan.isHighlighted()){
                    fan.setHighlighted(false); fan.setBottomText("Off");
                    databaseReference.child("F").setValue(0);
                } else {

                    fan.setHighlighted(true); fan.setBottomText("On");
                    databaseReference.child("F").setValue(1);
                }

            }
        });

        // TODO: 02-06-2020 tey 1-1-1-1 format

        allon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allon.isHighlighted()){
                    allon.setHighlighted(false); allon.setBottomText("All Off");
                    databaseReference.child("F").setValue(0);
                    databaseReference.child("L1").setValue(0);
                    databaseReference.child("L2").setValue(0);
                    getdata();
//
//                    fan.setHighlighted(false); fan.setBottomText("Off");
//                    light2.setHighlighted(false); light2.setBottomText("Off");
//                    light1.setHighlighted(false); light1.setBottomText("Off");

                } else {

                    allon.setHighlighted(true); allon.setBottomText("All On");

//                    fan.setHighlighted(true); fan.setBottomText("On");
//                    light2.setHighlighted(true); light2.setBottomText("On");
//                    light1.setHighlighted(true); light1.setBottomText("On");

                    databaseReference.child("F").setValue(1);
                    databaseReference.child("L1").setValue(1);
                    databaseReference.child("L2").setValue(1);

                    getdata();
                }

            }
        });

        navviewdata();

createNotificationChannels();

    }


    public void navviewdata() {
        View nav_view = navView.getHeaderView(0);

        TextView nav_h_n = nav_view.findViewById(R.id.nav_header_name);
        TextView nav_h_s = nav_view.findViewById(R.id.nav_header_secondary);
        ImageView imageView = nav_view.findViewById(R.id.nav_header_imageView);
        RelativeLayout image = nav_view.findViewById(R.id.header_image);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.N) {
            image.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.bg_polygon));
        } else {
            image.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.material_bg_1));
        }

        if (sharedPreferences.getBoolean("pic_stat", false)) {
            Glide.with(this).load(sharedPreferences.getString("pic", "..")).into(imageView);
        } else {
            Glide.with(this).load(R.drawable.ic_male).into(imageView);
        }

        nav_h_n.setText(user_name);
        nav_h_s.setText(user_email);

        setTitle("Hey, " + user_name);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel("default", "Switch", NotificationManager.IMPORTANCE_DEFAULT));
        }



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        NotificationCompat.Action action1,action2,action3;
        {
            Intent intent = new Intent(this, SwitchService.class);
            intent.setAction(SwitchService.ACTION1);
            intent.putExtra("uid",sharedPreferences.getString("uid", "..."));
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            action1 = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "L1", pendingIntent).build();
        }

        {
            Intent intent = new Intent(this, SwitchService.class);
            intent.setAction(SwitchService.ACTION2);
            intent.putExtra("uid",sharedPreferences.getString("uid", "..."));
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            action2 = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "L2", pendingIntent).build();
        }

        {
            Intent intent = new Intent(this, SwitchService.class);
            intent.setAction(SwitchService.ACTION3);
            intent.putExtra("uid",sharedPreferences.getString("uid", "..."));
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            action3 = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "F", pendingIntent).build();
        }





        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("title");
        builder.setContentText("Marked ");
        builder.setOngoing(true);
        builder.addAction(action1);
        builder.addAction(action2);
        builder.addAction(action3);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1000, builder.build());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence charSequence = "Notification";
//            String notidisc = "Noti disc";
//            String CHANNEL_ID = "simple notification";
//            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, charSequence, NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.setDescription(notidisc);
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(notificationChannel);
//            // The id of the group.
//            String groupId = "my_group_01";
//            // The user-visible name of the group.
//            CharSequence groupName = "Hello";
//            NotificationManager mnotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mnotificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId, notidisc));
//
//        }


    }


    public void getdata(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild("IP")){
                  initial();
                } else {
                    Log.d(TAG, "getdata: " + dataSnapshot.child("L1").getValue().toString()                                                                                                                                                   );
                 //   ((TextView) findViewById(R.id.device_ip)).setText(dataSnapshot.child("IP").getValue().toString());

                    if (dataSnapshot.child("L1").getValue().toString().equals("1")){

                        light1.setHighlighted(true);
                        light1.setBottomText("On");
                        editor.putInt("L1" , 1);

                    } else if (dataSnapshot.child("L1").getValue().toString().equals("0")){

                        editor.putInt("L1" , 0);

                        if (light1.isHighlighted()){
                            light1.performClick();
                        }
                        light1.setBottomText("Off");

                    }

                    if (dataSnapshot.child("L2").getValue().toString().equals("1")){

                        editor.putInt("L2" , 1);

                        light2.setHighlighted(true);
                        light2.setBottomText("On");

                    } else if (dataSnapshot.child("L2").getValue().toString().equals("0")){

                        editor.putInt("L2" , 0);

                        if (light2.isHighlighted()){
                            light2.performClick();
                        }
                        light2.setBottomText("Off");

                    }

                    if (dataSnapshot.child("F").getValue().toString().equals("1")){

                        editor.putInt("F" , 1);

                        fan.setHighlighted(true);
                        fan.setBottomText("On");

                    } else if (dataSnapshot.child("F").getValue().toString().equals("0")){

                        editor.putInt("F" , 0);

                        if (fan.isHighlighted()){
                            fan.performClick();
                        }
                        fan.setBottomText("Off");

                    }

                    if (dataSnapshot.child("F").getValue().toString().equals("1") && dataSnapshot.child("L1").getValue().toString().equals("1") && dataSnapshot.child("L2").getValue().toString().equals("1")){

                        allon.setHighlighted(true);
                        allon.setBottomText("On");

                    } else if (dataSnapshot.child("F").getValue().toString().equals("0")){

                        if (allon.isHighlighted()){
                            allon.performClick();
                        }
                        allon.setBottomText("Off");

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initial() {
        databaseReference.child("IP").setValue("");
        databaseReference.child("L1").setValue("");
        databaseReference.child("L2").setValue("");
        databaseReference.child("F").setValue("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_signout) {
//            firebaseAuth.signOut();
//            startActivity(new Intent(MainActivity.this, Login.class));
//        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}