package rmr.kairos.threads;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import rmr.kairos.util.KairosLong;

/**
 * Temporizador formado por un Timer y un TimerTask. El Timer está sincronizado para que
 * el hilo correspondiente se puede poner en espera cuando se pause el temporizador, y también para
 * poder reanudarlo
 */
public class CounterThreadTimer {
    //estados del temporizador en un ciclo de vida
    //representa que el usuario ha detenido el ciclo de vida y por tanto el temporizador
    public static final int TIMER_STOPPED = 100;
    public static final int TIMER_STARTED = 101;
    public static final int TIMER_PAUSED = 102;
    //esta constante refleja el estado finalizado del temporizador, no es lo mismo
    //que detenido. Esto último lo debe solicitar el usuario
    public static final int TIMER_FINISHED = 103;
    public static final int TIMER_RESTARTED = 104;
    public static final int TIMER_RUNNING = 105;
    public static final int TIMER_STARTING = 106;
    public static final int TIMER_CYCLING = 107;
    //static MainActivity ma = new MainActivity();
    //tiempos para los estados de los ciclos de vida
    private long startTimeMillis;
    private long breakTimeMilis;
    private long longBreakTimeMilis;
    public static final int CYCLE_TIME_MILLIS = 5000;

    private int timerState;
    private KairosLong timeLeftMilis;
    private Timer timer;
    private TimerTask timerTask;
    private LifeCycleThread lifeCycleThread;
    private SharedPreferences kp;
    private Context context;
    //private int workTime;
    //private String day = LocalDate.now().getDayOfWeek().toString();

    public CounterThreadTimer(SharedPreferences kp, LifeCycleThread lifeCycleThread, Context context) {
        this.kp = kp;
        this.initializeProperties();
        this.lifeCycleThread = lifeCycleThread;
        if (lifeCycleThread.getLifeCycleState() == LifeCycleThread.CYCLING)
            this.timerState = TIMER_CYCLING;
        else
            this.timerState = TIMER_STARTED;
        this.timeLeftMilis = new KairosLong(0);
        this.timer = new Timer("CounterThreadTimer");
        this.context = context;
    }

    private void initializeProperties() {
        this.startTimeMillis = this.kp.getInt("work_bar_key", 25) * 60000;
        this.breakTimeMilis = this.kp.getInt("break_bar_key", 5) * 60000;
        this.longBreakTimeMilis = this.kp.getInt("sleep_bar_key", 15) * 60000;
    }

    private void startTimerTask() {
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                if (timerState == TIMER_PAUSED) {
                    try {
                        synchronized (timer) {
                            timer.wait();
                            //se incrementa 1 segundo el temporizador debido
                            //al tiempo para salir de la espera
                            timeLeftMilis.setValue(timeLeftMilis.getValue() + 1000);
                        }
                        timerState = TIMER_RUNNING;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (timerState == TIMER_STOPPED) {
                    this.cancel();
                    timer.cancel();
                    return;
                }
                updateTimerText(timeLeftMilis);
                timeLeftMilis.setValue(timeLeftMilis.getValue() - 1000);
                if (timeLeftMilis.getValue() < 0)
                    this.cancel();
            }

            @Override
            public boolean cancel() {
                switch (lifeCycleThread.getLifeCycleState()) {
                    case LifeCycleThread.WORKING:
                        lifeCycleThread.setLifeCycleSession(lifeCycleThread.getLifeCycleSession() + 1);
                        lifeCycleThread.updateSessionText(1);
                        lifeCycleThread.updateStateText(lifeCycleThread.getWorkingStateText());
                        if (lifeCycleThread.getLifeCycleSession() < 4) {
                            timeLeftMilis.setValue(breakTimeMilis);
                            lifeCycleThread.updateStateText(lifeCycleThread.getBreakingStateText());
                            lifeCycleThread.setLifeCycleState(LifeCycleThread.BREAKING);
                        } else {
                            lifeCycleThread.setLifeCycleSession(0);
                            timeLeftMilis.setValue(longBreakTimeMilis);
                            lifeCycleThread.updateStateText(lifeCycleThread.getSleepingStateText());
                            lifeCycleThread.setLifeCycleState(LifeCycleThread.SLEEPING);
                        }
                        break;
                    case LifeCycleThread.CYCLING:
                    case LifeCycleThread.BREAKING:
                    case LifeCycleThread.SLEEPING:
                        timeLeftMilis.setValue(startTimeMillis);
                        lifeCycleThread.updateStateText(lifeCycleThread.getWorkingStateText());
                        lifeCycleThread.setLifeCycleState(LifeCycleThread.WORKING);
                        break;
                }

                timerState = TIMER_FINISHED;
                if (kp.getBoolean("vibrate_key",true)) {
                    Vibrator v = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }
                }
                timer.purge();
                return true;
            }

            @Override
            public long scheduledExecutionTime() {
                return super.scheduledExecutionTime();
            }
        };
    }

    public int getTimerState() {
        return this.timerState;
    }

    public void startTimer(KairosLong timeLeftMilis) {
        this.timeLeftMilis = timeLeftMilis;
        this.timerState = TIMER_STARTING;
        this.startTimerTask();
        this.runTimerTask();
    }

    public void runTimerTask() {
        this.timer = new Timer("CounterThreadTimer");
        this.timer.schedule(this.timerTask, 0, 1000);
        this.timerState = TIMER_RUNNING;
    }

    public void pauseTimer() {
        this.timerState = CounterThreadTimer.TIMER_PAUSED;
    }

    public void endTimer() {
        this.timerTask.cancel();
        this.timer.cancel();
        this.timer.purge();
        this.timeLeftMilis.setValue(startTimeMillis);
        this.timerState = CounterThreadTimer.TIMER_STOPPED;
    }

    public void reStartTimer() {
        this.timerState = CounterThreadTimer.TIMER_RESTARTED;
        synchronized (this.timer) {
            this.timer.notify();
        }
    }

    public void updateTimerText(KairosLong timeLeftMilis) {
        int minutes = (int) (timeLeftMilis.getValue() / 1000) / 60;
        int seconds = (int) (timeLeftMilis.getValue() / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        lifeCycleThread.updateTimerText(timeLeftFormatted);
    }

    public void stopTimer() {
        this.timer.cancel();
    }

    public boolean isCycling() {
        return this.timerState == CounterThreadTimer.TIMER_CYCLING;
    }

    public boolean hasFinished() {
        return this.timerState == CounterThreadTimer.TIMER_FINISHED;
    }

    public boolean hasPaused() {
        return this.timerState == CounterThreadTimer.TIMER_PAUSED;
    }

    public boolean hasStarted() {
        return this.timerState == CounterThreadTimer.TIMER_STARTED;
    }

    public boolean hasStoped() {
        return this.timerState == CounterThreadTimer.TIMER_STOPPED;
    }

    public long getStartTimeMillis() {
        return this.startTimeMillis;
    }

    public long getBreakTimeMilis() {
        return breakTimeMilis;
    }

    public long getLongBreakTimeMilis() {
        return longBreakTimeMilis;
    }
}
