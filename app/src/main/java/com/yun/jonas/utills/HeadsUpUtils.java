package com.yun.jonas.utills;

/**
 * Created by Jonas on 2015/11/22.
 */
public class HeadsUpUtils {
//    public static void show(Context context, Class<?> targetActivity, String title, String content,
//                            int largeIcon, int smallIcon, int code) {
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(context, 11, new Intent(context, targetActivity),
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//        HeadsUpManager manage = HeadsUpManager.getInstant(context);
//        HeadsUp.Builder builder = new HeadsUp.Builder(context);
//        builder.setContentTitle(title)
//                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
//                .setContentIntent(pendingIntent)
//                .setFullScreenIntent(pendingIntent, false)
//                .setContentText(content);
//
//        if (Build.VERSION.SDK_INT >= 21) {
//            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
//                    .setSmallIcon(smallIcon);
//        }
//        else {
//            builder.setSmallIcon(largeIcon);
//        }
//
//        HeadsUp headsUp = builder.buildHeadUp();
//        headsUp.setSticky(true);
//        manage.notify(code, headsUp);
//    }
}
