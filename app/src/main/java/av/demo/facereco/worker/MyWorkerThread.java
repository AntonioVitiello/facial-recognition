package av.demo.facereco.worker;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.File;
import java.text.SimpleDateFormat;

import av.demo.facereco.files.PictureSaver;
import av.demo.facereco.images.ImageBox;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 16/05/2018.
 */
public class MyWorkerThread extends HandlerThread {
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    public static final int SAVE_PICTURE_JOB = 0;
    public static final int CLEAN_PIC_DIR_JOB = 1;
    public static final int SAVE_TRANSF_PICTURE_JOB = 2;

    PictureSaver mPictureSaver = new PictureSaver();
    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private OnResponse mOnResponse;

    public MyWorkerThread(OnResponse onResponse) {
        super(MyWorkerThread.class.getSimpleName());
        super.start();
        prepareHandler();
        mResponseHandler = new Handler();
        //   Same as:
        // mResponseHandler = new Handler(Looper.getMainLooper());
        mOnResponse = onResponse;
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), mHandlerCallback);
    }

    public void enqueue(Object obj, int what) {
        Timber.d("[%d]Job added to the queue", what);
        Message message = mWorkerHandler.obtainMessage(what, obj);
        message.sendToTarget();
        //   Same as:
        // Message message = Message.obtain(mWorkerHandler, what, obj);
        // mWorkerHandler.sendMessage(message);
    }

    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Timber.d("[%d]Processing message", msg.what);
            switch (msg.what) {
                case SAVE_PICTURE_JOB: {
                    File file = mPictureSaver.with((byte[])msg.obj);
                    postBack(file, SAVE_PICTURE_JOB);
                    break;
                }
                case CLEAN_PIC_DIR_JOB: {
                    cleanPictureDir();
                    break;
                }
                case SAVE_TRANSF_PICTURE_JOB: {
                    ImageBox imageBox = (ImageBox) msg.obj;
                    mPictureSaver.with(imageBox);
                    postBack(imageBox.getFile(), SAVE_TRANSF_PICTURE_JOB);
                    break;
                }
                default:
                    Timber.e("Unknown job request: %d", msg.what);
            }

            return true;
        }
    };

    private void postBack(final File file, final int jobId) {
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                mOnResponse.onSaved(file, jobId);
            }
        });
    }

    private void cleanPictureDir() {

    }

    public interface OnResponse {
        public void onSaved(Object obj, int jobId);
    }

}