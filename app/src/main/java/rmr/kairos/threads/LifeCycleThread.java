package rmr.kairos.threads;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import rmr.kairos.R;
import rmr.kairos.activities.MainActivity;
import rmr.kairos.interfaces.LayoutUpdatable;
import rmr.kairos.util.KairosLong;

/**
 * Máquina de estados para gestionar los ciclos temporizados
 */
public class LifeCycleThread implements Runnable {
    //estados de un ciclo de vida
    public static final int WORKING = 1;
    public static final int SLEEPING = 3;
    public static final int BREAKING = 2;
    public static final int CYCLING = 4;

    private KairosLong timeLeftMilis;
    private CounterThreadTimer ctTimer;
    private int lifeCycleSession;
    private int lifeCycleState;
    private MainActivity mainActivity;
    private SharedPreferences kp;

    public LifeCycleThread(int session, int state, LayoutUpdatable layoutUpdatable) {
        this.mainActivity = (MainActivity) layoutUpdatable;
        this.kp = PreferenceManager.getDefaultSharedPreferences(this.mainActivity);
        this.lifeCycleSession = session;
        this.lifeCycleState = state;
        this.updateStateText(this.mainActivity.getResources().getString(R.string.trabajando));
        this.timeLeftMilis = new KairosLong(0);
        this.ctTimer = new CounterThreadTimer(kp,this);
    }
    @Override
    public void run() {
        boolean timerStarting = false;
        while (this.ctTimer.getTimerState() != CounterThreadTimer.TIMER_STOPPED) {
            if(this.ctTimer.getTimerState()==CounterThreadTimer.TIMER_PAUSED) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            switch (this.ctTimer.getTimerState()) {
                case CounterThreadTimer.TIMER_CYCLING:
                case CounterThreadTimer.TIMER_RESTARTED:
                case CounterThreadTimer.TIMER_STARTED:
                case CounterThreadTimer.TIMER_FINISHED:
                    if (!timerStarting) {
                        this.startTimer();
                        timerStarting = true;
                    }
                    break;
                case CounterThreadTimer.TIMER_STARTING:
                    timerStarting = false;
                    break;
            }
        }
        System.out.println("Termina el ciclo");
    }
    public void startTimer() {
        switch (this.lifeCycleState) {
            case LifeCycleThread.WORKING:
                this.timeLeftMilis.setValue(ctTimer.getStartTimeMillis());
                break;
            case LifeCycleThread.BREAKING:
                this.timeLeftMilis.setValue(ctTimer.getBreakTimeMilis());
                break;
            case LifeCycleThread.SLEEPING:
                this.timeLeftMilis.setValue(ctTimer.getLongBreakTimeMilis());
                break;
            case LifeCycleThread.CYCLING:
                this.timeLeftMilis.setValue(CounterThreadTimer.CYCLE_TIME_MILLIS);
                break;
        }
        this.ctTimer.startTimer(this.timeLeftMilis);
    }

    public void setLifeCycleSession(int session) {
        this.lifeCycleSession = session;
    }

    public int getLifeCycleSession() {
        return this.lifeCycleSession;
    }

    public void setLifeCycleState(int lifeCycleState) {
        this.lifeCycleState = lifeCycleState;
    }

    public int getLifeCycleState() {
        return this.lifeCycleState;
    }

    public void pauseTimer() {
        this.ctTimer.pauseTimer();
    }
    public void endTimer() {
        ctTimer.endTimer();
    }
    public void reStartTimer() {
        this.ctTimer.reStartTimer();
        synchronized (this) {
            this.notify();
        }
    }
    public boolean timerHasStoped() {
        return ctTimer.hasStoped();
    }

    public boolean timerHasFinished() {
        return ctTimer.hasFinished();
    }

    public boolean timerHasPaused() {
        return ctTimer.hasPaused();
    }

    public boolean timerHasStarted() {
        return ctTimer.hasStarted();
    }
    public boolean timerIsCycling(){
        return ctTimer.isCycling();
    }
    public void updateSessionText(int sessionCount) {
        this.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.updateSessionText(sessionCount);
            }
        });

    }
    public void updateTimerText(String text) {
        this.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.updateTimerText(text);
            }
        });
    }
    public void updateStateText(String text) {
        this.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.updateStateText(text);
            }
        });
    }
    public String getCyclingStateText(){
        return this.mainActivity.getResources().getString(R.string.nuevociclo);
    }
    public String getWorkingStateText() {
        return this.mainActivity.getResources().getString(R.string.trabajando);
    }

    public String getSleepingStateText() {
        return this.mainActivity.getResources().getString(R.string.durmiendo);
    }

    public String getBreakingStateText() {
        return this.mainActivity.getResources().getString(R.string.descansando);
    }
}