package av.demo.facereco.worker;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 16/05/2018.
 */
public class MyWorkerThread extends HandlerThread {
    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private Map<ImageView, String> mRequestMap = new HashMap<ImageView, String>();
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

    public void enqueueTask(int what, int arg1, int arg2, Object obj) {
        mRequestMap.put((ImageView) obj, "");
        Timber.d("Added to queue");
        Message message = mWorkerHandler.obtainMessage(what, arg1, arg2, obj);
        message.sendToTarget();
        //   Same as:
        // Message message = Message.obtain(mWorkerHandler, what, side, reqId, imageView);
        // mWorkerHandler.sendMessage(message);
    }

    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Timber.d("Processing...");
            handleRequest(msg.arg1, msg.arg2, msg);
            return true;
        }
    };

    private void handleRequest(final int arg1, final int arg2, final Object msg) {
        mRequestMap.remove(msg);
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                mOnResponse.onImageDownloaded(arg1, arg2, msg);
            }
        });
    }

    public interface OnResponse {
        public void onImageDownloaded(int arg1, int arg2, Object msg);
    }

}
