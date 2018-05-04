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

import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import av.demo.facereco.R;
import av.demo.facereco.files.FileUtils;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 18/04/2018.
 */

public class DetectAsyncTask extends AsyncTask<File, Void, List<VisionDetRet>> {
    public static final double EYE_CLOSED_THRESHOLD = 0.195;
    private static final double MOUTH_CLOSED_THRESHOLD = 0.195;
    private final Context mContext;
    private final ImageView mImageView;
    private ProgressDialog mDialog;
    private static FaceDet sFaceDet;
    private static Object lock = new Object();

    public DetectAsyncTask(Context context, ImageView imageView) {
        mContext = context;
        mImageView = imageView;
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog();
    }

    @Override
    protected List<VisionDetRet> doInBackground(File... pictureFiles) {
        if (sFaceDet == null) {
            Timber.w("FaceDet: detector not initialized.");
            return new ArrayList<>();
        }
        File pictureFile = pictureFiles[0];
        Timber.d("FaceDet: with file %s", pictureFile);
        List<VisionDetRet> faceList = sFaceDet.detect(pictureFile.getPath());
        Timber.d("FaceDet: %d faces detected.", faceList.size());
        return faceList;
    }

    @Override
    protected void onPostExecute(List<VisionDetRet> faceList) {
        hideProgressDialog();
        if (faceList.size() == 0) {
            if (sFaceDet == null) {
                showDialogNotReady();
            }
        } else {
            // drawFaceLandmarks
            drawRect(faceList, Color.GREEN);
        }
    }

    private void showProgressDialog() {
        mDialog = ProgressDialog.show(mContext, "Wait", "Face detection in progress...", true);
    }

