package com.example.ahmed.AlarmRepeating;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

/**
 * Created by ahmed on 13/06/17.
 */

import com.example.ahmed.memo.MainActivity;
import com.example.ahmed.memo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import db.DbController;

public class NotificationReceiver extends BroadcastReceiver {

    int numMessages = 0;
    Context context = null;
    NotificationManager mNotificationManager = null;
    Intent intent = null;
    int timeMinutes, timeHours;
    private int notificationID = 0;
    Calendar calendar = Calendar.getInstance();
    ArrayList<HashMap<String, String>> hashMaps = null;
    DbController dbController = null;

//    public NotificationReceiver(Context context){
//        this.context = context;
//        dbController = new DbController(this.context);
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.intent = intent;
        this.context = context;
        dbController = new DbController(context);
        //createNotification();
        //getPassedData();
        getDataFromDbForChecking();


    }

    private void getDataFromDbForChecking() {
        hashMaps = dbController.getData("select * from " + DbController.tblNotes);
        if (!hashMaps.isEmpty()) {
            //chekc if the minutes, hours and AM or PM euals the current system time
            for (int i = 0; i < hashMaps.size(); i++){
                String hours = hashMaps.get(i).get(DbController.noteTimeHours);
                String minutes = hashMaps.get(i).get(DbController.noteTimeMinutes);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, Integer.parseInt(hours));
                calendar1.set(Calendar.MINUTE, Integer.parseInt(minutes));
                if(calendar1.get(Calendar.HOUR)== calendar.get(Calendar.HOUR) &&
                        Calendar.getInstance().get(Calendar.MINUTE)== calendar.get(Calendar.MILLISECOND)){
                    createNotification();
                }
//
            }

        }

    }

    private void getPassedData() {
        timeHours = intent.getIntExtra("hours", 0);
        timeMinutes = intent.getIntExtra("min", 0);
        createToast("min " + timeMinutes + "hours " + timeHours, Toast.LENGTH_SHORT);
    }

    private void createToast(String s, int lengthShort) {
        Toast.makeText(context, s, lengthShort).show();
    }

    public void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.context);
        mBuilder.setContentTitle("New Message");
        mBuilder.setContentText("You've received new message.");
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.mipmap.alarm);
        /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);
        /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this.context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
        stackBuilder.addParentStack(MainActivity.class);
       /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        /* notificationID allows you to update the notification later on. */

        mNotificationManager.notify(notificationID, mBuilder.build());
    }


}
