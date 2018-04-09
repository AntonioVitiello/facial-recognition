package av.demo.facereco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private static final String TAG_PICTURING_FRAGMENT = "tag_picturing_fragment";
//    private PicturingFragment mPicturingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);

/*
        if (savedInstanceState == null) {
            // create the fragment just first time
            FragmentManager fm = getSupportFragmentManager();
            mPicturingFragment = PicturingFragment.newInstance();
            fm.beginTransaction().replace(R.id.picturing_frame_layout, mPicturingFragment, TAG_PICTURING_FRAGMENT).commit();
        }
*/
    }

/*
    @Override
    public void onPause() {
        super.onPause();
        // if this activity will not be recreated, ie. user is leaving it or the activity is otherwise finishing
        Timber.d("onPause: isFinishing = %b", isFinishing());
        if (isFinishing()) {
            // remove retained fragment and all others Fragment added to perform its own cleanup
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(mPicturingFragment).commit();
        }
    }
*/

    public void onClickReopen(View view) {
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }
}
