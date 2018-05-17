package av.demo.facereco.detect;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 18/04/2018.
 */

public class DetectAsyncTask extends AsyncTask<File, Void, List<VisionDetRet>> {
//    public static final double EYE_CLOSED_THRESHOLD = 0.195;
//    private static final double MOUTH_CLOSED_THRESHOLD = 0.141; //old: 0.195;
    public static final double EYE_CLOSED_THRESHOLD = 0.25;
    private static final double MOUTH_CLOSED_THRESHOLD = 0.085;
    private Context mContext;
    private final ImageView mImageView;
    private ProgressDialog mDialog;
    private Paint mFacePaint;
    private Paint mLandmardkPaint;


    public DetectAsyncTask(Context context, ImageView imageView) {
        mContext = context;
        mImageView = imageView;
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog();
    }

    @Override
    protected List<VisionDetRet> doInBackground(File... pictures) {
        initPainters();
        Timber.d("FaceDet: detecting face in %s", pictures[0]);
        // start face detector
        FaceDet faceDet = DetectorMgr.getInstance().getFaceDet();
        List<VisionDetRet> faceList = faceDet.detect(pictures[0].getPath());
        Timber.d("FaceDet: %d face detected.", faceList.size());
        return faceList;
    }

    @Override
    protected void onPostExecute(List<VisionDetRet> faceList) {
        drawRect(faceList); // draw face landmarks
        hideProgressDialog();
        String msg = faceList.size() != 0 ?  faceList.size() + " face detected." : "No face detected.";
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {
        mDialog = ProgressDialog.show(mContext, "Please Wait", "Face detection in progress...", true);
    }

    private void hideProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void initPainters() {
        mFacePaint = new Paint();
        mFacePaint.setColor(Color.GREEN);
        mFacePaint.setStrokeWidth(2);
        mFacePaint.setStyle(Paint.Style.STROKE);
        mFacePaint.setTextSize(16.0f);
        mLandmardkPaint = new Paint();
        mLandmardkPaint.setStrokeWidth(1);
        mLandmardkPaint.setStyle(Paint.Style.FILL);
    }

    private void showDialogNotReady() {
        mDialog = ProgressDialog.show(mContext, "Please Wait", "Face detection initializing...", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDialog.dismiss();
            }
        }, 1500);
    }

    private void drawRect(List<VisionDetRet> results) {
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable, so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        // Create canvas to draw
        Canvas canvas = new Canvas(bitmap);

        // Loop on every face detected
        for (VisionDetRet visionDetRet : results) {
            Rect bounds = new Rect();
            bounds.left = (int) (visionDetRet.getLeft());
            bounds.top = (int) (visionDetRet.getTop());
            bounds.right = (int) (visionDetRet.getRight());
            bounds.bottom = (int) (visionDetRet.getBottom());
            canvas.drawRect(bounds, mFacePaint);
            Timber.d("Detected FaceRect[W=%d,H=%d]", bounds.width(), bounds.height());

            // Get landmark and draw them
            ArrayList<Point> landmarks = visionDetRet.getFaceLandmarks();
            for (int i = 0; i < landmarks.size(); i++) {
                mLandmardkPaint.setColor(getLandmarkColorByIndex(i));
                Point point = landmarks.get(i);
                int pointX = (int) (point.x);
                int pointY = (int) (point.y);
                canvas.drawCircle(pointX, pointY, 2, mLandmardkPaint);
            }

            // Add text for open/close mouth/eyes
            int x = bounds.left + 2;
            int y = bounds.top - 38;
            canvas.drawText("Left-Eye: " + (isLeftEyeOpen(landmarks) ? "Open" : "Closed"), x, y, mFacePaint);
            canvas.drawText("Right-Eye: " + (isRightEyeOpen(landmarks) ? "Open" : "Closed"), x, y + 16, mFacePaint);
            canvas.drawText("Mouth: " + (isMouthOpen(landmarks) ? "Open" : "Closed"), x, y + 32, mFacePaint);
        }

        mImageView.setImageBitmap(bitmap);
    }

    private int getLandmarkColorByIndex(int index) {
        // 0-17 jaw (mascella)
        if (index < 17) {
            return Color.GREEN;
        }
        // 17-22: right_eyebrow (sopracciglio destro)
        if (index < 22) {
            return Color.MAGENTA;
        }
        // 22-27: left_eyebrow (sopracciglio sinistro)
        if (index < 27) {
            return Color.RED;
        }
        // 27-36: nose (naso)
        if (index < 36) {
            return Color.DKGRAY;
        }
        // 36-42: right_eye (occhio destro)
        if (index < 42) {
            return Color.CYAN;
        }
        // 42-48: left_eye (occhio sinistro)
        if (index < 48) {
            return Color.BLUE;
        }
        // 48-68: mouth (bocca)
        if (index < 68) {
            return Color.YELLOW;
        }
        return Color.BLACK;
    }

    public double euclideanDistance(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    /**
     * 42-48: left_eye
     *
     * @param landmarks
     * @return
     */
    private boolean isLeftEyeOpen(ArrayList<Point> landmarks) {
        Point p1 = landmarks.get(42);
        Point p2 = landmarks.get(43);
        Point p3 = landmarks.get(44);
        Point p4 = landmarks.get(45);
        Point p5 = landmarks.get(46);
        Point p6 = landmarks.get(47);
        double eyeAspectRatio = getEyeAspectRatio(p1, p2, p3, p4, p5, p6);
        Timber.d("Left-Eye-Aspect-Ratio=%f, eye-closed-threshold=%f", eyeAspectRatio, EYE_CLOSED_THRESHOLD);
        return eyeAspectRatio > EYE_CLOSED_THRESHOLD;
    }

    /**
     * 36-42: right_eye
     *
     * @param landmarks
     * @return
     */
    private boolean isRightEyeOpen(ArrayList<Point> landmarks) {
        Point p1 = landmarks.get(36);
        Point p2 = landmarks.get(37);
        Point p3 = landmarks.get(38);
        Point p4 = landmarks.get(39);
        Point p5 = landmarks.get(40);
        Point p6 = landmarks.get(41);
        double eyeAspectRatio = getEyeAspectRatio(p1, p2, p3, p4, p5, p6);
        Timber.d("Right-Eye-Aspect-Ratio=%f, eye-closed-threshold=%f", eyeAspectRatio, EYE_CLOSED_THRESHOLD);
        return eyeAspectRatio > EYE_CLOSED_THRESHOLD;
    }

    private double getEyeAspectRatio(Point p1, Point p2, Point p3, Point p4, Point p5, Point p6) {
        return (euclideanDistance(p2, p6) + euclideanDistance(p3, p5)) / (2.0 * euclideanDistance(p1, p4));
    }

    /**
     * 48-68: mouth
     *
     * @param landmarks
     * @return
     */
    public boolean isMouthOpen(ArrayList<Point> landmarks) {
        Point p1 = landmarks.get(60);
        Point p2 = landmarks.get(61);
        Point p3 = landmarks.get(62);
        Point p4 = landmarks.get(63);
        Point p5 = landmarks.get(64);
        Point p6 = landmarks.get(65);
        Point p7 = landmarks.get(66);
        Point p8 = landmarks.get(67);
        double mouthAspectRatio = getMouthAspectRatio(p1, p2, p3, p4, p5, p6, p7, p8);
        Timber.d("Mouth-Aspect-Ratio = %f, mouth-closed-threshold=%f", mouthAspectRatio, MOUTH_CLOSED_THRESHOLD);
        return mouthAspectRatio > MOUTH_CLOSED_THRESHOLD;
    }

    private double getMouthAspectRatio(Point p1, Point p2, Point p3, Point p4, Point p5, Point p6, Point p7, Point p8) {
        return (euclideanDistance(p2, p8) + euclideanDistance(p3, p7) + euclideanDistance(p4, p6)) / (2.0 * euclideanDistance(p1, p5));
    }

}
