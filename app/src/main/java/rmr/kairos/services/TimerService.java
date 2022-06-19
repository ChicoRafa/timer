package rmr.kairos.services;

import static rmr.kairos.activities.MainActivity.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import rmr.kairos.R;
import rmr.kairos.activities.MainActivity;

/**
 * Servicio que permite crear las notificaciones de la app
 * @author Rafa M.
 * @version 2.0
 * @since 1.0
 */
public class TimerService extends Service {
    public static final String SERVICE_EXTRA = "timerExtra";
    private Intent intent;
    private Notification notification;
    private boolean isFirst=true;


    public TimerService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.createNotificationChannel();
        this.generateNotification(intent);
        this.intent = intent;
        startForeground(1, this.notification);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void generateNotification(Intent intent) {
        //if (this.isFirst){
        String serviceString = intent.getStringExtra(SERVICE_EXTRA);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        }else PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        this.notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Kairós")
                .setContentText(serviceString)
                .setSmallIcon(R.drawable.kairos_nm)
                //.setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        this.isFirst=false;
        // }

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Kairós Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}