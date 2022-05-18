package rmr.kairos.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import java.sql.Time;
import java.util.Locale;

import rmr.kairos.R;
import rmr.kairos.threads.TimerThread;

public class MainActivity extends AppCompatActivity {
    private static final long START_TIME_MILIS = (6000);
    private static final int WORK_ID = 1;
    private static final int BREAK_ID = 2;
    private static final int LONG_BREAK_ID = 3;
    private int sesion = 0;
    private int id;
    private boolean isStopped = true;
    private int state = WORK_ID;
    private TextView tvTimer;
    private TextView tvSession;
    private TextView tvState;
    private TimerThread ctdTimer;
    boolean isFirst = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTimer = findViewById(R.id.tvTimer);
        tvState = findViewById(R.id.tvState);
        updateCountDownText();
        tvSession = findViewById(R.id.tvSession);
        tvTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFirst) {
                    isFirst=false;
                    ctdTimer = new TimerThread(isStopped, sesion, state, tvTimer, tvSession, tvState);
                    ctdTimer.run();
                }
                if (ctdTimer.hasStopped()) ctdTimer.run();
                if (!ctdTimer.hasStopped())ctdTimer.pauseTimer(ctdTimer.getTimer());

            }
        });
    }

    public void setTimer(TimerThread ctdTimer){
        this.ctdTimer = ctdTimer;
    }
    public int[] getParameters(){
        int[] parameters = new int[2];
        parameters[0] = sesion;
        parameters[1] = state;
        return parameters;
    }
    public void setParameters(int sesion, int state, boolean isStopped, TextView tvTimer, TextView tvSession, TextView tvState){
        this.sesion = sesion;
        this.state = state;
        ctdTimer = new TimerThread(isStopped, sesion, state, tvTimer, tvSession, tvState);
        ctdTimer.run();
    }
    /**
     * Método que actualiza el estado del contador
     * since 1.0
     */
    public void updateCountDownText() {
        int minutes = (int) (START_TIME_MILIS / 1000) / 60;
        int seconds = (int) (START_TIME_MILIS / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
        tvState.setText("TRABAJO");


    }
}