package av.demo.facereco;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.rohitarya.picasso.facedetection.transformation.core.PicassoFaceDetector;

import av.demo.facereco.adapters.GalleryPagerAdapter;

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

/*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    @Override
    protected void onPause() {
        if(isFinishing()){
            PicassoFaceDetector.releaseDetector();
        }
        super.onPause();
    }
}
