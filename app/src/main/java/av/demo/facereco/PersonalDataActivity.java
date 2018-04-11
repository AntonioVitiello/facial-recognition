package av.demo.facereco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import timber.log.Timber;

public class PersonalDataActivity extends AppCompatActivity {

    private static final String PERSONAL_DATA_FRAGMENT_TAG = "personal_data_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
        initComponents();

        if(savedInstanceState == null) {
            PersonalDataFragment personalDataFragment = PersonalDataFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_personal_data, personalDataFragment, PERSONAL_DATA_FRAGMENT_TAG)
                    .commit();
        }

    }

    private void initComponents() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.facrec, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean consumed = false;
        switch (item.getItemId()) {
            case R.id.gallery_mi: {
                Intent i = new Intent(this, GalleryActivity.class);
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
