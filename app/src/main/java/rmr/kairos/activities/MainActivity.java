package rmr.kairos.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;

import rmr.kairos.R;
import rmr.kairos.controllers.OnSwipeTouchListener;
import rmr.kairos.fragments.PreferenceFragment;
import rmr.kairos.interfaces.LayoutUpdatable;
import rmr.kairos.threads.CounterThreadTimer;
import rmr.kairos.threads.LifeCycleThread;

/**
 * Actividad principal
 */
public class MainActivity extends AppCompatActivity implements LayoutUpdatable, PreferenceFragment.BottomSheetListener {
    private int lifeCycleSession = 0;
    private TextView tvTimer;
    private TextView tvSession;
    private TextView tvState;
    //private TextView tvToLogin;
    private ImageView imPreferences;
    private LifeCycleThread timerThread = null;
    private Thread lifeCycleThread;
    public static final String CHANNEL_ID = "Kairós";
    public static final String SERVICE_EXTRA = "timerExtra";
    private Intent serviceIntent;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tvTimer = findViewById(R.id.tvTimer);
        this.tvState = findViewById(R.id.tvState);
        this.tvSession = findViewById(R.id.tvSession);
        this.imPreferences = findViewById(R.id.imPreferences);
        //createNotificationChannel();
        //this.tvToLogin = findViewById(R.id.opLogin);
        this.tvTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //comienza un ciclo de vida, se inicializa el hilo de trabajo
                if (timerThread == null)
                    timerThread = new LifeCycleThread(lifeCycleSession, LifeCycleThread.WORKING,
                            MainActivity.this);
                if (timerThread.timerHasStarted()) {
                    lifeCycleThread = new Thread(timerThread, "LifeCycleThread");

                    lifeCycleThread.start();
                } else if (timerThread.timerHasPaused()) {
                    timerThread.reStartTimer();
                } else if (!timerThread.timerIsCycling()) {
                    timerThread.pauseTimer();
                }
            }
        });
        this.tvTimer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //no es posible iniciar un nuevo ciclo sin salir de la pausa anterior,
                //esto si quiere lo puedes quitar
                if (!timerThread.timerHasPaused()) return true;
                //se finaliza el hilo anterior que se encargará de detener al temporizador
                timerThread.endTimer();
                //stopService();
                Toast.makeText(MainActivity.this, "Se ha detenido el ciclo, en " +
                        CounterThreadTimer.CYCLE_TIME_MILLIS / 1000 +
                        " segundos se pondrá en marcha un ciclo nuevo", Toast.LENGTH_LONG).show();

                clearSessionText("0");
                lifeCycleSession = 0;
                //se crea un nuevo hilo para empezar un nuevo ciclo de vida
                timerThread = new LifeCycleThread(lifeCycleSession, LifeCycleThread.CYCLING,
                        MainActivity.this);
                if (timerThread.timerIsCycling()) {
                    updateStateText(getResources().getString(R.string.nuevociclo));
                    lifeCycleThread = new Thread(timerThread, "LifeCycleThread");

                    lifeCycleThread.start();
                }
                return true;
            }
        });

        /*this.tvTimer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getActionMasked();
                switch (action){
                    case MotionEvent.ACTION_UP:
                        //mandar un mensaje a hilo para añadir un minuto
                        Toast.makeText(MainActivity.this, "UPPPPPPPP", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });*/

        this.tvSession.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (timerThread.timerHasPaused()){
                    clearSessionText("0");
                    lifeCycleSession = 0;
                }
                else Toast.makeText(MainActivity.this, R.string.strRestartSessions, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        this.imPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager preferences = getSupportFragmentManager();
                FragmentTransaction preferencesTransaction = preferences.beginTransaction();
                PreferenceFragment preferenceFragment = new PreferenceFragment();
                preferencesTransaction.add(preferenceFragment, null);
                preferencesTransaction.commit();
            }
        });

    }

    @Override
    public void updateSessionText(int sessionCount) {
        this.tvSession.setText(String.valueOf(Integer.parseInt((String) tvSession.getText()) + sessionCount));
    }

    @Override
    public void updateTimerText(String text) {
        this.tvTimer.setText(text);
    }

    @Override
    public void updateStateText(String text) {
        this.tvState.setText(text);
    }

    @Override
    public void clearSessionText(String text) {
        this.tvSession.setText(text);
    }
    /*public void setSliderListener(){
        if (lifeCycleThread!=null) {
            this.tvTimer.setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    int state = timerThread.getLifeCycleState();
                    if (timerThread.timerHasPaused()) {
                        switch (state) {
                            case 1:
                                timerThread.endTimer();
                                if (timerThread.getLifeCycleSession() < 4)
                                    timerThread = new LifeCycleThread(lifeCycleSession, LifeCycleThread.BREAKING, MainActivity.this);
                                else
                                    timerThread = new LifeCycleThread(lifeCycleSession, LifeCycleThread.SLEEPING, MainActivity.this);
                                break;
                            case 2:
                                timerThread.endTimer();
                                timerThread = new LifeCycleThread(lifeCycleSession, LifeCycleThread.WORKING, MainActivity.this);
                                break;
                            case 3:
                                timerThread.endTimer();
                                timerThread = new LifeCycleThread(lifeCycleSession, LifeCycleThread.WORKING, MainActivity.this);
                                break;
                        }
                    }

                }

                @Override
                public void onSwipeRight() {
                    //envia señal para ir a sesión siguiente
                }
            });
        }
    }*/
    @Override
    public void onBottomSheetClicked(int op) {
        switch (op) {
            case 1:
                Intent intentToAjustes = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intentToAjustes);
                finish();
                break;
            case 2:
                Intent intentToStat = new Intent(getApplicationContext(), StatActivity.class);
                startActivity(intentToStat);
                finish();
                break;
            case 3:
                Intent intentToTag = new Intent(getApplicationContext(), TagActivity.class);
                startActivity(intentToTag);
                finish();
                break;
            case 4:
                Intent intentToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentToLogin);
                finish();
                break;
        }
    }
}