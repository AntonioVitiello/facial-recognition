package av.demo.camerakit;

import android.os.Handler;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 06/04/2018.
 */

public class PictureScheduler {
    private static final PictureScheduler sInstance = new PictureScheduler();
    private static final String LOG_TAG = "PictureScheduler";
    private boolean mIsStarted = false;
    private Handler mHandler = new Handler();
    final int mDelay = MyApplication.getIntResource(R.integer.picture_delay_millisec);
    private OnSchedule mOnSchedule;

    public interface OnSchedule {
        void onScheduled();
    }

    private PictureScheduler() {
//        Do nothing
    }

    public static PictureScheduler getInstance() {
        return sInstance;
    }

    public void start(OnSchedule onSchedule) {
        mOnSchedule = onSchedule;
        if (mIsStarted) {
            Timber.d("Picture scheduler already started.");
            return;
        }
        Timber.d("Picture scheduler started");
        mIsStarted = true;
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (mOnSchedule != null) {
                    synchronized (PictureScheduler.class) {
                        if (mOnSchedule != null) {
                            mOnSchedule.onScheduled();
                            mHandler.postDelayed(this, mDelay);
                            Timber.d("Picture scheduler activated and rearmed.");
                        }
                    }
                } else {
                    Timber.d("Picture scheduler stopped.");
                    mIsStarted = false;
                }
            }
        }, mDelay);
    }

    public void stop(OnSchedule onSchedule) {
        if(mOnSchedule == onSchedule) {
            mOnSchedule = null;
            Timber.d("Picture scheduler stop requested.");
        }
    }
}
