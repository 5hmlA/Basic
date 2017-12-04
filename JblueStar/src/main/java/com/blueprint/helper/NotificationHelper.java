package com.blueprint.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.blueprint.LibApp;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    public static void create(int id, Intent intent, int smallIcon, String contentTitle, String contentText){
        NotificationManager manager = (NotificationManager)LibApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent para disparar o broadcast
        PendingIntent p = PendingIntent.getActivity(LibApp.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cria a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(LibApp.getContext()).setContentIntent(p)
                .setContentTitle(contentTitle).setContentText(contentText).setSmallIcon(smallIcon).setAutoCancel(true);

        // Dispara a notification
        Notification n = builder.build();
        manager.notify(id, n);

        Log.d(TAG, "Notification criada com sucesso");
    }

    public static void createStackNotification(int id, String groupId, Intent intent, int smallIcon, String contentTitle, String contentText){
        NotificationManager manager = (NotificationManager)LibApp.getContext()
                .getSystemService(LibApp.getContext().NOTIFICATION_SERVICE);

        // Intent para disparar o broadcast
        PendingIntent p = intent != null ? PendingIntent
                .getActivity(LibApp.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT) : null;

        // Cria a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(LibApp.getContext()).setContentIntent(p)
                .setContentTitle(contentTitle).setContentText(contentText).setSmallIcon(smallIcon).setGroup(groupId)
                .setAutoCancel(true);

        // Dispara a notification
        Notification n = builder.build();
        manager.notify(id, n);

        Log.d(TAG, "Notification criada com sucesso");
    }

    // Notificação simples sem abrir intent (usada para alertas, ex: no wear)
    public static void create(int smallIcon, String contentTitle, String contentText){
        NotificationManager manager = (NotificationManager)LibApp.getContext()
                .getSystemService(LibApp.getContext().NOTIFICATION_SERVICE);

        // Cria a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(LibApp.getContext())
                .setContentTitle(contentTitle).setContentText(contentText).setSmallIcon(smallIcon).setAutoCancel(true);

        // Dispara a notification
        Notification n = builder.build();
        manager.notify(0, n);

        Log.d(TAG, "Notification criada com sucesso");
    }

    public static void cancell(int id){
        NotificationManagerCompat nm = NotificationManagerCompat.from(LibApp.getContext());
        nm.cancel(id);
    }

    public static void cancellAll(){
        NotificationManagerCompat nm = NotificationManagerCompat.from(LibApp.getContext());
        nm.cancelAll();
    }
}
