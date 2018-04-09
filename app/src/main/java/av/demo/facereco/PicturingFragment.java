package av.demo.facereco;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import timber.log.Timber;

/**
 * Created by Vitiello Antonio on 07/04/2018.
 */

public class PicturingFragment extends Fragment implements PictureScheduler.Subscriber {
    private CameraView mCameraView;
    private Button mScattoButton;
    private ImageSaver mImageSaver;

    public static PicturingFragment newInstance() {
        return new PicturingFragment();
    }

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        mImageSaver = new ImageSaver(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_picturing, container, false);

        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        mCameraView = rootView.findViewById(R.id.camera);
/*
        mCameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        mCameraView.setFlash(CameraKit.Constants.FLASH_OFF);
        mCameraView.setCropOutput(true);
        mCameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE);
        mCameraView.setFocus(CameraKit.Constants.FOCUS_OFF);
        mCameraView.setJpegQuality(50);
        mCameraView.setMethod(CameraKit.Constants.METHOD_STILL);
        mCameraView.setPinchToZoom(false);
        mCameraView.setZoom(1.0F);
*/
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mCameraView.start();
            mCameraView.setVisibility(View.INVISIBLE);
            PictureScheduler.getInstance().start(this);
        } catch (final Exception exc) {
            PictureScheduler.getInstance().stop(this);
            Timber.e("Error while starting capture image.", exc);
        }
    }

    @Override
    public void onPause() {
        try {
            mCameraView.setVisibility(View.GONE);
            mCameraView.stop();
        } catch (final Exception exc) {
            Timber.e("Error while stopping capture image.", exc);
        } finally {
            PictureScheduler.getInstance().stop(this);
            super.onPause();
        }
    }

    //@OnCameraKitEvent(CameraKitImage.class)
    public void imageCaptured(CameraKitImage image) {
        try {
            byte[] jpeg = image.getJpeg();
            mImageSaver.setImage(jpeg);
            new Thread(mImageSaver).run();
        } catch (Exception exc) {
            Timber.e("Error while preparing image.", exc);
        }
    }

    @Override
    public void onSchedule() {
        captureImage();
    }

    public void captureImage() {
        try {
            mCameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                @Override
                public void callback(CameraKitImage event) {
                    imageCaptured(event);
                }
            });
        } catch (Exception exc) {
            Timber.e("Error while capturing image.", exc);
        }
    }

}
