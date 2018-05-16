package av.demo.facereco.scheduler;

import android.os.Handler;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 06/04/2018.
 */

public class PictureScheduler {
    private static final PictureScheduler sInstance = new PictureScheduler();
    final int mDelay = MyApplication.getIntResource(R.integer.take_picture_interval_millisec);
    private final Object lock = new Object();
    private Subscriber mSubscriber;
    private boolean isStarted;

    public interface Subscriber {
        void onSchedule();
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            try {
                if (mSubscriber != null) {
                    mSubscriber.onSchedule();
                    Timber.d("Picture scheduler: new event.");
                    isStarted = mHandler.postDelayed(this, mDelay);
                } else {
                    isStarted = false;
                    Timber.d("Picture scheduler: stopped.");
                }
            } catch (NullPointerException exc) {
                isStarted = false;
                Timber.d("Picture scheduler: interrupted.");
            } catch (Exception exc) {
                isStarted = false;
                Timber.e(exc, "Picture scheduler: error.");
            }
        }
    };

    private PictureScheduler() {
        // Do nothing
    }

    public static PictureScheduler getInstance() {
        return sInstance;
    }

    public void start(Subscriber subscriber) {
        mSubscriber = subscriber;
        if (isStarted) {
            Timber.d("Picture scheduler: already started.");
        } else {
            synchronized (lock) {
                if (!isStarted) {
                    isStarted = mHandler.postDelayed(mRunnable, mDelay);
                    Timber.d("Picture scheduler: start request.");
                }
            }
        }
    }

    public void stop(Subscriber subscriber) {
        if (mSubscriber == null) {
            Timber.d("Picture scheduler: already stopped.");
            return;
        }
        if (subscriber == null || subscriber != mSubscriber) {
            Timber.d("Picture scheduler: ignored stop requested from invalid subscriber.");
            return;
        }
        mSubscriber = null;
        Timber.d("Picture scheduler: stop request.");
    }

    private long startTime(){
        return System.currentTimeMillis();
    }

    private String stopTime(long startTime){
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d,%03d", minutes, seconds, millis);
    }

}
