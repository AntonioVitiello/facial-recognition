package av.demo.facereco;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

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
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
        Timber.d("AAA onBackPressed: " );
    }
}
