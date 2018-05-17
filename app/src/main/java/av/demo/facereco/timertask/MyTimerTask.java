package av.demo.facereco.timertask;

import java.util.TimerTask;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 16/05/2018.
 */
public class MyTimerTask extends TimerTask {
    private final OnTimer mOnTimer;
    private final int mTaskId;
    
    public interface OnTimer {
        void onTimeout(int taskId);
    }

    public MyTimerTask(OnTimer onTimer, int taskId) {
        mOnTimer = onTimer;
        mTaskId = taskId;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        mOnTimer.onTimeout(mTaskId);
        Timber.d("[%d]MyTimerTask: took %s to complete.", mTaskId, getTimeFrom(startTime));
    }

    private String getTimeFrom(long startTime) {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d,%02d", minutes, seconds, millis);
    }

}
