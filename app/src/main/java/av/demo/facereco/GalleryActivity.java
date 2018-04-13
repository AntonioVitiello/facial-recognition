package av.demo.facereco;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import av.demo.facereco.adapters.GalleryPagerAdapter;
import av.demo.facereco.event.FaceCenterEvent;
import av.demo.facereco.picasso.PicassoFaceDetector;
import timber.log.Timber;

public class GalleryActivity extends BaseActivity {
    private GalleryPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Face  center initialization
        PicassoFaceDetector.initialize(this);

        initComponent();
    }

    private void initComponent(){
        initActionBar();
        mPagerAdapter = new GalleryPagerAdapter(this, getSupportFragmentManager());
        mViewPager = findViewById(R.id.gallery_vp);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onDestroy() {
        PicassoFaceDetector.releaseDetector();
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
                consumed = true;
                break;
            }
            case R.id.personal_data_mi: {
                startPersonalDataActivity();
                consumed = true;
                break;
            }
            case R.id.center_face: {
                EventBus.getDefault().post(new FaceCenterEvent());
                break;
            }
            case android.R.id.home: {
                navigateUpFromSameTask();
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
