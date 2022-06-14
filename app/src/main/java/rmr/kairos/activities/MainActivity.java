package rmr.kairos.activities;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences kp;
    private final int RQ_MAIN = 10;
    private final String IK_MAIN = "main_key";
    private ActivityResultLauncher<Intent> launcher;
    private final ActivityResultRegistry mRegistry;

    public MainActivity(){
        this.mRegistry = new ActivityResultRegistry() {
            @Override
            public <I, O> void onLaunch(int requestCode, @NonNull ActivityResultContract<I, O> contract, I input, @Nullable ActivityOptionsCompat options) {
                ComponentActivity activity = MainActivity.this;
                Intent intent = contract.createIntent(activity, input);
                Bundle optionsBundle = null;
                if (intent.getExtras() != null && intent.getExtras().getClassLoader() == null) {
                    intent.setExtrasClassLoader(activity.getClassLoader());
                }
                ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsBundle);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tvTimer = findViewById(R.id.tvTimer);
        this.tvState = findViewById(R.id.tvState);
        this.tvSession = findViewById(R.id.tvSession);
        this.imPreferences = findViewById(R.id.imPreferences);
        this.kp = PreferenceManager.getDefaultSharedPreferences(this);
        this.tvTimer.setText(String.valueOf(kp.getInt("work_value_key",25))+":00");
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
        this.setUpLauncher();

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
                intentToAjustes.putExtra(IK_MAIN, RQ_MAIN);
                launcher.launch(intentToAjustes);
                break;
            case 2:
                Intent intentToStat = new Intent(getApplicationContext(), StatActivity.class);
                intentToStat.putExtra(IK_MAIN, RQ_MAIN);
                launcher.launch(intentToStat);
                break;
            case 3:
                Intent intentToTag = new Intent(getApplicationContext(), TagActivity.class);
                intentToTag.putExtra(IK_MAIN, RQ_MAIN);
                launcher.launch(intentToTag);
                break;
            case 4:
                Intent intentToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                intentToLogin.putExtra(IK_MAIN, RQ_MAIN);
                launcher.launch(intentToLogin);
                break;
        }
    }

    private void setUpLauncher(){
        this.launcher = this.mRegistry.register(IK_MAIN,
                new ActivityResultContracts.StartActivityForResult(),
                null);
    }
}