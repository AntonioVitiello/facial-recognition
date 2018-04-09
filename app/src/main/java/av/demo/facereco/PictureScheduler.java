package av.demo.facereco;

import android.os.Handler;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 06/04/2018.
 */

public class PictureScheduler {
    private static final PictureScheduler sInstance = new PictureScheduler();
    final int mDelay = MyApplication.getIntResource(R.integer.picture_delay_millisec);
    private final Object lock = new Object();
    private Handler mHandler = new Handler();
    private Subscriber mSubscriber;
    private boolean isStarted;

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
                    isStarted = true;
                    startCycler();
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

    private void startCycler() {
        mHandler.postDelayed(new Runnable() {
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
                    Timber.e("Picture scheduler: error.", exc);
                }
            }
        }, mDelay);
    }

    public interface Subscriber {
        void onSchedule();
    }

}
