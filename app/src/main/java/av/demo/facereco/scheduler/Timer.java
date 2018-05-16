package av.demo.facereco.scheduler;

import android.os.Handler;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 11/04/2018.
 */

public class Timer {
    private Subscriber mSubscriber;
    private long mDelay;
    private long mStartTime;
    private int mAlarmId;

    public interface Subscriber {
        void onSchedule(int alarmId);
    }

    private Handler mTimerHandler = new Handler();
    private boolean isStarted;
    private Runnable mTimerRunnable = new Runnable() {
        public void run() {
            try {
                Timber.d("%s fired new event", getIdForLog());
                mSubscriber.onSchedule(mAlarmId);
                Timber.d("%s scheduled new event.", getIdForLog());
                isStarted = schedule();
            } catch (Exception exc) {
                isStarted = false;
                Timber.e(exc, "%s File cleaner daemon: error.", getIdForLog());
            }
        }
    };

    private Timer() {
        // Do nothing
    }

    private Timer(Builder builder) {
        mSubscriber = builder.mSubscriber;
        mDelay = builder.mDelay;
        mAlarmId = builder.mAlarmId;
    }

    public void start() {
        if (isStarted) {
            Timber.d("%s start request ignored, already started.", getIdForLog());
        } else {
            isStarted = schedule();
        }
    }

    public void stop() {
        if (isStarted) {
            mTimerHandler.removeCallbacks(mTimerRunnable);
            isStarted = false;
            Timber.d("%s stopped.", getIdForLog());
        } else {
            Timber.d("%s stop request ignored, already stopped.", getIdForLog());
        }
    }

    private String getIdForLog() {
        return String.format("[offset=%s][alarmID=%d]Timer: ", getTime(), mAlarmId);
    }

    private boolean schedule(){
        mStartTime = System.currentTimeMillis();
        return mTimerHandler.postDelayed(mTimerRunnable, mDelay);
    }

    private String getTime(){
        long millis = System.currentTimeMillis() - mStartTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d,%02d", minutes, seconds, millis);
    }

    public static final class Builder {
        private Subscriber mSubscriber;
        private long mDelay;
        private int mAlarmId;

        public Builder subscriber(Subscriber subscriber) {
            mSubscriber = subscriber;
            return this;
        }

        public Builder delay(long delay) {
            mDelay = delay;
            return this;
        }

        public Builder alarmId(int alarmId) {
            mAlarmId = alarmId;
            return this;
        }

        public Timer build() {
            return new Timer(this);
        }
    }

}
