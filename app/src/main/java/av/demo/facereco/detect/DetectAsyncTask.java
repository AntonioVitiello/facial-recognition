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
import android.widget.ImageView;

import com.tzutalin.dlib.Constants;
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
    private final Context mContext;
    private final ImageView mImageView;
    private ProgressDialog mDialog;
    private static FaceDet mFaceDet;


    public DetectAsyncTask(Context context, ImageView imageView) {
        mContext = context;
        mImageView = imageView;
    }

    @Override
    protected void onPreExecute() {
        showDiaglog();
    }

    @Override
    protected List<VisionDetRet> doInBackground(File... pictureFiles) {
        File pictureFile = pictureFiles[0];

        initFaceDet();

        Timber.d("FaceDet: with file %s", pictureFile);
        List<VisionDetRet> faceList = mFaceDet.detect(pictureFile.getPath());
        Timber.d("FaceDet: %d faces detected.", faceList.size());

        return faceList;
    }

    private void initFaceDet() {
        if (mFaceDet == null) {
            // Copy file shape model
            String targetPath = Constants.getFaceShapeModelPath();
            if (new File(targetPath).exists()) {
                Timber.d("FaceDet: landmark model already in %s", targetPath);
            } else {
                Timber.d("FaceDet: copy landmark model to %s", targetPath);
                FileUtils.copyFileFromRawToOthers(mContext, R.raw.shape_predictor_68_face_landmarks, targetPath);
            }
            // Instantiate FaceDet
            mFaceDet = new FaceDet(targetPath);
        }
    }

    @Override
    protected void onPostExecute(List<VisionDetRet> faceList) {
        dismissDialog();
        if (faceList.size() > 0) {
            // drawFaceLandmarks
            drawRect(faceList, Color.GREEN);
        }
    }

    private void showDiaglog() {
        mDialog = ProgressDialog.show(mContext, "Wait", "Face detection in progress...", true);
    }

    private void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void drawRect(List<VisionDetRet> results, int color) {
        Timber.d("FaceDet: draw rect on %d face.", results.size());
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

        // Loop result list
        for (VisionDetRet ret : results) {
            Timber.d("FaceDet: VisionDetRet[%s], FaceLandmarks=%d", ret, ret.getFaceLandmarks().size());
            Rect bounds = new Rect();
            bounds.left = (int) (ret.getLeft());
            bounds.top = (int) (ret.getTop());
            bounds.right = (int) (ret.getRight());
            bounds.bottom = (int) (ret.getBottom());
            canvas.drawRect(bounds, paint);

            // Get landmark
            ArrayList<Point> landmarks = ret.getFaceLandmarks();
            for (Point point : landmarks) {
                Timber.d("FaceDet: landmarks[%s]", point);
                int pointX = (int) (point.x);
                int pointY = (int) (point.y);
                canvas.drawCircle(pointX, pointY, 2, paint);
            }
        }

        mImageView.setImageBitmap(bitmap);
    }

    public void toggle() {

    }
}
