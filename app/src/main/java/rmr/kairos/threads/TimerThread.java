package rmr.kairos.threads;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Locale;

import rmr.kairos.activities.MainActivity;

public class TimerThread implements Runnable {
    private static final long START_TIME_MILIS = (6000);
    private static final long BREAK_TIME_MILIS = (3000);
    private static final long LONG_BREAK_TIME_MILIS = (4000);
    private long timeLeftMilis;
    private long endTime;
    private CountDownTimer ctTimer;
    private boolean isStopped;
    private int sesion;
    private int state;
    private TextView tvTimer;
    private TextView tvSession;
    private TextView tvState;
    private MainActivity index = new MainActivity();
    private String strWork = "TRABAJO";
    private String strBreak = "DESCANSO";
    private String strSleep = "DURMIENDO";
    //private String currentState = "";

    public TimerThread(boolean isStopped, int sesion, int state, TextView tvTimer, TextView tvSession, TextView tvState) {
        this.isStopped = isStopped;
        this.sesion = sesion;
        this.state = state;
        this.tvTimer = tvTimer;
        this.tvSession = tvSession;
        this.tvState = tvState;
    }

    @Override
    public void run() {
        int[] parameters = index.getParameters();
        parameters [0] = sesion;
        parameters [1] = state;
        if (isStopped) startTimer();
    }

    public void startTimer() {

        switch (state) {
            case 1:
                timeLeftMilis = START_TIME_MILIS;
                break;
            case 2:
                timeLeftMilis = BREAK_TIME_MILIS;
                break;
            case 3:
                timeLeftMilis = LONG_BREAK_TIME_MILIS;
                break;
        }
        runTimer();
    }
    public void runTimer(){
        endTime = System.currentTimeMillis() + timeLeftMilis;
        ctTimer = new CountDownTimer(timeLeftMilis, 1000) {
            @Override
            public void onTick(long milisUntilFinished) {
                isStopped=false;
                timeLeftMilis = milisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                switch (state) {
                    case 1:
                        sesion++;
                        tvSession.setText(String.valueOf(Integer.parseInt((String) tvSession.getText()) + 1));
                        tvState.setText(strWork);
                        if (sesion < 4) {
                            timeLeftMilis = BREAK_TIME_MILIS;
                            tvState.setText(strBreak);
                            state = 2;
                            isStopped=true;
                            index.setParameters(sesion, state, isStopped, tvTimer, tvSession, tvState);
                        } else{
                            sesion = 0;
                            timeLeftMilis = LONG_BREAK_TIME_MILIS;
                            tvState.setText(strSleep);
                            state = 3;
                            isStopped=true;
                            index.setParameters(sesion, state, isStopped, tvTimer, tvSession, tvState);
                        }
                        break;
                    case 2:
                        timeLeftMilis = START_TIME_MILIS;
                        tvState.setText(strWork);
                        state = 1;
                        isStopped=true;
                        index.setParameters(sesion, state, isStopped, tvTimer, tvSession, tvState);
                        break;
                    case 3:
                        timeLeftMilis = START_TIME_MILIS;
                        tvState.setText(strWork);
                        state = 1;
                        isStopped=true;
                        index.setParameters(sesion, state, isStopped, tvTimer, tvSession, tvState);
                        break;
                }

            }
        }.start();
    }
    /**
     * Método que pausa el contador
     *
     * @since 1.0
     */
    public void pauseTimer(CountDownTimer ctTimer) {
        ctTimer.cancel();
        isStopped = true;
    }

    /**
     * Método que acaba con el contador
     *
     * @since 1.0
     */
    public void endTimer() {
        timeLeftMilis = START_TIME_MILIS;
        updateCountDownText();
    }

    /**
     * Método que actualiza el estado del contador
     * since 1.0
     */
    public void updateCountDownText() {
        int minutes = (int) (timeLeftMilis / 1000) / 60;
        int seconds = (int) (timeLeftMilis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);


    }
    public CountDownTimer getTimer(){
        return ctTimer;
    }
    public boolean hasStopped(){
        return isStopped;
    }

}