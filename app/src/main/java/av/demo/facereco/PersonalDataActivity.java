package av.demo.facereco;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import timber.log.Timber;

public class PersonalDataActivity extends BaseActivity {
    private static final String PERSONAL_DATA_FRAGMENT_TAG = "personal_data_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        initActionBar();

        if(savedInstanceState == null) {
            PersonalDataFragment personalDataFragment = PersonalDataFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_personal_data, personalDataFragment, PERSONAL_DATA_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean consumed = false;
        switch (item.getItemId()) {
            case R.id.gallery_mi: {
                startGalleryActivity();
                consumed = true;
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
