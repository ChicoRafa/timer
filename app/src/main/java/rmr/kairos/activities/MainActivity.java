package rmr.kairos.activities;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.time.LocalDate;
import java.util.LinkedHashMap;

import rmr.kairos.R;
import rmr.kairos.database.KairosDB;
import rmr.kairos.database.KairosHelper;
import rmr.kairos.fragments.PreferenceFragment;
import rmr.kairos.interfaces.LayoutUpdatable;
import rmr.kairos.services.TimerService;
import rmr.kairos.threads.CounterThreadTimer;
import rmr.kairos.threads.LifeCycleThread;

/**
 * Actividad principal de Kairós, crea los contadores usando hilos y permite la navegabilidad por toda
 * la app; es el centro neurálgico de la aplicación
 * @author Rafa M.
 * @version 1.0
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity implements LayoutUpdatable, PreferenceFragment.BottomSheetListener {
    private View lyMain;
    private int lifeCycleSession = 0;
    private int tiempoRestante = 0;
    private int workTime = 0;
    private TextView tvTimer;
    private TextView tvSession;
    private TextView tvState;
    private ImageView imTag;
    private TextView tvTagMain;
    //private TextView tvToLogin;
    private ImageView imPreferences;
    private LifeCycleThread timerThread = null;
    private Thread lifeCycleThread;
    public static final String CHANNEL_ID = "Kairós";
    public static final String SERVICE_EXTRA = "timerExtra";
    private final int RQ_MAIN = 10;
    private final String IK_MAIN = "main_key";
    private Intent serviceIntent;
    private boolean isFirst = true;
    private SharedPreferences kp;
    private ActivityResultLauncher<Intent> launcher;
    private final ActivityResultRegistry mRegistry;
    private KairosHelper dbHelper = new KairosHelper(MainActivity.this);
    private SQLiteDatabase dbKairos;
    private KairosDB db = new KairosDB(MainActivity.this);
    private boolean logueado = false;

    public MainActivity() {
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

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.kp = PreferenceManager.getDefaultSharedPreferences(this);
        if (kp.getBoolean("onScreen_key",true)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (kp.getBoolean("dark_mode_key",true)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        this.tvTimer = findViewById(R.id.tvTimer);
        this.tvState = findViewById(R.id.tvState);
        this.tvSession = findViewById(R.id.tvSession);
        this.imPreferences = findViewById(R.id.imPreferences);
        this.imTag = findViewById(R.id.imTag);
        this.tvTagMain = findViewById(R.id.tvTagMain);
        this.lyMain = findViewById(R.id.lyMain);
        try {
            this.logueado = getIntent().getExtras().getBoolean("Logueado");
        } catch (NullPointerException np) {
            np.toString();
        }
        //this.tvTimer.setText(String.valueOf(kp.getInt("work_value_key", 25)) + ":00");
        //inicia la BDD
        dbKairos = dbHelper.getWritableDatabase();
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
                //createNotificationChannel();
                //no es posible iniciar un nuevo ciclo sin salir de la pausa anterior,
                //esto si quiere lo puedes quitar
                if (!timerThread.timerHasPaused() && timerThread!=null) return true;
                //se finaliza el hilo anterior que se encargará de detener al temporizador
                timerThread.endTimer();
                if (logueado) {
                    tiempoRestante = Integer.valueOf(tvTimer.getText().toString().substring(0, 2));
                    workTime = kp.getInt("work_bar_key", 25) - tiempoRestante;
                    if (workTime != 0)
                    db.updateStat(getDia(), workTime);
                    else db.updateStat(getDia(), kp.getInt("work_bar_key", 25));
                }
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
                if (timerThread.timerHasPaused()) {
                    clearSessionText("0");
                    lifeCycleSession = 0;
                } else
                    Toast.makeText(MainActivity.this, R.string.strRestartSessions, Toast.LENGTH_SHORT).show();
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
        this.imTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagDialog();
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
        startService();
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

    /**
     * Método que permite la selección de opciones en el fragmento de preferencias (BottomSheetFragment)
     * @param op ==> opción seleccionada
     * @since 1.0
     */
    @Override
    public void onBottomSheetClicked(int op) {
        switch (op) {
            case 1:
                Intent intentToAjustes = new Intent(getApplicationContext(), SettingsActivity.class);
                intentToAjustes.putExtra(IK_MAIN, RQ_MAIN);
                launcher.launch(intentToAjustes);
                break;
            case 2:
                if (logueado) {
                    Intent intentToStat = new Intent(getApplicationContext(), StatActivity.class);
                    intentToStat.putExtra(IK_MAIN, RQ_MAIN);
                    launcher.launch(intentToStat);
                } else Toast.makeText(this, R.string.strStatNotLogged, Toast.LENGTH_SHORT).show();
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

    /**
     * Método que crea el diálogo de selección de etiqueta
     * @since 1.0
     */
    public void tagDialog() {
        AlertDialog.Builder tagBuilder = new AlertDialog.Builder(MainActivity.this);
        View tagView = getLayoutInflater().inflate(R.layout.dialog_tag_main, null);
        EditText etTag = tagView.findViewById(R.id.etTagNameMain);
        String[] colorsCode = getResources().getStringArray(R.array.tagSpinnerArray);
        String[] colorsName = getResources().getStringArray(R.array.tagSpinnerColor);
        LinkedHashMap<String, String> mapaColores = new LinkedHashMap<String, String>();
        for (int i = 0; i < colorsName.length; i++) {
            mapaColores.put(colorsName[i], colorsCode[i]);
        }
        tagBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (!etTag.getText().toString().isEmpty()) {
                    for (int j = 0; j < db.selectTags().size(); j++) {
                        if (etTag.getText().toString().equalsIgnoreCase(db.selectTags().get(j).getTagName())) {
                            tvTagMain.setTextColor(Color.parseColor(mapaColores.get(db.selectTags().get(j).getTagColor())));
                            tvTagMain.setText(db.selectTags().get(j).getTagName());
                            tvTagMain.setVisibility(View.VISIBLE);
                        }
                    }
                }else {
                    tvTagMain.setText("");
                    tvTagMain.setVisibility(View.INVISIBLE);
                }

            }
        });

        tagBuilder.setCancelable(true);
        tagBuilder.setView(tagView);
        AlertDialog dialog = tagBuilder.create();
        dialog.show();


    }

    /**
     * Método que obtiene el día actual para usarlo en la BDD a la hora de añadir información
     * a las estadísticas
     * @return ==> día de la semana
     * @since 1.0
     */
    private String getDia() {
        String day = "";
        String dia = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            day = LocalDate.now().getDayOfWeek().toString();
        }
        switch (day) {
            case "MONDAY":
                dia = "lunes";
                break;
            case "TUESDAY":
                dia = "martes";
                break;
            case "WEDNESDAY":
                dia = "miercoles";
                break;
            case "THURSDAY":
                dia = "jueves";
                break;
            case "FRIDAY":
                dia = "viernes";
                break;
            case "SATURDAY":
                dia = "sabado";
                break;
            case "SUNDAY":
                dia = "domingo";
                break;
        }
        return dia;
    }

    /**
     * Método usado para la navegabilidad
     * since 1.0
     */
    private void setUpLauncher() {
        this.launcher = this.mRegistry.register(IK_MAIN,
                new ActivityResultContracts.StartActivityForResult(),
                null);

    }

    /**
     * Método que inicia el servicio de notificaciones
     * @since 1.0
     */
    public void startService() {
        String serviceString = "Tiempo restante: " + tvTimer.getText().toString();
        this.serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra(SERVICE_EXTRA, serviceString);
        startService(serviceIntent);
    }

    public void stopService() {
        stopService(this.serviceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timerThread!=null) {
            try {
                startService();
                this.isFirst = false;
            }catch (NullPointerException e){
                e.toString();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.serviceIntent != null)
            stopService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.serviceIntent != null)
            stopService();
    }
}