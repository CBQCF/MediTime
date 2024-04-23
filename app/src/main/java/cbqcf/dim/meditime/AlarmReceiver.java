package cbqcf.dim.meditime;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    String medicName;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Ici, vous appelez votre méthode pour envoyer la notification
        medicName = intent.getStringExtra("MEDIC_NAME");
        sendNotification(context);
    }

    private void sendNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotifManager.CHANNEL_ID)
                .setSmallIcon(R.drawable.pill_24px)
                .setContentTitle("Medic Time !")
                .setContentText("Un médicament peut être pris !")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotifManager.CHANNEL_ID, "Channel title", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1001, builder.build());
    }
}