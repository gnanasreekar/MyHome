package com.rgs.myhome;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class NotificationSwitch extends Service {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public NotificationSwitch() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        sharedPreferences = getApplicationContext().getSharedPreferences("sp", 0);

        editor = sharedPreferences.edit();
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

        builder.setSmallIcon(R.drawable.home_logo);

        builder.setContentTitle("Home");
        builder.setOngoing(true);
        builder.addAction(action1);
        builder.addAction(action2);
        builder.addAction(action3);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1000, builder.build());
        super.onCreate();


    }
}
