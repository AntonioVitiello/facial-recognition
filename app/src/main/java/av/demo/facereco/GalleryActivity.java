package av.demo.facereco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import av.demo.facereco.adapters.GalleryPagerAdapter;
import av.demo.facereco.picasso.PicassoFaceDetector;
import timber.log.Timber;

public class GalleryActivity extends AppCompatActivity {

    private GalleryPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        PicassoFaceDetector.initialize(this);
        mPagerAdapter = new GalleryPagerAdapter(this, getSupportFragmentManager());

        initComponent();
    }

    private void initComponent(){
        mViewPager = findViewById(R.id.gallery_vp);
        mViewPager.setAdapter(mPagerAdapter);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                consumed = true;
                break;
            }
            case R.id.personal_data_mi: {
                Intent i = new Intent(this, PersonalDataActivity.class);
                startActivity(i);
                consumed = true;
                break;
            }
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
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
