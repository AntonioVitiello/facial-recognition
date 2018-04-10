package av.demo.facereco;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private EditText mPasswordView;
    private EditText mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);

        initComponent();
    }

    private void initComponent(){
        mEmailView = findViewById(R.id.email_et);
        mPasswordView = findViewById(R.id.password_et);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                // If triggered by an enter key, this is the event; otherwise, this is null.
//                if (keyEvent != null) {
//                    // may be shift key is down, eg. if we want to insert the '\n' char in the TextView
//                    if (!keyEvent.isShiftPressed()) {
//                        attemptLogin();
//                        return true;
//                    }
//                }
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    Timber.d("onEditorAction: keyEvent = %s, actionId = %d", keyEvent, actionId);
                    hideSoftInput(textView);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    public void hideSoftInput(View view) {
        View focusedView = view != null ? view : getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void attemptLogin() {
        onClickReopen(null);
    }

    public void onClickReopen(View view) {
        Intent i = new Intent(this, PersonalDataActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }

}
