package av.demo.camerakit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.OnCameraKitEvent;

import java.io.File;

public class MainActivity extends AppCompatActivity implements PictureScheduler.OnSchedule {
    private static final String LOG_TAG = "MainActivity";
    private CameraView cameraView;
    private File outputMediaFile;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mine);

        initCameraView();

    }

    private void initCameraView() {
        cameraView = findViewById(R.id.camera);
/*
        cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
        cameraView.setCropOutput(true);
        cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE);
        cameraView.setFocus(CameraKit.Constants.FOCUS_OFF);
        cameraView.setJpegQuality(50);
        cameraView.setMethod(CameraKit.Constants.METHOD_STILL);
        cameraView.setPinchToZoom(false);
        cameraView.setZoom(1.0F);
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        PictureScheduler.getInstance().start(this);
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        PictureScheduler.getInstance().stop(this);
        super.onPause();
    }

    public void onClickScatto(View view) {
        captureImage();
    }

    @OnCameraKitEvent(CameraKitImage.class)
    public void imageCaptured(CameraKitImage image) {
        byte[] jpeg = image.getJpeg();
        ImageSaver imageSaver = new ImageSaver(this, jpeg);
        mFile = imageSaver.getPictureFile();
        new Thread(imageSaver).run();
    }

    @Override
    public void onScheduled() {
        captureImage();
    }

    public void captureImage() {
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage event) {
                imageCaptured(event);
            }
        });
    }

}
