package rmr.kairos.threads;

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

    //tiempos para los estados de los ciclos de vida
    public static final long START_TIME_MILIS = (6000);
    public static final long BREAK_TIME_MILIS = (3000);
    public static final long LONG_BREAK_TIME_MILIS = (4000);
    public static final int CYCLE_TIME_MILLIS = 5000;

    private int timerState;
    private KairosLong timeLeftMilis;
    private Timer timer;
    private TimerTask timerTask;
    private LifeCycleThread lifeCycleThread;

    public CounterThreadTimer(LifeCycleThread lifeCycleThread) {
        this.lifeCycleThread = lifeCycleThread;
        if(lifeCycleThread.getLifeCycleState()==LifeCycleThread.CYCLING)
            this.timerState = TIMER_CYCLING;
        else
            this.timerState = TIMER_STARTED;
        this.timeLeftMilis = new KairosLong(0);
        this.timer = new Timer("CounterThreadTimer");
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
                                timeLeftMilis.setValue(timeLeftMilis.getValue()+1000);
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
                                timeLeftMilis.setValue(BREAK_TIME_MILIS);
                                lifeCycleThread.updateStateText(lifeCycleThread.getBreakingStateText());
                                lifeCycleThread.setLifeCycleState(LifeCycleThread.BREAKING);
                            } else {
                                lifeCycleThread.setLifeCycleSession(0);
                                timeLeftMilis.setValue(LONG_BREAK_TIME_MILIS);
                                lifeCycleThread.updateStateText(lifeCycleThread.getSleepingStateText());
                                lifeCycleThread.setLifeCycleState(LifeCycleThread.SLEEPING);
                            }
                            break;
                        case LifeCycleThread.CYCLING:
                        case LifeCycleThread.BREAKING:
                        case LifeCycleThread.SLEEPING:
                            timeLeftMilis.setValue(START_TIME_MILIS);
                            lifeCycleThread.updateStateText(lifeCycleThread.getWorkingStateText());
                            lifeCycleThread.setLifeCycleState(LifeCycleThread.WORKING);
                            break;
                    }

                    timerState = TIMER_FINISHED;
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
        this.timeLeftMilis.setValue(START_TIME_MILIS);
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

    public boolean isCycling(){
        return this.timerState==CounterThreadTimer.TIMER_CYCLING;
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
}
