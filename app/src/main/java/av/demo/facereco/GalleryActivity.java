package av.demo.facereco;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import av.demo.facereco.adapters.GalleryPagerAdapter;
import av.demo.facereco.detect.DetectAsyncTask;
import av.demo.facereco.dialogs.ErrorDialog;
import av.demo.facereco.dialogs.RationaleDialog;
import av.demo.facereco.event.MenuTapEvent;
import timber.log.Timber;

public class GalleryActivity extends BaseActivity {
    private static final int WRITE_PERMISSION_CODE = 1;
    private static final String FRAGMENT_DIALOG_TAG = "dialog";
    private GalleryPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Face detection initialization
        DetectAsyncTask.initialize(this);

        initComponent();
        checkPermissions();
    }

    private void initComponent(){
        initActionBar();
        mPagerAdapter = new GalleryPagerAdapter(this, getSupportFragmentManager());
        mViewPager = findViewById(R.id.gallery_vp);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            if (shouldShowRequestPermissionRationale(writePermission)) {
                RationaleDialog.newInstance(writePermission, WRITE_PERMISSION_CODE, getString(R.string.disk_request_permission))
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG_TAG);
            } else {
                requestPermissions(new String[]{writePermission}, WRITE_PERMISSION_CODE);
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed, only on SDK M or later...
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_PERMISSION_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.disk_denied_permission))
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG_TAG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        // Release face detector
        DetectAsyncTask.releaseDetector();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean consumed = false;
        switch (item.getItemId()) {
            case R.id.login_mi: {
                startLoginActivity();
                finish();
                consumed = true;
                break;
            }
            case R.id.personal_data_mi: {
                startPersonalDataActivity();
                consumed = true;
                finish();
                break;
            }
            case R.id.detect_face: {
                EventBus.getDefault().post(new MenuTapEvent(MenuTapEvent.DETECT_FACE));
                break;
            }
            case R.id.recognize_dir: {
                EventBus.getDefault().post(new MenuTapEvent(MenuTapEvent.RECOGNIZE_DIR));
                break;
            }
            case android.R.id.home: {
                navigateUpFromSameTask();
                finish();
                consumed = true;
                break;
            }
            default: {
                Timber.e("Unable to manage menu action: " + item.getItemId());
                consumed = super.onOptionsItemSelected(item);
            }
        }
        return consumed;
    }

}
