package rmr.kairos.services;

import static rmr.kairos.activities.MainActivity.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import rmr.kairos.R;
import rmr.kairos.activities.MainActivity;


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
        generateNotification(intent);
        this.intent = intent;
        startForeground(1, notification);
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
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            this.notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Kairós")
                    .setContentText(serviceString)
                    .setSmallIcon(R.drawable.kairos_nm)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();
            this.isFirst=false;
       // }

    }
}