    private void hideProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void showDialogNotReady() {
        mDialog = ProgressDialog.show(mContext, "Wait", "Face detection initializing...", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDialog.dismiss();
            }
        }, 1500);
    }

    private void drawRect(List<VisionDetRet> results, int color) {
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
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        // Loop on every face detected
        for (VisionDetRet visionDetRet : results) {
            Rect bounds = new Rect();
            bounds.left = (int) (visionDetRet.getLeft());
            bounds.top = (int) (visionDetRet.getTop());
            bounds.right = (int) (visionDetRet.getRight());
            bounds.bottom = (int) (visionDetRet.getBottom());
            canvas.drawRect(bounds, paint);

            // Get landmark
            ArrayList<Point> landmarks = visionDetRet.getFaceLandmarks();

            for (int i = 0; i < 68; i++) {
                paint.setColor(getLandmarkColorByIndex(i));
                Point point = landmarks.get(i);
                int pointX = (int) (point.x);
                int pointY = (int) (point.y);
                canvas.drawCircle(pointX, pointY, 2, paint);
            }
/*
            for (Point point : landmarks) {
                int pointX = (int) (point.x);
                int pointY = (int) (point.y);
                canvas.drawCircle(pointX, pointY, 2, paint);
            }
*/
            Paint textPaint = new Paint();
            textPaint.setColor(color);
            textPaint.setTextSize(16.0f);
            int x = bounds.left + 2;
            int y = bounds.top - 38;
            canvas.drawText("left eye: " + (isLeftEyeOpen(landmarks) ? "Open" : "Closed"), x, y, textPaint);
            canvas.drawText("right eye: " + (isRightEyeOpen(landmarks) ? "Open" : "Closed"), x, y + 16, textPaint);
            canvas.drawText("mouth open: " + (isMouthOpen(landmarks) ? "Open" : "Closed"), x, y + 32, textPaint);
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

    // TODO: 04/05/2018 Just for tests, clean
    private List<Landmark> buildLandmarks() {
        List<Landmark> landmarks = new ArrayList<>();

        landmarks.add(new Landmark.Builder()
                .points(new Point(63, 264), new Point(65, 280), new Point(68, 297), new Point(71, 313), new Point(76, 330), new Point(84, 345), new Point(94, 358), new Point(108, 369), new Point(124, 372), new Point(143, 369), new Point(159, 359), new Point(173, 347), new Point(183, 332), new Point(190, 315), new Point(193, 297), new Point(195, 279), new Point(195, 260), new Point(67, 249), new Point(72, 238), new Point(84, 234), new Point(97, 234), new Point(110, 239), new Point(124, 239), new Point(137, 233), new Point(152, 231), new Point(167, 235), new Point(176, 246), new Point(117, 250), new Point(117, 259), new Point(116, 269), new Point(115, 280), new Point(105, 293), new Point(111, 295), new Point(117, 297), new Point(125, 295), new Point(132, 293), new Point(81, 257), new Point(87, 253), new Point(94, 253), new Point(102, 256), new Point(94, 259), new Point(87, 259), new Point(138, 255), new Point(144, 251), new Point(152, 252), new Point(160, 255), new Point(153, 258), new Point(145, 257), new Point(97, 323), new Point(106, 319), new Point(112, 316), new Point(118, 318), new Point(124, 316), new Point(134, 319), new Point(146, 323), new Point(134, 328), new Point(125, 329), new Point(119, 329), new Point(113, 329), new Point(106, 328), new Point(101, 323), new Point(113, 321), new Point(118, 322), new Point(124, 321), new Point(142, 323), new Point(125, 322), new Point(118, 323), new Point(113, 322))
                .label("180419_000013.jpg")
                .build());

        landmarks.add(new Landmark.Builder()
                .points(new Point(47, 280), new Point(49, 296), new Point(53, 311), new Point(58, 327), new Point(64, 342), new Point(72, 356), new Point(83, 368), new Point(96, 376), new Point(112, 378), new Point(130, 374), new Point(146, 364), new Point(161, 352), new Point(171, 335), new Point(176, 316), new Point(177, 295), new Point(176, 274), new Point(175, 253), new Point(45, 265), new Point(48, 255), new Point(57, 251), new Point(68, 251), new Point(79, 254), new Point(90, 250), new Point(102, 242), new Point(117, 237), new Point(133, 237), new Point(146, 244), new Point(86, 264), new Point(86, 274), new Point(86, 283), new Point(86, 294), new Point(81, 309), new Point(86, 310), new Point(93, 311), new Point(100, 307), new Point(108, 305), new Point(58, 276), new Point(62, 272), new Point(69, 269), new Point(77, 270), new Point(71, 275), new Point(64, 277), new Point(109, 264), new Point(116, 260), new Point(123, 258), new Point(132, 259), new Point(125, 263), new Point(117, 264), new Point(82, 339), new Point(86, 334), new Point(90, 329), new Point(96, 330), new Point(101, 327), new Point(111, 329), new Point(126, 331), new Point(115, 337), new Point(106, 339), new Point(101, 341), new Point(95, 342), new Point(89, 342), new Point(85, 338), new Point(92, 335), new Point(98, 335), new Point(102, 333), new Point(121, 332), new Point(104, 333), new Point(99, 334), new Point(93, 335))
                .label("180419_000012")
                .build());

        return landmarks;
    }

    private void claculateFaceDistance1() {
        List<Landmark> landmarks = buildLandmarks();
        for (int i = 0; i < landmarks.size(); i++) {
            Landmark l1 = landmarks.get(i);
            for (int j = i + 1; j < landmarks.size(); j++) {
                Landmark l2 = landmarks.get(j);
                int countOk = checkDistance(l1.getPoints(), l2.getPoints(), 60);
                Timber.e("AAA [%d] distance[%s,%s] countOk=%d", i, l1.getLabel(), l2.getLabel(), countOk);
            }
        }
    }

    private int checkDistance(Point[] p1, Point[] p2, int threshold) {
        int countUnder = 0;
        for (int i = 0; i < p1.length; i++) {
            double distance = euclideanDistance(p1[i], p2[i]);
            if (distance < threshold) {
                ++countUnder;
            }
        }
        return countUnder;
    }

    public double euclideanDistance(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    public static final void initialize(final Context context) {
        if (lock != null) {
            synchronized (lock) {
                if (lock != null) {
                    lock = null;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Copy file shape model just one time
                            File modelFile = FileUtils.getFaceShapeModelFile();
                            if (modelFile.exists()) {
                                Timber.d("FaceDet: landmark model already in %s", modelFile);
                            } else {
                                Timber.d("FaceDet: copy landmark model to %s", modelFile);
                                FileUtils.copyFromRaw(context, R.raw.shape_predictor_68_face_landmarks, modelFile);
                            }
                            // Instantiate FaceDet
                            sFaceDet = new FaceDet(modelFile.getPath());
                        }
                    }).start();
                }
            }
        }
    }

    public static void releaseDetector() {
        if (sFaceDet != null) {
            sFaceDet.release();
        }
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
        return (euclideanDistance(p2, p8) + euclideanDistance(p3, p7) + euclideanDistance(p4, p6)) / (euclideanDistance(p1, p5));
    }

}
