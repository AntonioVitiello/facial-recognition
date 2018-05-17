package av.demo.facereco.scheduler;

import java.util.TimerTask;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 16/05/2018.
 */
public class TakePictureTask extends TimerTask {
    public static final long INTERVAL = MyApplication.getIntResource(R.integer.take_picture_interval_millisec);
    public static final int TASK_ID = 11;
    private final OnTimer mOnTimer;
    private long mStartTime;

    public interface OnTimer {
        void onTimeout(int taskId);
    }

    public TakePictureTask(OnTimer onTimer) {
        mOnTimer = onTimer;
    }

    @Override
    public void run() {
        mStartTime = System.currentTimeMillis();
        Timber.d("TakePictureTask: Start [%s]", getTime());
        mOnTimer.onTimeout(TASK_ID);
        Timber.d("TakePictureTask: Tmout [%s]", getTime());
    }

    private String getTime() {
        long millis = System.currentTimeMillis() - mStartTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d,%02d", minutes, seconds, millis);
    }

}
